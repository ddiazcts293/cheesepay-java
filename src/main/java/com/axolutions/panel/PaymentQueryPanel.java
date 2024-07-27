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
     * Algoritmo:
     * 1. Inicio
     * 2. Preguntar la categoría de pago, ciclo y nivel educativo (si aplica)
     * 3. Mostrar precios de cobros
     * 4. Fin
     */

    @Override
    public PanelTransition show(AppContext appContext, PanelTransition args) 
    {
        System.out.println("Panel de consulta de cobros");
        return null;
    }
}
