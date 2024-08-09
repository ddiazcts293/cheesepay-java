package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.DbContext;
import com.axolutions.util.Console;

/**
 * Representa la base para los diferentes tipos de paneles.
 */
public abstract class BasePanel
{
    private Location location;
    protected AppContext appContext;
    protected DbContext dbContext;
    protected Console console;
    protected PanelHelper helper;

    /**
     * Constructor base.
     * @param appContext Instancia del objeto AppContext
     * @param location Ubicación que corresponde al panel
     */
    public BasePanel(AppContext appContext, Location location)
    {
        this.location = location;
        this.appContext = appContext;
        this.dbContext = appContext.getDbContext();
        this.console = appContext.getConsole();
        this.helper = appContext.getPanelHelper();
    }

    /**
     * Obtiene la ubicación que corresponde al panel.
     * @return Location
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Muestra el panel.
     * @param args Objeto que contiene los datos de transición provistos por el
     * último panel.
     * @return Instancia de objeto PanelTransitionArgs
     */
    public abstract PanelTransitionArgs show(PanelTransitionArgs args);

    /**
     * Establece una nueva ubicación.
     * @param requestedLocation Ubicación solicitada
     * @return Objeto PanelTransitionArgs
     */
    protected PanelTransitionArgs setLocation(Location requestedLocation)
    {
        return setLocation(requestedLocation, null);
    }

    /**
     * Establece una nueva ubicación.
     * @param requestedLocation Ubicación solicitada
     * @param obj Objeto a transferir
     * @return Objeto PanelTransitionArgs
     */
    protected PanelTransitionArgs setLocation(
        Location requestedLocation,
        Object obj)
    {
        return new PanelTransitionArgs(requestedLocation, location, obj);
    }

    /**
     * Dirige a una nueva ubicación y regresa una vez que se terminen las
     * operaciones en él.
     * @param requestedLocation Ubicación solicitada
     * @return Objeto devuelto desde la ubicación solicitada
     */
    protected Object goTo(Location requestedLocation)
    {
        return goTo(requestedLocation);
    }

    /**
     * Dirige a una nueva ubicación transfiriendo un objeto y regresa una vez
     * que se terminen las operaciones en él.
     * @param requestedLocation Ubicación solicitada
     * @param obj Objeto a transferir
     * @return Objeto devuelto desde la ubicación solicitada
     */
    protected Object goTo(Location requestedLocation, Object obj)
    {
        var args = new PanelTransitionArgs(requestedLocation, location, obj);
        return appContext.goTo(args);
    }
}
