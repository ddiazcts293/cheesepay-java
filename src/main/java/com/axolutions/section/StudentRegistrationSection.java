package com.axolutions.section;

import com.axolutions.AppContext;

public class StudentRegistrationSection implements SectionBase 
{
    @Override
    public Destination show(AppContext appContext) 
    {
        System.out.println("Registro de alumnos");
        System.out.print("Presione una tecla...");
        appContext.getScanner().nextLine();

        return Destination.Back;
    }
    
}
