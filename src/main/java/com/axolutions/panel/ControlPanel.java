package com.axolutions.panel;

import com.axolutions.AppContext;

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
        return null;
    }
}
