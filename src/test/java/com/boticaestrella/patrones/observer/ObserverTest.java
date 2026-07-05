package com.boticaestrella.patrones.observer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObserverTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GestorStock gestorStock;

    @Test
    void debePublicarEventoDeStockCritico() {
        // Ejecutar
        gestorStock.dispararAlertaStockCritico("SKU-123", 2);

        // Capturar el evento exacto que se publicó
        ArgumentCaptor<StockCriticoEvent> captor = ArgumentCaptor.forClass(StockCriticoEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());

        // Verificar los datos del evento inmutable (Record)
        StockCriticoEvent eventoLanzado = captor.getValue();
        assertEquals("SKU-123", eventoLanzado.sku());
        assertEquals(2, eventoLanzado.stockActual());
    }

    @Test
    void notificacionAdminDebeGenerarMensajeCorrecto() {
        NotificacionAdmin adminObserver = new NotificacionAdmin();
        StockCriticoEvent evento = new StockCriticoEvent("PARACETAMOL-500", 5);
        
        String resultado = adminObserver.procesarAlertaStock(evento);
        
        assertTrue(resultado.contains("[ALERTA GERENCIA: Administrador General]"));
        assertTrue(resultado.contains("PARACETAMOL-500"));
    }

    @Test
    void logisticaObserverDebeGenerarMensajeCorrecto() {
        LogisticaObserver logisticaObserver = new LogisticaObserver();
        StockCriticoEvent evento = new StockCriticoEvent("IBUPROFENO-400", 0);
        
        String resultado = logisticaObserver.procesarAlertaStock(evento);
        
        assertTrue(resultado.contains("[ALERTA LOGÍSTICA: Jefe de Almacén]"));
    }
}