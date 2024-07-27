package com.axolutions.panel;

import com.axolutions.AppContext;

public class GroupQueryPanel implements BasePanel
{
    /**
     * TODO: Panel de consulta de grupos
     *
     * Aquí se muestran a los alumnos que pertenecen a los diferentes grupos, 
     * ya sea por ciclo escolar, grado y nivel educativo
     * 1. Inicio
     * 2. Preguntar el ciclo escolar, nivel educativo, grado y grupo ya sea 
     *    mostrando un menú para cada opción o que el usuario lo ingrese 
     *    directamente
     * 3. Mostrar los alumnos que pertenece a esos grupos
     * 4. Fin
     */

    @Override
    public Destination show(AppContext appContext) 
    {
        System.out.println("Panel de consulta de grupos");
        return null;
    }
}
