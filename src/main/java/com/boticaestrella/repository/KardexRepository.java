package com.boticaestrella.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.boticaestrella.modelo.MovimientoKardex;

@Repository
public interface KardexRepository extends JpaRepository<MovimientoKardex, Integer> {

    @Query(value = """
        SELECT 
            k.id_movimiento,
            k.tipo_movimiento,
            k.cantidad,
            k.fecha,
            k.motivo,
            p.nombre AS nombreProducto,
            u.username AS nombreUsuario
        FROM kardex_movimientos k
        INNER JOIN productos p 
            ON k.id_producto = p.id_producto
        INNER JOIN usuarios u 
            ON k.id_usuario = u.id_usuario
        ORDER BY k.fecha DESC
        """, nativeQuery = true)
    List<Object[]> listarHistorialKardex();
}