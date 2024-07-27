package com.axolutions.panel;

import java.sql.SQLException;

import com.axolutions.AppContext;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

public class LoginPanel extends BasePanel
{
    /**
     * Panel de inicio de sesión
     * 
     * Este es el primer panel que ve el usuario al iniciar el programa.
     * 
     * Aquí no se hacen consultas en la BD, simplimente se llama a la función 
     * login() en appContext para intentar iniciar sesión.
     * 
     * Todo ello deberá estar dentro de un bucle que se ejecute mientras no se
     * haya iniciado sesión, o bien, hasta que ocurra un error de conexión,en 
     * cuyo caso el programa debe finalizar.
     * 
     * La clase Console dispone de un metodo para que el usuario ingrese una
     * contraseña sin mostrarla en pantalla.
     */

    public PanelTransition show(AppContext appContext, PanelTransition args)
    {
        var console = appContext.getConsole();
        String user, password;

        System.out.println("Bienvenido a CheesePay v1.0");
        
        do {
            user = console.readString("Ingrese su usuario");
            password = console.readPassword("Contraseña");
            
            // Intenta realizar la conexión con las credenciales dadas
            try 
            {
                System.out.print("Conectando... ");
                appContext.login(user, password);
            }
            // Captura un error de conexión
            catch (CommunicationsException e)
            {
                System.out.println("error de conexión");
                System.out.println(e.getMessage());
                
                // Terminar programa
                return nextDestination(Location.Exit);
            }
            // Captura un error en las credenciales de acceso
            catch (SQLException e)
            {
                System.out.println("acceso denegado");
                System.out.println(e.getMessage());
                
                // Volver a pedir las credenciales
            }
        // Repite mientras no este conectado a la base de datos
        } while (!appContext.isConnected());
        
        System.out.println("ok");
        
        // Retorna al menú principal
        return nextDestination(Location.MainMenu);
    }
}
