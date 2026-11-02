
package com.latamairlines;

import com.latamairlines.dao.PagoDAO;
import com.latamairlines.entities.Pago;
import java.math.BigDecimal;

public class PruebaPagoDAO {

    public static void main(String[] args) {

        PagoDAO dao = new PagoDAO();

        try {
            Pago pago = dao.buscarPorReserva(3);

            if (pago != null) {

                System.out.println("Monto anterior: "
                        + pago.getMonto());

                dao.actualizarPago(
                        pago.getId(),
                        new BigDecimal("250.00"),
                        pago.getFechaPago(),
                        pago.getMetodoPago()
                );

                System.out.println("Pago actualizado correctamente");

            } else {
                System.out.println("Pago no encontrado");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        } finally {
            dao.cerrar();
        }
    }
}
