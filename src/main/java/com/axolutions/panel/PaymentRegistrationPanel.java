package com.axolutions.panel;

import com.axolutions.AppContext;

public class PaymentRegistrationPanel implements BasePanel
{
    /**
     * TODO: Panel de registro de pagos
     * 
     * En este panel se podrán registrar todos los pagos que un alumno efectue
     * 
     * Algoritmo
     * 1. Inicio
     * 2. Preguntar si se conoce la matricula del alumno, si se conoce, 
     *    solicitarla; sino, buscar en el panel de busqueda.
     * 3. Seleccionar el tutor que registra el pago
     * 4. Preguntar qué desea pagar
     * 5. Preguntar por el ciclo escolar (por defecto se utilizará el actual)
     * 6. Preguntar si desea agregar otro pago
     * 7. Mostrar el total y pedir confirmación
     * 8. Registrar el pago
     * 9. Si el pago es de inscripción, entonces el alumno deberá ser asignado
     *    a un grupo y nivel educativo
     * 10. Fin
     */

    @Override
    public Destination show(AppContext appContext) 
    {
        System.out.println("Panel de registro de pagos");

        return null;
    }

}
