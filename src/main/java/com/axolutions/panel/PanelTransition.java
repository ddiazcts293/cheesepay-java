package com.axolutions.panel;

public class PanelTransition 
{
    public Location location;
    public Object obj;

    public PanelTransition(Location destination, Object obj)
    {
        this.location = destination;
        this.obj = obj;
    }
}
