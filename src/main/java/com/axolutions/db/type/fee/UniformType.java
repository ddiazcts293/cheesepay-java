package com.axolutions.db.type.fee;

public class UniformType 
{
    public int number;
    public String description;
    
    @Override
    public String toString() 
    {
        return description;
    }
}
