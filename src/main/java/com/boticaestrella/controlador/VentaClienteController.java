package com.boticaestrella.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boticaestrella.dto.PedidoClienteRequestDTO;
import com.boticaestrella.dto.PedidoClienteResponseDTO;
import com.boticaestrella.servicio.ServicioVentaCliente;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "*")
public class VentaClienteController {

    private final ServicioVentaCliente servicioVentaCliente;

    public VentaClienteController(ServicioVentaCliente servicioVentaCliente) {
        this.servicioVentaCliente = servicioVentaCliente;
    }

    /**
     * CREAR NUEVO PEDIDO DESDE CARRITO
     * POST /api/v1/pedidos
     */
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoClienteRequestDTO request) {
        try {
            PedidoClienteResponseDTO pedido = servicioVentaCliente.crearPedido(request);
            return new ResponseEntity<>(
                    Map.of(
                            "success", true,
                            "mensaje", "Pedido creado exitosamente",
                            "pedido", pedido
                    ),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("success", false, "error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * OBTENER PEDIDO POR ID
     * GET /api/v1/pedidos/{idVentaCliente}
     */
    @GetMapping("/{idVentaCliente}")
    public ResponseEntity<?> obtenerPedido(@PathVariable int idVentaCliente) {
        try {
            PedidoClienteResponseDTO pedido = servicioVentaCliente.obtenerPedidoPorId(idVentaCliente);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("success", false, "error", e.getMessage()),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * OBTENER TODOS LOS PEDIDOS DE UN CLIENTE
     * GET /api/v1/pedidos/cliente/{idUsuario}
     */
    @GetMapping("/cliente/{idUsuario}")
    public ResponseEntity<?> obtenerPedidosCliente(@PathVariable int idUsuario) {
        try {
            List<PedidoClienteResponseDTO> pedidos = servicioVentaCliente.obtenerPedidosCliente(idUsuario);
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "total", pedidos.size(),
                            "pedidos", pedidos
                    )
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("success", false, "error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * OBTENER PEDIDOS POR ESTADO (Para admin)
     * GET /api/v1/pedidos/estado/{estado}
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPedidosPorEstado(@PathVariable String estado) {
        try {
            List<PedidoClienteResponseDTO> pedidos = servicioVentaCliente.obtenerPedidosPorEstado(estado);
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "total", pedidos.size(),
                            "pedidos", pedidos
                    )
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("success", false, "error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * OBTENER TODOS LOS PEDIDOS (Para Admin)
     * GET /api/v1/pedidos/todos
     */
    @GetMapping("/admin/todos")
    public ResponseEntity<?> obtenerTodosLosPedidos() {
        try {
            List<PedidoClienteResponseDTO> pedidos = servicioVentaCliente.obtenerTodosLosPedidos();
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "total", pedidos.size(),
                            "pedidos", pedidos
                    )
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("success", false, "error", e.getMessage()),
                    org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * ACTUALIZAR ESTADO DEL PEDIDO (Para admin)
     * PUT /api/v1/pedidos/{idVentaCliente}/estado
     */
    @PutMapping("/{idVentaCliente}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable int idVentaCliente,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String numeroSeguimiento) {
        try {
            PedidoClienteResponseDTO pedido = servicioVentaCliente.actualizarEstadoPedido(
                    idVentaCliente, nuevoEstado, numeroSeguimiento
            );
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "mensaje", "Estado actualizado correctamente",
                            "pedido", pedido
                    )
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("success", false, "error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * CANCELAR PEDIDO
     * PUT /api/v1/pedidos/{idVentaCliente}/cancelar
     */
    @PutMapping("/{idVentaCliente}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable int idVentaCliente) {
        try {
            servicioVentaCliente.cancelarPedido(idVentaCliente);
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "mensaje", "Pedido cancelado correctamente"
                    )
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("success", false, "error", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}

