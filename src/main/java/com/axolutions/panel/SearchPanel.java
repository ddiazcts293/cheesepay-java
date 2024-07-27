package com.axolutions.panel;

import com.axolutions.AppContext;

public class SearchPanel implements BasePanel 
{
    /**
     * TODO: Panel de búsqueda
     * 
     * En este panel se podrá realizar la busqueda de tutores y alumnos
     * 
     * Algortimo:
     * - Buscar a un tutor
     * - Buscar a un alumno
     *
     * Para ambos casos:
     * 1. Inicio
     * 2. Preguntar que desea buscar (tutor o alumno)
     * 3. Solicitar un dato (se puede preguntar a partir de una lista de campos
     *    o simplemente solicitarlo y buscar coincidencias en todos los campos)
     * 4. Realizar la consulta
     * 5. Mostrar los resultados obtenidos
     * 6. Preguntar si desea ver la información de un elemento, en cuyo caso se
     *    pide que seleccione el número de elemento, o bien si desea volver al 
     *    menú principal.
     * 7. Fin
     */

    @Override
    public Destination show(AppContext appContext) 
    {
        System.out.println("Panel de búsqueda");
        return null;
    }
}
