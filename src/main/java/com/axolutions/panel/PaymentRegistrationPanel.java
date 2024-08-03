package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.ScholarPeriod;
import com.axolutions.util.Menu;

public class PaymentRegistrationPanel extends BasePanel
{
    /**
     * TODO: Panel de registro de pagos
     * 
     * En este panel se podrán registrar todos los pagos que un alumno efectue
     * 
     * ALGORITMO
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
     * 
     * CONSULTAS
     * - Registrar un pago que contiene diferentes cobros
     * - Registrar a un alumno en un grupo
     */

    public PaymentRegistrationPanel(AppContext appContext)
    {
        super(appContext, Location.StudentRegistrationPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args) 
    {
        System.out.println("Panel de registro de pagos");

        ScholarPeriod selection = null;
        ScholarPeriod[] periods;
        String option;

        try 
        {
            periods = dbContext.getScholarPeriods();
        }
        catch (Exception e) 
        {
            System.out.println("Error al obtener los datos de ciclos " +
                "escolares");
            return null;
        }

        Menu menu = createMenu("Ciclos escolares");
        for (int i = 0; i < periods.length; i++) 
        {
            var level = periods[i];
            String description = String.format("%d-%d", 
                level.startingDate.getYear(),
                level.endingDate.getYear());

            menu.addItem(Integer.toString(i), description);
        }

        menu.addBlankLine();
        menu.addItem("v", "Volver al menú principal");

        do
        {
            option = menu.show("Seleccione una opción");
            if (option.equalsIgnoreCase("v"))
            {
                break;
            }
            
            int index = Integer.parseInt(option);
            selection = periods[index];

            showPaymentsForPeriod(selection);
        }
        while (true);

        return null;
    }

    private void showPaymentsForPeriod(ScholarPeriod period)
    {
        Menu menu = createMenu();
        menu.addItem("i", "Inscripciones");
    }
}
