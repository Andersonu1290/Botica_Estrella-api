document.addEventListener("DOMContentLoaded", async function () {
    // Protección de ruta
    if (!sessionStorage.getItem("usuarioActivo")) {
        window.location.href = "Login.html";
        return;
    }

    await renderizarConfirmacionVenta();
});

/**
 * Renderiza la confirmación de venta desde sessionStorage
 */
async function renderizarConfirmacionVenta() {
    const posVoucherBody = document.getElementById("posVoucherBody");
    const receiptDocBody = document.getElementById("receiptDocBody");
    const documentoTitulo = document.getElementById("documentoTitulo");
    const comprobanteElectronico = document.getElementById("comprobanteElectronico");
    const panelAlertasStock = document.getElementById("panelAlertasStock");
    const contenedorAlertas = document.getElementById("contenedorAlertas");

    try {
        const raw = sessionStorage.getItem("ultimaVentaProcesada");

        if (!raw) {
            if (posVoucherBody) posVoucherBody.textContent = "No se encontraron datos de la última venta.";
            if (receiptDocBody) receiptDocBody.textContent = "Sin comprobante disponible.";
            return;
        }

        let transaccion;

        try {
            transaccion = JSON.parse(raw);
        } catch (e) {
            console.error("JSON inválido en ultimaVentaProcesada:", e);
            if (posVoucherBody) posVoucherBody.textContent = "Error en datos de transacción.";
            return;
        }

        // =========================
        // 1. LOG DE PAGO
        // =========================
        if (posVoucherBody) {
            posVoucherBody.innerHTML =
                transaccion?.msgPago ||
                "Transacción aprobada. Código: " + Math.floor(Math.random() * 900000 + 100000);
        }

        // =========================
        // 2. DOCUMENTO ELECTRÓNICO
        // =========================
        if (receiptDocBody) {
            const texto =
                transaccion?.msgDoc ||
                `TICKET: ${transaccion?.comprobante || "N/A"}\n` +
                `Cliente: ${transaccion?.nombreCliente || "Cliente Varios"}\n` +
                `Total: ${formatMoneda(transaccion?.total || 0)}`;

            receiptDocBody.textContent = texto;
        }

        // =========================
        // 3. TIPO DE COMPROBANTE
        // =========================
        const tipoComp = (transaccion?.tipoComprobante || "BOLETA").toUpperCase();

        if (comprobanteElectronico) {
            comprobanteElectronico.classList.remove("style-BOLETA", "style-FACTURA");
            comprobanteElectronico.classList.add(`style-${tipoComp}`);
        }

        if (documentoTitulo) {
            documentoTitulo.className = `doc-title title-${tipoComp}`;
            documentoTitulo.textContent =
                tipoComp === "FACTURA"
                    ? "FACTURA ELECTRÓNICA"
                    : "BOLETA DE VENTA ELECTRÓNICA";
        }

        // =========================
        // 4. ALERTAS DE STOCK
        // =========================
        const alertas = Array.isArray(transaccion?.alertasStock)
            ? transaccion.alertasStock
            : [];

        if (contenedorAlertas && panelAlertasStock) {
            contenedorAlertas.innerHTML = "";

            if (alertas.length > 0) {
                alertas.forEach((alerta) => {
                    const div = document.createElement("div");
                    div.className = "user-badge font-mono text-xs mb-15";
                    div.style.borderLeft = "3px solid #ef4444";
                    div.style.paddingLeft = "8px";
                    div.textContent = alerta;
                    contenedorAlertas.appendChild(div);
                });

                panelAlertasStock.style.display = "block";
            } else {
                panelAlertasStock.style.display = "none";
            }
        }

        // opcional: limpiar después de mostrar
        // sessionStorage.removeItem("ultimaVentaProcesada");

    } catch (error) {
        console.error("Error en confirmación de venta:", error);

        if (posVoucherBody) posVoucherBody.textContent = "Error al cargar datos del ticket.";
        if (receiptDocBody) receiptDocBody.textContent = "Error al generar comprobante.";

        if (typeof mostrarNotificacion === "function") {
            mostrarNotificacion("Error al cargar la confirmación de venta", "error");
        }
    }
}