package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.DbContext;
import com.axolutions.util.Console;

public abstract class BasePanel 
{
    public BasePanel(AppContext appContext)
    {
        this.appContext = appContext;
        this.dbContext = appContext.getDbContext();
        this.console = appContext.getConsole();
    }

    protected AppContext appContext;
    protected DbContext dbContext;
    protected Console console;

    public abstract PanelTransitionArgs show(PanelTransitionArgs args);

    protected PanelTransitionArgs nextDestination(Location location)
    {
        return nextDestination(location, null);
    }

    protected PanelTransitionArgs nextDestination(Location location, Object obj)
    {
        return new PanelTransitionArgs(location, obj);
    }
}
