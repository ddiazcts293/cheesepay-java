package com.axolutions.db.type;

public class EducationLevel 
{
    public String code;
    public String description;
    
    @Override
    public String toString()
    {
        return description;
    }
}
