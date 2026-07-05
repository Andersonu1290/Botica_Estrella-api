package com.boticaestrella.patrones.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoAdapterTest {

    @Mock
    private PasarelaExterna pasarelaExternaMock;

    @InjectMocks
    private PagoAdapter pagoAdapter;

    @Test
    void debeTraducirPeticionAPasarelaExterna() {
        // Preparar
        double monto = 100.0;
        when(pasarelaExternaMock.realizarTransaccion(monto)).thenReturn("Respuesta API Banco");

        // Ejecutar
        String resultado = pagoAdapter.procesarPago(monto);

        // Verificar
        assertTrue(resultado.contains("[ADAPTER] Traduciendo..."));
        assertTrue(resultado.contains("Respuesta API Banco"));
        verify(pasarelaExternaMock, times(1)).realizarTransaccion(monto);
    }
}