package com.axolutions.section;

import java.sql.SQLException;

import com.axolutions.AppContext;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

public class LoginSection implements SectionBase
{
    public Destination show(AppContext appContext)
    {
        var scanner = appContext.getScanner();
        String user, password;
        
        System.out.println("Bienvenido a CheesePay v1.0");

        System.out.print("Ingrese su usuario: ");
        user = scanner.nextLine();
        System.out.print("Contraseña: ");
        password = scanner.nextLine();

        // TODO: Identificar tipo de error: conexión o acceso denegado
        
        try 
        {
            System.out.print("Conectando... ");
            appContext.login(user, password);
        }
        // Captura un error de conexión
        catch (CommunicationsException e)
        {
            System.out.println("error");
            System.out.println(e.getMessage());
            
        }
        // Captura un error en las credenciales de acceso
        catch (SQLException e)
        {
            System.out.println("error");
            System.out.println(e.getMessage());
            // Si fue de conexión, terminar programa; si fue por contraseña
            // incorrecta, volver a pedirla

            return Destination.Exit;
        }

        System.out.println("ok");
        
        return Destination.MainMenu;
    }
}
