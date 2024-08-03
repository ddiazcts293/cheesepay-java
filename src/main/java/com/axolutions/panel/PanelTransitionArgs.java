package com.axolutions.panel;

/**
 * Representa una clase que contiene datos de transición entre paneles.
 */
public class PanelTransitionArgs
{
    private Location requestedLocation;
    private Location lastLocation;
    private Object obj;

    /**
     * Crea un nuevo objeto PanelTransitionArgs.
     * @param requestedLocation Ubicación solicitada
     * @param lastLocation Última ubicación
     * @param obj Objeto establecido desde la última ubicación
     */
    public PanelTransitionArgs(
        Location requestedLocation,
        Location lastLocation,
        Object obj)
    {
        this.requestedLocation = requestedLocation;
        this.lastLocation = lastLocation;
        this.obj = obj;
    }

    /**
     * Obtiene la obicación solicitada.
     * @return Location
     */
    public Location getRequestedLocation()
    {
        return requestedLocation;
    }

    /**
     * Obtiene la última ubicación.
     * @return Location
     */
    public Location getLastLocation()
    {
        return lastLocation;
    }

    /**
     * Obtiene el objeto establecido desde la última ubicación.
     * @return Objeto
     */
    public Object getObj()
    {
        return obj;
    }
}
