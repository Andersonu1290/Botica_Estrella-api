package com.boticaestrella.patrones.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ComprobanteFactoryTest {

    private ComprobanteFactory comprobanteFactory;

    @BeforeEach
    void setUp() {
        // Simulamos el Map que inyecta Spring automáticamente
        Map<String, IComprobante> comprobantes = Map.of(
            "boleta", new Boleta(),
            "factura", new Factura()
        );
        comprobanteFactory = new ComprobanteFactory(comprobantes);
    }

    @Test
    void debeRetornarBoletaCuandoSeSolicita() {
        IComprobante resultado = comprobanteFactory.crearComprobante("boleta");
        
        assertNotNull(resultado);
        assertTrue(resultado instanceof Boleta);
        assertTrue(resultado.generar("TEST").contains("BOLETA EMITIDA"));
    }

    @Test
    void debeRetornarFacturaCuandoSeSolicitaConEspaciosYMayusculas() {
        // Tu código usa trim() y toLowerCase(), ¡hay que probarlo!
        IComprobante resultado = comprobanteFactory.crearComprobante("  FACTURA  ");
        
        assertNotNull(resultado);
        assertTrue(resultado instanceof Factura);
        assertTrue(resultado.generar("TEST").contains("FACTURA EMITIDA"));
    }

    @Test
    void debeRetornarNullSiElTipoNoExiste() {
        IComprobante resultado = comprobanteFactory.crearComprobante("ticket_simple");
        assertNull(resultado);
    }
}