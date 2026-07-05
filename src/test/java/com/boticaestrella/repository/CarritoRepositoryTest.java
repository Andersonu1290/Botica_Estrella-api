package com.boticaestrella.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import com.boticaestrella.modelo.CarritoItem;
import com.boticaestrella.modelo.Categoria;
import com.boticaestrella.modelo.Producto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarritoRepositoryTest {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void findAndDeleteByUsuario_workAsExpected() {
        Producto producto1 = productoRepository.save(crearProducto("SKU-CAR-1-" + UUID.randomUUID(), "Producto Carrito 1"));
        Producto producto2 = productoRepository.save(crearProducto("SKU-CAR-2-" + UUID.randomUUID(), "Producto Carrito 2"));

        carritoRepository.save(crearItem(12, producto1.getIdProducto(), 2));
        carritoRepository.save(crearItem(12, producto2.getIdProducto(), 1));
        carritoRepository.save(crearItem(13, producto1.getIdProducto(), 5));

        List<CarritoItem> items = carritoRepository.findByIdUsuario(12);
        assertEquals(2, items.size());
        assertTrue(carritoRepository.findByIdUsuarioAndIdProducto(12, producto1.getIdProducto()).isPresent());

        carritoRepository.deleteByIdUsuario(12);

        assertEquals(0, carritoRepository.findByIdUsuario(12).size());
        assertEquals(1, carritoRepository.findByIdUsuario(13).size());
    }

    private Producto crearProducto(String sku, String nombre) {
        Categoria categoria = categoriaRepository.save(crearCategoria("Accesorios-" + UUID.randomUUID()));
        Producto producto = new Producto();
        producto.setIdCategoria(categoria.getIdCategoria());
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        producto.setStockActual(10);
        producto.setStockMinimo(1);
        producto.setPrecio(6.0);
        return producto;
    }

    private Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    private CarritoItem crearItem(int idUsuario, int idProducto, int cantidad) {
        CarritoItem item = new CarritoItem();
        item.setIdUsuario(idUsuario);
        item.setIdProducto(idProducto);
        item.setCantidad(cantidad);
        return item;
    }
}