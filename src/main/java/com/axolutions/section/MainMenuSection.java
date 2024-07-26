package com.axolutions.section;

import com.axolutions.AppContext;

public class MainMenuSection implements SectionBase
{
    @Override
    public Destination show(AppContext appContext) 
    {
        var scanner = appContext.getScanner();

        System.out.println("Menú principal");
        
        System.out.println("1. Registro de alumnos");
        System.err.println("2. Cambiar de cuenta");
        System.out.println("3. Salir del programa");
        String option = scanner.nextLine();

        switch (option) 
        {
            case "1":
                return Destination.StudentRegistrationSection;
            case "2":
                return Destination.LoginSection;
            default:
                return Destination.Exit;
        }
    }
}
