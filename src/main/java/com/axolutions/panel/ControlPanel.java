package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.util.Menu;

public class ControlPanel extends BasePanel
{
    /**
     * TODO: Panel de control
     * 
     * Aquí es donde se pueden modificar los cobros, gestionar empleados, y 
     * cambiar contraseñas.
     * 
     * Solo los administrador podrán agregar o quitar empleados.
     * 
     * Consultas involucradas:
     * - Obtener cobros (por nivel, ciclo)
     * - Registrar nuevos cobros
     * - Actualizar datos de cobros
     *
     * Algoritmo 
     * 1. Inicio
     * 2. Preguntar que desea hacer
     *    - Modificar cobros actuales
     *    - Registrar un nuevo ciclo escolar
     *    - Registrar un nuevo nivel educativo
     *    - Registrar un nuevo cobro
     *    - Dar de alta/baja a un empleado
     * 3. Fin
     */
    
    @Override
    public PanelTransition show(AppContext appContext, PanelTransition args)
    {
        System.out.println("Panel de control");

        Menu menu = appContext.createMenu();

        menu
            .AddItem("1", "Modificar lista de cobros")
            .AddItem("2", "Registrar un nuevo ciclo escolar")
            .AddItem("3", "Registrar un nuevo cobro")
            .AddItem("4", "Gestionar empleados");

        menu.show();

        return null;
    }
}
