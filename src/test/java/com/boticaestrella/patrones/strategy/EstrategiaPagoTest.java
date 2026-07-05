package com.boticaestrella.patrones.strategy;

import com.boticaestrella.patrones.adapter.IServicioPago;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstrategiaPagoTest {

    // --- PRUEBA PAGO TARJETA (Usa Mockito por dependencia) ---
    @Mock
    private IServicioPago servicioPagoMock;

    @InjectMocks
    private PagoTarjeta pagoTarjeta;

    @Test
    void debeProcesarPagoConTarjetaUsandoAdaptador() {
        // Preparar
        double monto = 150.0;
        String comprobante = "TCK-123";
        when(servicioPagoMock.procesarPago(monto)).thenReturn("Transacción APROBADA simulada");

        // Ejecutar
        String resultado = pagoTarjeta.procesarPago(comprobante, monto);

        // Verificar
        assertTrue(resultado.contains("TARJETA"));
        assertTrue(resultado.contains("Transacción APROBADA simulada"));
        verify(servicioPagoMock, times(1)).procesarPago(monto);
    }

    // --- PRUEBA PAGO EFECTIVO (Sin dependencias) ---
    @Test
    void debeProcesarPagoEnEfectivo() {
        PagoEfectivo pagoEfectivo = new PagoEfectivo();
        String resultado = pagoEfectivo.procesarPago("TCK-001", 50.0);
        
        assertTrue(resultado.contains("Efectivo (Caja)"));
        assertTrue(resultado.contains("50.0"));
    }

    // --- PRUEBA PAGO TRANSFERENCIA (Sin dependencias) ---
    @Test
    void debeProcesarPagoTransferencia() {
        PagoTransferencia pagoTransferencia = new PagoTransferencia();
        String resultado = pagoTransferencia.procesarPago("TCK-002", 20.0);
        
        assertTrue(resultado.contains("YAPE/PLIN"));
    }
}