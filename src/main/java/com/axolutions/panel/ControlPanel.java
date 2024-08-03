package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.util.*;

public class ControlPanel extends BasePanel
{
    /**
     * TODO: Panel de control
     * 
     * Aquí es donde se pueden modificar los cobros 
     * 
     * Consultas involucradas:
     * - Obtener cobros (por nivel, ciclo)
     * - Registrar nuevos cobros
     * - Actualizar datos de cobros
     *
     * Algoritmo 
     * 1. Inicio
     * 2. Preguntar que desea hacer
     *    - Registrar un nuevo ciclo escolar
     *    - Registrar un nuevo cobro
     *    - Dar de alta/baja a un empleado
     * 3. Fin
     */
    
    public ControlPanel(AppContext appContext)
    {
        super(appContext, Location.ControlPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        Menu menu = appContext.createMenu("Panel de control");
        menu.addItem("1", "Registrar un nuevo ciclo escolar");
        menu.addItem("2", "Registrar un nuevo evento especial");
        menu.addItem("3", "Regresar");

        /*
         * 
         */

        return null;
    }
}
