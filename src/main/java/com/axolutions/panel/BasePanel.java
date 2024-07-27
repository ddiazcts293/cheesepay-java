package com.axolutions.panel;

import com.axolutions.AppContext;

public abstract class BasePanel 
{
    public abstract PanelTransition show(
        AppContext appContext, 
        PanelTransition args);

    protected PanelTransition nextDestination(Location location)
    {
        return nextDestination(location, null);
    }

    protected PanelTransition nextDestination(Location location, Object obj)
    {
        return new PanelTransition(location, obj);
    }
}
