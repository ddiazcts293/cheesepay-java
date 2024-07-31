package com.axolutions.panel;

public class PanelTransitionArgs 
{
    public Location newLocation;
    public Location currentLocation;
    public Object obj;

    public PanelTransitionArgs(Location newLocation, Object obj)
    {
        this.newLocation = newLocation;
        this.obj = obj;
    }

    public PanelTransitionArgs(
        Location newLocation, 
        Location currentLocation,
        Object obj)
    {
        this(newLocation, obj);
        this.currentLocation = currentLocation;
    }
}
