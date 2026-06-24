package com.boticaestrella.servicio;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boticaestrella.dto.DetalleVentaClienteDTO;
import com.boticaestrella.dto.ItemCarritoDTO;
import com.boticaestrella.dto.PedidoClienteRequestDTO;
import com.boticaestrella.dto.PedidoClienteResponseDTO;
import com.boticaestrella.modelo.DetalleVentaCliente;
import com.boticaestrella.modelo.MovimientoKardex;
import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.VentaCliente;
import com.boticaestrella.patrones.observer.GestorStock;
import com.boticaestrella.repository.DetalleVentaClienteRepository;
import com.boticaestrella.repository.KardexRepository;
import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.VentaClienteRepository;
// 🔥 Importamos el repositorio de las Series
import com.boticaestrella.repository.SeriesRepository;

@Service
public class ServicioVentaCliente {

    private final VentaClienteRepository ventaClienteRepository;
    private final DetalleVentaClienteRepository detalleVentaClienteRepository;
    private final ProductoRepository productoRepository;
       // 🌟 NUEVAS DEPENDENCIAS PARA EL KARDEX Y STOCK
    private final KardexRepository kardexRepository;
    private final GestorStock gestorStock;
    // 🔥 Inyectamos el repositorio de las Series
    private final SeriesRepository seriesRepository;

    public ServicioVentaCliente(VentaClienteRepository ventaClienteRepository,
                                 DetalleVentaClienteRepository detalleVentaClienteRepository,
                                 ProductoRepository productoRepository,
                                 KardexRepository kardexRepository,
                                 GestorStock gestorStock,
                                 SeriesRepository seriesRepository) { // 🔥 Añadido al constructor
        this.ventaClienteRepository = ventaClienteRepository;
        this.detalleVentaClienteRepository = detalleVentaClienteRepository;
        this.productoRepository = productoRepository;
        this.kardexRepository = kardexRepository;
        this.gestorStock = gestorStock;
        this.seriesRepository = seriesRepository;
    }

    /**
     * CREAR UN NUEVO PEDIDO DESDE EL CARRITO
     */
    @Transactional(rollbackFor = Exception.class)
    public PedidoClienteResponseDTO crearPedido(PedidoClienteRequestDTO request) throws Exception {
        try {
            if (request.items() == null || request.items().isEmpty()) {
                throw new Exception("El pedido debe contener al menos un producto");
            }

            double subtotal = 0.0;
            for (ItemCarritoDTO item : request.items()) {
                subtotal += item.precioUnitario() * item.cantidad();
            }

            double costoEnvio = request.costoEnvio() != 0 ? request.costoEnvio() : 0.0;
            double total = subtotal + costoEnvio;

            String nroPedido = generarNumeroPedido();

            VentaCliente ventaCliente = new VentaCliente(
                    request.idUsuario(), nroPedido, "PENDIENTE", LocalDateTime.now(),
                    subtotal, costoEnvio, total, request.dniCliente(), request.nombreCliente(),
                    request.emailCliente(), request.telefonoCliente(), request.direccionEnvio(),
                    request.ciudad(), request.departamento(), request.tipoEnvio(), request.tipoPago()
            );

            ventaCliente.setApellidoCliente(request.apellidoCliente());
            ventaCliente.setNumeroCalle(request.numeroCalle());
            ventaCliente.setApartamento(request.apartamento());
            ventaCliente.setCodigoPostal(request.codigoPostal());
            ventaCliente.setTipoTarjeta(request.tipoTarjeta());
            ventaCliente.setUltimos4Digitos(request.ultimos4Digitos());
            ventaCliente.setBancoTarjeta(request.bancoTarjeta());
            ventaCliente.setNombreTitular(request.nombreTitular());
            ventaCliente.setObservaciones(request.observaciones());
            ventaCliente.setFechaEntregaEstimada(calcularFechaEntrega(request.tipoEnvio()));

            VentaCliente ventaGuardada = ventaClienteRepository.save(ventaCliente);

            List<DetalleVentaClienteDTO> detallesDTO = new ArrayList<>();
            for (ItemCarritoDTO item : request.items()) {
                DetalleVentaCliente detalle = new DetalleVentaCliente(
                        ventaGuardada.getIdVentaCliente(), item.idProducto(), item.cantidad(),
                        item.precioUnitario(), item.precioUnitario() * item.cantidad()
                );
                DetalleVentaCliente detalleGuardado = detalleVentaClienteRepository.save(detalle);

                // 🌟 1. BUSCAMOS EL PRODUCTO Y ACTUALIZAMOS SU STOCK GENERAL
                Producto producto = productoRepository.findById(item.idProducto())
                        .orElseThrow(() -> new Exception("Producto no encontrado en BD"));

                if (producto.getStockActual() < item.cantidad()) {
                    throw new Exception("Stock insuficiente en el backend para: " + producto.getNombre());
                }

                producto.setStockActual(producto.getStockActual() - item.cantidad());
                productoRepository.save(producto);

                // 🌟 NUEVO: ACTUALIZAMOS LA TABLA SERIES (Para que no haya desfase con el admin)
                List<com.boticaestrella.modelo.Series> seriesDisponibles = seriesRepository.findByIdProductoAndEstado(producto.getIdProducto(), "DISPONIBLE");
                
                if (seriesDisponibles.size() < item.cantidad()) {
                    throw new Exception("Desfase de stock físico: No hay suficientes series DISPONIBLES para " + producto.getNombre());
                }

                List<com.boticaestrella.modelo.Series> seriesAAsignar = new ArrayList<>();
                for (int i = 0; i < item.cantidad(); i++) {
                    com.boticaestrella.modelo.Series s = seriesDisponibles.get(i);
                    s.setEstado("ASIGNADO"); // Lo apartamos de la tienda física
                    seriesAAsignar.add(s);
                }
                seriesRepository.saveAll(seriesAAsignar); // Guardamos el cambio de estado masivo
                // -------------------------------------------------------------

                // 🌟 2. REGISTRAMOS EL MOVIMIENTO EN EL KARDEX
                MovimientoKardex kardex = new MovimientoKardex();
                kardex.setIdProducto(producto.getIdProducto());
                kardex.setTipoMovimiento("SALIDA");
                kardex.setCantidad(item.cantidad());
                kardex.setMotivo("Pedido Web: " + ventaGuardada.getNroPedido());
                kardex.setIdUsuario(request.idUsuario());
                kardex.setFecha(new Timestamp(System.currentTimeMillis()));
                kardexRepository.save(kardex);

                // 🌟 3. DISPARAMOS ALERTA DE STOCK CRÍTICO (Patrón Observer)
                if (producto.getStockActual() <= producto.getStockMinimo()) {
                    gestorStock.dispararAlertaStockCritico(producto.getCodigoSKU(), producto.getStockActual());
                }

                detallesDTO.add(new DetalleVentaClienteDTO(
                        detalleGuardado.getIdDetalle(), item.idProducto(), producto.getNombre(),
                        item.cantidad(), item.precioUnitario(), detalleGuardado.getSubtotal(), 0.0
                ));
            }

            PedidoClienteResponseDTO response = new PedidoClienteResponseDTO(
                    ventaGuardada.getIdVentaCliente(), ventaGuardada.getNroPedido(), ventaGuardada.getEstado(),
                    ventaGuardada.getFechaPedido(), ventaGuardada.getFechaEntregaEstimada(), ventaGuardada.getSubtotal(),
                    ventaGuardada.getCostoEnvio(), ventaGuardada.getTotal(), ventaGuardada.getNombreCliente(),
                    ventaGuardada.getEmailCliente(), ventaGuardada.getTelefonoCliente(), ventaGuardada.getDireccionEnvio(),
                    ventaGuardada.getCiudad(), ventaGuardada.getDepartamento(), ventaGuardada.getTipoEnvio(),
                    ventaGuardada.getNumeroSeguimiento(), ventaGuardada.getTipoPago(), ventaGuardada.getTipoTarjeta(),
                    ventaGuardada.getUltimos4Digitos()
            );
            response.setDetalles(detallesDTO);

            return response;

        } catch (Exception e) {
            throw new Exception("Error al crear el pedido: " + e.getMessage());
        }
    }

    /**
     * OBTENER PEDIDO POR ID
     */
    public PedidoClienteResponseDTO obtenerPedidoPorId(int idVentaCliente) throws Exception {
        VentaCliente venta = ventaClienteRepository.findById(idVentaCliente)
                .orElseThrow(() -> new Exception("Pedido no encontrado"));

        List<DetalleVentaCliente> detalles = detalleVentaClienteRepository.findByIdVentaCliente(idVentaCliente);

        List<DetalleVentaClienteDTO> detallesDTO = detalles.stream().map(d -> {
            Optional<Producto> producto = productoRepository.findById(d.getIdProducto());
            String nombreProducto = producto.map(Producto::getNombre).orElse("Producto");
            return new DetalleVentaClienteDTO(
                    d.getIdDetalle(), d.getIdProducto(), nombreProducto, d.getCantidad(),
                    d.getPrecioUnitario(), d.getSubtotal(), d.getDescuento()
            );
        }).collect(Collectors.toList());

        PedidoClienteResponseDTO response = new PedidoClienteResponseDTO(
                venta.getIdVentaCliente(), venta.getNroPedido(), venta.getEstado(),
                venta.getFechaPedido(), venta.getFechaEntregaEstimada(), venta.getSubtotal(),
                venta.getCostoEnvio(), venta.getTotal(), venta.getNombreCliente(),
                venta.getEmailCliente(), venta.getTelefonoCliente(), venta.getDireccionEnvio(),
                venta.getCiudad(), venta.getDepartamento(), venta.getTipoEnvio(),
                venta.getNumeroSeguimiento(), venta.getTipoPago(), venta.getTipoTarjeta(),
                venta.getUltimos4Digitos()
        );
        response.setDetalles(detallesDTO);
        return response;
    }

    /**
     * OBTENER TODOS LOS PEDIDOS DE UN CLIENTE
     */
    public List<PedidoClienteResponseDTO> obtenerPedidosCliente(int idUsuario) {
        List<VentaCliente> ventas = ventaClienteRepository.findByIdUsuario(idUsuario);

        return ventas.stream().map(venta -> {
            List<DetalleVentaCliente> detalles = detalleVentaClienteRepository.findByIdVentaCliente(venta.getIdVentaCliente());

            List<DetalleVentaClienteDTO> detallesDTO = detalles.stream().map(d -> {
                Optional<Producto> producto = productoRepository.findById(d.getIdProducto());
                String nombreProducto = producto.map(Producto::getNombre).orElse("Producto");
                return new DetalleVentaClienteDTO(
                        d.getIdDetalle(), d.getIdProducto(), nombreProducto, d.getCantidad(),
                        d.getPrecioUnitario(), d.getSubtotal(), d.getDescuento()
                );
            }).collect(Collectors.toList());

            PedidoClienteResponseDTO response = new PedidoClienteResponseDTO(
                    venta.getIdVentaCliente(), venta.getNroPedido(), venta.getEstado(),
                    venta.getFechaPedido(), venta.getFechaEntregaEstimada(), venta.getSubtotal(),
                    venta.getCostoEnvio(), venta.getTotal(), venta.getNombreCliente(),
                    venta.getEmailCliente(), venta.getTelefonoCliente(), venta.getDireccionEnvio(),
                    venta.getCiudad(), venta.getDepartamento(), venta.getTipoEnvio(),
                    venta.getNumeroSeguimiento(), venta.getTipoPago(), venta.getTipoTarjeta(),
                    venta.getUltimos4Digitos()
            );
            response.setDetalles(detallesDTO);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * ACTUALIZAR ESTADO DEL PEDIDO
     */
    @Transactional(rollbackFor = Exception.class)
    public PedidoClienteResponseDTO actualizarEstadoPedido(int idVentaCliente, String nuevoEstado, String numeroSeguimiento) throws Exception {
        VentaCliente venta = ventaClienteRepository.findById(idVentaCliente)
                .orElseThrow(() -> new Exception("Pedido no encontrado"));

        venta.setEstado(nuevoEstado);
        if (numeroSeguimiento != null && !numeroSeguimiento.isEmpty()) {
            venta.setNumeroSeguimiento(numeroSeguimiento);
        }

        if ("ENTREGADO".equals(nuevoEstado)) {
            venta.setFechaEntregaReal(LocalDateTime.now());
        }

        VentaCliente ventaActualizada = ventaClienteRepository.save(venta);

        return obtenerPedidoPorId(ventaActualizada.getIdVentaCliente());
    }

    /**
     * CANCELAR PEDIDO
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelarPedido(int idVentaCliente) throws Exception {
        VentaCliente venta = ventaClienteRepository.findById(idVentaCliente)
                .orElseThrow(() -> new Exception("Pedido no encontrado"));

        if ("ENVIADO".equals(venta.getEstado()) || "ENTREGADO".equals(venta.getEstado())) {
            throw new Exception("No se puede cancelar un pedido ya enviado o entregado");
        }

        // 🌟 DEVOLVER EL STOCK SI EL PEDIDO SE CANCELA
        List<DetalleVentaCliente> detalles = detalleVentaClienteRepository.findByIdVentaCliente(idVentaCliente);
        for (DetalleVentaCliente detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getIdProducto()).orElse(null);
            if (producto != null) {
                
                // 1. Devolvemos el stock general
                producto.setStockActual(producto.getStockActual() + detalle.getCantidad());
                productoRepository.save(producto);

                // 🌟 NUEVO: Devolvemos el estado DISPONIBLE a las cajas físicas en la tabla SERIES
                List<com.boticaestrella.modelo.Series> seriesAsignadas = seriesRepository.findByIdProductoAndEstado(producto.getIdProducto(), "ASIGNADO");
                List<com.boticaestrella.modelo.Series> seriesALiberar = new ArrayList<>();
                for (int i = 0; i < detalle.getCantidad() && i < seriesAsignadas.size(); i++) {
                    com.boticaestrella.modelo.Series s = seriesAsignadas.get(i);
                    s.setEstado("DISPONIBLE"); // Volvemos a colocar la serie en vitrina
                    seriesALiberar.add(s);
                }
                seriesRepository.saveAll(seriesALiberar);
                // --------------------------------------------------------------------------

                // Registramos el ingreso por cancelación en el Kardex
                MovimientoKardex kardex = new MovimientoKardex();
                kardex.setIdProducto(producto.getIdProducto());
                kardex.setTipoMovimiento("INGRESO");
                kardex.setCantidad(detalle.getCantidad());
                kardex.setMotivo("Cancelación Pedido Web: " + venta.getNroPedido());
                kardex.setIdUsuario(venta.getIdUsuario());
                kardex.setFecha(new Timestamp(System.currentTimeMillis()));
                kardexRepository.save(kardex);
            }
        }

        venta.setEstado("CANCELADO");
        ventaClienteRepository.save(venta);
    }

    public List<PedidoClienteResponseDTO> obtenerPedidosPorEstado(String estado) {
        List<VentaCliente> ventas = ventaClienteRepository.findByEstado(estado);
        return ventas.stream().map(venta -> {
            try {
                return obtenerPedidoPorId(venta.getIdVentaCliente());
            } catch (Exception e) {
                return null;
            }
        }).filter(p -> p != null).collect(Collectors.toList());
    }

    private String generarNumeroPedido() {
        long numero = ventaClienteRepository.countTotalVentas() + 1;
        return String.format("PED-%tY-%05d", LocalDateTime.now(), numero);
    }

    private LocalDate calcularFechaEntrega(String tipoEnvio) {
        LocalDate today = LocalDate.now();
        switch (tipoEnvio) {
            case "EXPRESS":
                return today.plusDays(2); 
            case "SAME_DAY":
                return today.plusDays(1); 
            default: 
                return today.plusDays(5); 
        }
    }

    public List<PedidoClienteResponseDTO> obtenerTodosLosPedidos() {
        List<VentaCliente> ventas = ventaClienteRepository.findAll();
        
        // Ordenamos para que los pedidos más recientes salgan primero
        ventas.sort((a, b) -> b.getFechaPedido().compareTo(a.getFechaPedido()));

        return ventas.stream().map(venta -> {
            try {
                return obtenerPedidoPorId(venta.getIdVentaCliente());
            } catch (Exception e) {
                return null;
            }
        }).filter(p -> p != null).collect(Collectors.toList());
    }
}