package com.axolutions.panel;

import com.axolutions.AppContext;

public class PaymentQueryPanel extends BasePanel
{
    /**
     * TODO: Panel de consulta de cobros
     *
     * En este panel se podrán consultar los costos de cada cobro disponible,
     * según la categoría, ciclo escolar y nivel educativo
     *
     * ALGORITMO:
     * 1. Inicio
     * 2. Preguntar la categoría de pago, ciclo y nivel educativo (si aplica)
     * 3. Mostrar precios de cobros
     * 4. Fin
     *
     * CONSULTAS:
     * - Obtener, consultas individuales, los listados de costos de los 
     *   cobros para cada ciclo escolar y nivel educativo.
     *   Datos: identificador de cobro, concepto (si lo tiene), costo, fechas
     *   de periodo escolar y descripción de nivel educativo (si lo tiene)
     * 
     * - Obtener la cantidad de pagos realizados para cada categoria agrupado 
     *   por ciclo escolar
     *   Datos: categoria de cobro, fechas de perido escolar, cantidad de pagos
     * 
     * - Obtener el costo de un paquete de papeleria para un grado, nivel 
     *   educativo y ciclo escolar
     *   Datos: identificador de cobro, concepto, grado, costo, fechas
     *   de periodo escolar y descripción de nivel educativo
     * 
     * - Obtener el costo de un uniforme para un nivel educativo, tipo y ciclo 
     *   escolar
     *   Datos: identificador de cobro, concepto, talla, descripción de tipo de
     *   uniforme, periodo escolar y nivel educativo
     */

    public PaymentQueryPanel(AppContext appContext)
    {
        super(appContext, Location.ControlPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        System.out.println("Panel de consulta de cobros");
        return null;
    }
}
