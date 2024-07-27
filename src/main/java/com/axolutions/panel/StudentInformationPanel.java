package com.axolutions.panel;

import com.axolutions.AppContext;

public class StudentInformationPanel extends BasePanel
{
    /**
     * TODO: Panel de información de alumno
     * 
     * En este panel se mostrara toda la información concerniente a un alumno
     * 
     * Algoritmo
     * 1. Inicio
     * 2. Preguntar si se conoce la matricula del alumno, si se conoce, 
     *    solicitarla; sino, buscar en el panel de busqueda
     * 3. Preguntar que desea hacer:
     *    - consultar la información del alumno
     *    - modificar su información personal
     *    - consultar los grupos en los que ha estado inscrito por ciclo y nivel
     *      educativo
     *    - consultar los pagos que este ha realizado ya sea por categoría y/o 
     *      ciclo escolar
     *    - ver sus tutores y sus teléfonos registrados
     * 4. Fin
     */

    @Override
    public PanelTransition show(AppContext appContext, PanelTransition args) 
    {
        System.out.println("Panel de información de alumno");
        return null;
    }

}
