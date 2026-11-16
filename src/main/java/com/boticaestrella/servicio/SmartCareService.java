package com.boticaestrella.servicio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.boticaestrella.dto.OcrResponseDTO;
import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.TomaTratamiento;
import com.boticaestrella.modelo.TratamientoSmartCare;
import com.boticaestrella.repository.DetalleVentaClienteRepository;
import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.TomaTratamientoRepository;
import com.boticaestrella.repository.TratamientoSmartCareRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SmartCareService {

    private static final List<String> MODELOS = List.of(
            "gemini-3.8-flash",       // principal
            "gemini-3.6-flash",       // respaldo 1
            "gemini-3.5-flash-lite"   // respaldo 2
    );

    private static final int MAX_INTENTOS_POR_MODELO = 3;

    private final TratamientoSmartCareRepository smartCareRepository;
    private final TomaTratamientoRepository tomaTratamientoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public SmartCareService(TratamientoSmartCareRepository smartCareRepository, 
                            TomaTratamientoRepository tomaTratamientoRepository) {
        this.smartCareRepository = smartCareRepository;
        this.tomaTratamientoRepository = tomaTratamientoRepository;
    }

    // =====================================================================
    // OCR con Gemini
    // =====================================================================
    public OcrResponseDTO procesarOcr(MultipartFile imagenReceta) throws Exception {
        if (imagenReceta == null || imagenReceta.isEmpty()) {
            throw new Exception("La imagen de la receta es obligatoria.");
        }

        String base64Image = Base64.getEncoder().encodeToString(imagenReceta.getBytes());
        String mimeType = imagenReceta.getContentType();

        if (mimeType == null || !mimeType.startsWith("image/")) {
            mimeType = "image/jpeg";
        }
        mimeType = mimeType.split(";")[0]; 

        // Nuevo Schema incluyendo cantidadPorToma y unidadMedida
        String requestBody = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "Analiza esta receta médica manuscrita. Extrae los datos del PRIMER medicamento. Si no encuentras un valor numérico, usa 0. No uses markdown."
                },
                {
                  "inlineData": {
                    "mimeType": "%s",
                    "data": "%s"
                  }
                }
              ]
            }
          ],
          "generationConfig": {
            "responseMimeType": "application/json",
            "responseSchema": {
              "type": "OBJECT",
              "properties": {
                "nombreMedicamento": { "type": "STRING", "description": "Nombre del fármaco recetado" },
                "dosis": { "type": "STRING", "description": "Concentración del medicamento, ej: 500 mg" },
                "unidadMedida": { "type": "STRING", "description": "Unidad de consumo (ej: tableta, cápsula, ml, cucharada)" },
                "cantidadPorToma": { "type": "INTEGER", "description": "Cuánto se debe consumir en cada toma (ej: 1, 2, 5). Solo número entero." },
                "frecuenciaHoras": { "type": "INTEGER", "description": "Frecuencia en horas (solo el número entero). Ej: si dice c/8h pon 8" },
                "cantidad": { "type": "INTEGER", "description": "Cantidad total a comprar del tratamiento (solo el número entero)" },
                "textoCrudo": { "type": "STRING", "description": "Texto literal detectado en la receta" }
              },
              "required": ["nombreMedicamento", "dosis", "unidadMedida", "cantidadPorToma", "frecuenciaHoras", "cantidad", "textoCrudo"]
            }
          }
        }
        """.formatted(mimeType, base64Image);

        HttpResponse<String> response = llamarGemini(requestBody);
        int status = response.statusCode();

        if (status != 200) {
            if (status == 503 || status == 429 || status == 500) {
                throw new Exception("El servicio de lectura de recetas está saturado. Intenta de nuevo en unos minutos.");
            }
            throw new Exception("Rechazo de Google IA (HTTP " + status + "): " + response.body());
        }

        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode candidates = rootNode.path("candidates");

        if (!candidates.isArray() || candidates.isEmpty()) {
            throw new Exception("La IA no devolvió resultados. Intenta con una foto más clara de la receta.");
        }

        String jsonExtraido = candidates.get(0)
                .path("content").path("parts").path(0)
                .path("text").asText("");

        jsonExtraido = limpiarJson(jsonExtraido);

        JsonNode datosMedicos;
        try {
            datosMedicos = objectMapper.readTree(jsonExtraido);
        } catch (Exception e) {
            throw new Exception("No se pudo interpretar la respuesta de la IA. Intenta con otra foto.");
        }

        return new OcrResponseDTO(
                datosMedicos.path("nombreMedicamento").asText("Desconocido"),
                datosMedicos.path("dosis").asText("No especificada"),
                datosMedicos.path("unidadMedida").asText("unidad"),
                datosMedicos.path("cantidadPorToma").asInt(1),
                datosMedicos.path("frecuenciaHoras").asInt(0),
                datosMedicos.path("cantidad").asInt(0),
                datosMedicos.path("textoCrudo").asText(""),
                true
        );
    }

    private HttpResponse<String> llamarGemini(String requestBody) throws Exception {
        HttpResponse<String> response = null;
        HttpResponse<String> ultimaTemporal = null;

        for (String modelo : MODELOS) {
            for (int intento = 1; intento <= MAX_INTENTOS_POR_MODELO; intento++) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + modelo + ":generateContent"))
                        .header("Content-Type", "application/json")
                        .header("x-goog-api-key", geminiApiKey) 
                        .timeout(Duration.ofSeconds(60))
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                try {
                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                } catch (Exception e) {
                    if (intento < MAX_INTENTOS_POR_MODELO) {
                        Thread.sleep(1000L * intento);
                        continue;
                    }
                    break; 
                }

                int status = response.statusCode();
                if (status == 200) return response;
                if (status == 404) break;

                boolean temporal = status == 503 || status == 429 || status == 500;
                if (!temporal) return response;

                ultimaTemporal = response;
                if (intento < MAX_INTENTOS_POR_MODELO) {
                    Thread.sleep(1000L * intento); 
                }
            }
        }

        if (ultimaTemporal != null) return ultimaTemporal; 
        if (response == null) throw new Exception("No se pudo conectar con el servicio de IA. Revisa tu conexión a internet.");
        
        return response;
    }

    private String limpiarJson(String texto) {
        if (texto == null) return "{}";
        String t = texto.trim();
        if (t.startsWith("```")) {
            t = t.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
        }
        return t.isEmpty() ? "{}" : t;
    }

    // =====================================================================
    // Registro y Cronograma de Tratamientos (Core Lógico)
    // =====================================================================
    @Transactional(rollbackFor = Exception.class)
    public TratamientoSmartCare registrarTratamiento(TratamientoSmartCare tratamiento) throws Exception {

        if (!tratamiento.isValidadoPorUsuario()) {
            throw new Exception("Seguridad Clínica: El paciente debe confirmar manualmente que los datos extraídos coinciden con la receta física.");
        }

        // =========================================================
        // NUEVO: BLOQUEO MATEMÁTICO POR FALTA DE STOCK FÍSICO
        // =========================================================
        if (tratamiento.getIdProducto() != null) {
            int stockLibre = calcularStockRealComprado(tratamiento.getIdUsuario(), tratamiento.getIdProducto());
            
            if (tratamiento.getCantidadInicial() > stockLibre) {
                throw new Exception("STOCK INSUFICIENTE: Intentas registrar un tratamiento de " 
                        + tratamiento.getCantidadInicial() + " unidades, pero solo tienes " 
                        + stockLibre + " pastillas libres en tu Botiquín. ¡Debes comprar más antes de iniciar este tratamiento!");
            }
        }

        // Prevención de división por cero
        int cantidadToma = Math.max(1, tratamiento.getCantidadPorToma());
        int frecuencia = Math.max(1, tratamiento.getFrecuenciaHoras());

        // 1. Cálculo matemático de duraciones
        int totalTomasPosibles = tratamiento.getCantidadInicial() / cantidadToma;
        double tomasPorDia = 24.0 / frecuencia;
        int diasDuracionTotal = (int) (totalTomasPosibles / tomasPorDia);

        LocalDate fechaAgotamiento = tratamiento.getFechaInicio().plusDays(diasDuracionTotal);
        tratamiento.setFechaAgotamiento(fechaAgotamiento);

        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaAgotamiento);
        tratamiento.setDiasRestantes((int) Math.max(0, diasRestantes));
        tratamiento.setStockActual(tratamiento.getCantidadInicial());

        // 2. Persistir tratamiento para obtener su ID Primario
        TratamientoSmartCare guardado = smartCareRepository.save(tratamiento);

        // 3. Creación del cronograma estricto en la tabla 'tomas_tratamiento'
        List<TomaTratamiento> cronograma = new ArrayList<>();
        LocalDateTime fechaHoraIterativa = LocalDateTime.of(
            tratamiento.getFechaInicio(), 
            LocalTime.parse(tratamiento.getHoraPrimeraDosis())
        );

        for (int i = 0; i < totalTomasPosibles; i++) {
            TomaTratamiento toma = new TomaTratamiento();
            toma.setIdTratamiento(guardado.getIdTratamiento());
            toma.setFechaHoraProgramada(fechaHoraIterativa);
            toma.setEstado("PENDIENTE");
            
            cronograma.add(toma);
            
            // Avanzar el reloj para la siguiente toma
            fechaHoraIterativa = fechaHoraIterativa.plusHours(frecuencia);
        }

        tomaTratamientoRepository.saveAll(cronograma);

        return guardado;
    }

    public List<TratamientoSmartCare> listarHistorialPaciente(int idUsuario) {
        // AHORA trae Activos y Finalizados para que Vue.js arme las pestañas
        List<TratamientoSmartCare> tratamientos = smartCareRepository.findByIdUsuario(idUsuario);
        LocalDate hoy = LocalDate.now();

        for (TratamientoSmartCare t : tratamientos) {
            long diasRestantesCalculados = ChronoUnit.DAYS.between(hoy, t.getFechaAgotamiento());
            t.setDiasRestantes((int) Math.max(0, diasRestantesCalculados));
            // Solo es crítico si aún sigue ACTIVO
            t.setAlertaCritica(diasRestantesCalculados <= 3 && "ACTIVO".equals(t.getEstado()));
        }

        return tratamientos;
    }

    // =====================================================================
    // FASE 2: GESTIÓN DE CICLO DE VIDA (CRUD) Y CONSUMO
    // =====================================================================

    // 1. Eliminar tratamiento de forma segura
    @Transactional(rollbackFor = Exception.class)
    public void eliminarTratamiento(int idTratamiento, int idUsuario) throws Exception {
        TratamientoSmartCare tratamiento = smartCareRepository.findById(idTratamiento)
                .orElseThrow(() -> new Exception("El tratamiento no existe."));

        if (tratamiento.getIdUsuario() != idUsuario) {
            throw new Exception("Acceso denegado. Este tratamiento no te pertenece.");
        }
        
        // ON DELETE CASCADE en MySQL borrará automáticamente todas las tomas (alarmas) de la tabla 'tomas_tratamiento'
        smartCareRepository.delete(tratamiento);
    }

    // 2. Registrar el consumo de un medicamento ("Tomé mi dosis")
    @Transactional(rollbackFor = Exception.class)
    public TratamientoSmartCare registrarToma(int idTratamiento, int idUsuario) throws Exception {
        TratamientoSmartCare tratamiento = smartCareRepository.findById(idTratamiento)
                .orElseThrow(() -> new Exception("El tratamiento no existe."));

        if (tratamiento.getIdUsuario() != idUsuario) {
            throw new Exception("Acceso denegado.");
        }

        if (tratamiento.getStockActual() < tratamiento.getCantidadPorToma()) {
            throw new Exception("No tienes suficiente stock en tu botiquín para registrar esta toma. ¡Compra más!");
        }

        // 1. Restamos la dosis que acaba de consumir del inventario del tratamiento
        tratamiento.setStockActual(tratamiento.getStockActual() - tratamiento.getCantidadPorToma());

        // 2. Buscamos la alarma (PENDIENTE) más antigua y la marcamos como "TOMADO"
        List<TomaTratamiento> tomas = tomaTratamientoRepository.findByIdTratamientoOrderByFechaHoraProgramadaAsc(idTratamiento);
        for (TomaTratamiento toma : tomas) {
            if ("PENDIENTE".equals(toma.getEstado())) {
                toma.setEstado("TOMADO");
                toma.setFechaHoraReal(LocalDateTime.now());
                tomaTratamientoRepository.save(toma);
                break; // Solo descontamos una toma a la vez
            }
        }

        // 3. MAGIA DE AUTO-FINALIZACIÓN: ¿Le alcanza para otra toma?
        if (tratamiento.getStockActual() < tratamiento.getCantidadPorToma()) {
            // No le alcanza. El tratamiento se acabó oficialmente.
            tratamiento.setEstado("FINALIZADO");
            tratamiento.setDiasRestantes(0);
            
            // Limpiamos cualquier alarma pendiente restante en la BD para evitar basura
            for (TomaTratamiento toma : tomas) {
                if ("PENDIENTE".equals(toma.getEstado())) {
                    tomaTratamientoRepository.delete(toma);
                }
            }
        } else {
            // 4. Si aún le queda medicina, recalculamos los días restantes de forma normal
            double tomasPorDia = 24.0 / (tratamiento.getFrecuenciaHoras() > 0 ? tratamiento.getFrecuenciaHoras() : 24);
            int diasDuracionRestante = (int) (tratamiento.getStockActual() / (tomasPorDia * tratamiento.getCantidadPorToma()));
            tratamiento.setFechaAgotamiento(LocalDate.now().plusDays(diasDuracionRestante));
            tratamiento.setDiasRestantes(diasDuracionRestante);
        }

        return smartCareRepository.save(tratamiento);
    }

    // 3. Modificar la pauta médica (Update)
    @Transactional(rollbackFor = Exception.class)
    public TratamientoSmartCare actualizarTratamiento(int idTratamiento, TratamientoSmartCare datosNuevos, int idUsuario) throws Exception {
        TratamientoSmartCare existente = smartCareRepository.findById(idTratamiento)
                .orElseThrow(() -> new Exception("El tratamiento no existe."));

        if (existente.getIdUsuario() != idUsuario) {
            throw new Exception("Acceso denegado.");
        }

        // Modificamos únicamente los datos de la pauta médica, NO la fecha de inicio ni cantidad comprada
        existente.setDosis(datosNuevos.getDosis());
        existente.setFrecuenciaHoras(datosNuevos.getFrecuenciaHoras());
        existente.setCantidadPorToma(datosNuevos.getCantidadPorToma());
        existente.setUnidadMedida(datosNuevos.getUnidadMedida());

        // Recalculamos cuántos días le va a durar el stock que le queda, basándonos en la nueva frecuencia médica
        double tomasPorDia = 24.0 / (existente.getFrecuenciaHoras() > 0 ? existente.getFrecuenciaHoras() : 24);
        int diasDuracionRestante = (int) (existente.getStockActual() / (tomasPorDia * existente.getCantidadPorToma()));
        
        existente.setFechaAgotamiento(LocalDate.now().plusDays(diasDuracionRestante));
        existente.setDiasRestantes(diasDuracionRestante);

        return smartCareRepository.save(existente);
    }

    // =====================================================================
    // BOTIQUÍN VIRTUAL (Stock real comprado por el paciente)
    // =====================================================================

    @Autowired
    private DetalleVentaClienteRepository detalleVentaRepository; 

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Calcula cuántas unidades reales tiene el paciente en su "Botiquín Virtual".
     * Lógica: Suma total de unidades compradas y entregadas - Unidades ya asignadas/consumidas en el tratamiento actual.
     */
    public int calcularStockRealComprado(int idUsuario, int idProducto) {
        // Obtenemos el dato crudo (Object) y lo convertimos a entero de forma segura
        Object compradoObj = detalleVentaRepository.sumCantidadByUsuarioAndProducto(idUsuario, idProducto);
        int totalComprado = (compradoObj != null) ? ((Number) compradoObj).intValue() : 0;

        Object asignadoObj = tomaTratamientoRepository.sumCantidadConsumidaByUsuarioAndProducto(idUsuario, idProducto);
        int totalAsignado = (asignadoObj != null) ? ((Number) asignadoObj).intValue() : 0;

        return totalComprado - totalAsignado;
    }

    @Transactional(rollbackFor = Exception.class)
    public TratamientoSmartCare finalizarTratamiento(int idTratamiento, int idUsuario) throws Exception {
        TratamientoSmartCare tratamiento = smartCareRepository.findById(idTratamiento)
                .orElseThrow(() -> new Exception("El tratamiento no existe."));

        if (tratamiento.getIdUsuario() != idUsuario) {
            throw new Exception("Acceso denegado.");
        }

        tratamiento.setEstado("FINALIZADO");
        
        // MAGIA DE INVENTARIO: Si finaliza el tratamiento, borramos las alarmas PENDIENTES.
        // Esto "libera" automáticamente las pastillas sobrantes y las devuelve al Botiquín Virtual.
        List<TomaTratamiento> tomas = tomaTratamientoRepository.findByIdTratamientoOrderByFechaHoraProgramadaAsc(idTratamiento);
        for (TomaTratamiento toma : tomas) {
            if ("PENDIENTE".equals(toma.getEstado())) {
                tomaTratamientoRepository.delete(toma);
            }
        }

        return smartCareRepository.save(tratamiento);
    }

    public List<Map<String, Object>> obtenerBotiquinPaciente(int idUsuario) {
        List<Object> idsProductos = detalleVentaRepository.findProductosCompradosByUsuario(idUsuario);
        List<Map<String, Object>> botiquin = new ArrayList<>();
        
        if (idsProductos == null || idsProductos.isEmpty()) {
            return botiquin;
        } 
        
        for (Object obj : idsProductos) {
            if (obj == null) continue;
            
            int idProd = ((Number) obj).intValue();
            Producto p = productoRepository.findById(idProd).orElse(null);
            
            if (p != null) {
                // 1. Total histórico comprado
                Object compradoObj = detalleVentaRepository.sumCantidadByUsuarioAndProducto(idUsuario, idProd);
                int totalComprado = (compradoObj != null) ? ((Number) compradoObj).intValue() : 0;

                // 2. Histórico consumido (Ya digerido)
                Object consumidoObj = tomaTratamientoRepository.sumCantidadConsumidaByUsuarioAndProducto(idUsuario, idProd);
                int totalConsumido = (consumidoObj != null) ? ((Number) consumidoObj).intValue() : 0;

                // 3. Reservado para el futuro (En el pastillero)
                Object reservadoObj = tomaTratamientoRepository.sumCantidadReservadaByUsuarioAndProducto(idUsuario, idProd);
                int totalReservado = (reservadoObj != null) ? ((Number) reservadoObj).intValue() : 0;

                // 4. Stock 100% Libre
                int stockLibre = totalComprado - (totalConsumido + totalReservado);
                
                Map<String, Object> item = new HashMap<>();
                item.put("idProducto", p.getIdProducto());
                item.put("codigoSKU", p.getCodigoSKU());
                item.put("nombre", p.getNombre());
                item.put("totalComprado", totalComprado);
                item.put("totalConsumido", totalConsumido); // <--- NUEVO
                item.put("totalReservado", totalReservado); // <--- NUEVO
                item.put("stockLibre", stockLibre);
                
                botiquin.add(item);
            }
        }
        
        return botiquin;
    }

    // =====================================================================
    // SISTEMA DE NOTIFICACIONES CON MATEMÁTICA AVANZADA (In-App)
    // =====================================================================
    public List<Map<String, Object>> obtenerNotificacionesPaciente(int idUsuario) {
        List<Map<String, Object>> notificaciones = new ArrayList<>();
        
        // Obtenemos solo los tratamientos ACTIVOS
        List<TratamientoSmartCare> tratamientos = smartCareRepository.findByIdUsuario(idUsuario)
                .stream().filter(t -> "ACTIVO".equals(t.getEstado())).toList();
        
        LocalDateTime ahora = LocalDateTime.now();

        for (TratamientoSmartCare t : tratamientos) {
            
            // ---------------------------------------------------------
            // LÓGICA 1: MATEMÁTICA DE PROYECCIÓN DE INVENTARIO
            // ---------------------------------------------------------
            // Calculamos exactamente cuántas unidades consume al día
            double tomasPorDia = 24.0 / (t.getFrecuenciaHoras() > 0 ? t.getFrecuenciaHoras() : 24);
            double consumoDiario = tomasPorDia * t.getCantidadPorToma();
            
            // Proyección exacta de días de cobertura restantes con decimales
            double diasCobertura = t.getStockActual() / consumoDiario;

            if (t.getStockActual() == 0) {
                Map<String, Object> notifAgotado = new HashMap<>();
                notifAgotado.put("id", "AGOTADO-" + t.getIdTratamiento());
                notifAgotado.put("tipo", "AGOTADO");
                notifAgotado.put("titulo", "Tratamiento interrumpido 🚨");
                notifAgotado.put("mensaje", "Te has quedado sin " + t.getNombreMedicamento() + ". Repón tu inventario de inmediato para no perder efectividad.");
                notifAgotado.put("fecha", ahora);
                notificaciones.add(notifAgotado);
            } else if (diasCobertura <= 3.0) {
                Map<String, Object> notifStock = new HashMap<>();
                notifStock.put("id", "STOCK-" + t.getIdTratamiento());
                notifStock.put("tipo", "ALERTA_STOCK");
                notifStock.put("titulo", "Alerta de Reposición 📦");
                int diasEnteros = (int) Math.floor(diasCobertura);
                notifStock.put("mensaje", "Solo te quedan " + t.getStockActual() + " unidades de " + t.getNombreMedicamento() + " (cobertura estimada para " + diasEnteros + " días).");
                notifStock.put("fecha", ahora);
                notificaciones.add(notifStock);
            }

            // ---------------------------------------------------------
            // LÓGICA 2: MATEMÁTICA DE INTERVALOS DE TIEMPO (TOMAS)
            // ---------------------------------------------------------
            List<TomaTratamiento> tomas = tomaTratamientoRepository.findByIdTratamientoOrderByFechaHoraProgramadaAsc(t.getIdTratamiento());
            
            for (TomaTratamiento toma : tomas) {
                if ("PENDIENTE".equals(toma.getEstado())) {
                    LocalDateTime programada = toma.getFechaHoraProgramada();
                    
                    // Calculamos la diferencia exacta en minutos entre AHORA y la HORA PROGRAMADA
                    long minutosDiferencia = java.time.Duration.between(ahora, programada).toMinutes();

                    if (minutosDiferencia < 0) {
                        // MATEMÁTICA: Si la diferencia es negativa, la hora ya pasó (ATRASO)
                        long minutosAtraso = Math.abs(minutosDiferencia);
                        long horasAtraso = minutosAtraso / 60;
                        long minRestantes = minutosAtraso % 60;
                        String tiempoStr = horasAtraso > 0 ? horasAtraso + "h " + minRestantes + "m" : minRestantes + " minutos";

                        Map<String, Object> notifAtraso = new HashMap<>();
                        notifAtraso.put("id", "ATRASO-" + toma.getIdToma());
                        notifAtraso.put("tipo", "DOSIS_ATRASADA");
                        notifAtraso.put("titulo", "Dosis Atrasada ⚠️");
                        notifAtraso.put("mensaje", "Debiste tomar " + t.getNombreMedicamento() + " hace " + tiempoStr + ". Regístrala si ya la tomaste.");
                        notifAtraso.put("fecha", programada);
                        notificaciones.add(notifAtraso);
                        
                    } else if (minutosDiferencia <= 120) { 
                        // MATEMÁTICA: Si la diferencia es positiva pero menor a 120 min, falta poco (CUENTA REGRESIVA)
                        long horasFaltan = minutosDiferencia / 60;
                        long minFaltan = minutosDiferencia % 60;
                        String tiempoStr = horasFaltan > 0 ? horasFaltan + "h " + minFaltan + "m" : minFaltan + " minutos";

                        Map<String, Object> notifProxima = new HashMap<>();
                        notifProxima.put("id", "PROXIMA-" + toma.getIdToma());
                        notifProxima.put("tipo", "DOSIS_PROXIMA");
                        notifProxima.put("titulo", "Próxima dosis en " + tiempoStr + " ⏱️");
                        notifProxima.put("mensaje", "Prepárate para tomar " + t.getCantidadPorToma() + " " + t.getUnidadMedida() + " de " + t.getNombreMedicamento() + ".");
                        notifProxima.put("fecha", programada);
                        notificaciones.add(notifProxima);
                    }
                    // IMPORTANTE: Un 'break' porque el paciente solo se preocupa por la dosis más inminente, no por las de mañana
                    break; 
                }
            }
        }
        
        // Ordenamos las notificaciones: las más recientes u urgentes primero
        notificaciones.sort((n1, n2) -> ((LocalDateTime) n2.get("fecha")).compareTo((LocalDateTime) n1.get("fecha")));
        
        return notificaciones;
    }
}