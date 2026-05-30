package com.boticaestrella.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.boticaestrella.modelo.CarritoItem;
import com.boticaestrella.repository.CarritoRepository;
import com.boticaestrella.repository.ProductoRepository;

@Service
public class ServicioCarrito {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    public ServicioCarrito(CarritoRepository carritoRepository, ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    public List<CarritoItem> obtenerCarritoDeUsuario(int idUsuario) {
        List<CarritoItem> items = carritoRepository.findByIdUsuario(idUsuario);
        // Llenamos el objeto Producto para que Vue pueda pintar nombres, fotos y precios
        for (CarritoItem item : items) {
            productoRepository.findById(item.getIdProducto()).ifPresent(item::setProducto);
        }
        return items;
    }

    public void agregarOActualizarItem(int idUsuario, int idProducto, int cantidadAñadida) {
        Optional<CarritoItem> existente = carritoRepository.findByIdUsuarioAndIdProducto(idUsuario, idProducto);
        
        if (existente.isPresent()) {
            CarritoItem item = existente.get();
            item.setCantidad(item.getCantidad() + cantidadAñadida);
            carritoRepository.save(item);
        } else {
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setIdUsuario(idUsuario);
            nuevoItem.setIdProducto(idProducto);
            nuevoItem.setCantidad(cantidadAñadida);
            carritoRepository.save(nuevoItem);
        }
    }

    public void eliminarItem(int idItem) {
        carritoRepository.deleteById(idItem);
    }

    public void vaciarCarrito(int idUsuario) {
        carritoRepository.deleteByIdUsuario(idUsuario);
    }
}