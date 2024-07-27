package com.axolutions;

import java.util.Scanner;
import java.sql.SQLException;
import java.util.HashMap;
import com.axolutions.db.*;
import com.axolutions.panel.*;
import com.axolutions.util.Console;
import com.axolutions.util.Menu;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

public class AppContext 
{
    private boolean isRunning;
    private boolean isConnected;

    // Instancia compartida de Scanner
    private Scanner scanner;
    // Instancia compartida de Console
    private Console console;
    // Contexto de la base de datos
    private DbContext dbContext;
    // Envoltorio de la conexión con la base de datos
    private DbConnectionWrapper dbConnectionWrapper;

    /**
     * Almacena las instancias de todas los paneles del programa basándose en
     * un identificador asignado.
     */
    private HashMap<Destination, BasePanel> panels;

    /**
     * Constructor principal.
     * 
     * @param scanner Instancia de Scanner que lee la entrada de usuario.
     * @param dbConnectionString Cadena de conexión con la base de datos.
     */
    public AppContext(Scanner scanner, String dbConnectionString)
    {
        // Inicialización de variables
        this.isRunning = false;
        this.scanner = scanner;
        this.console = new Console(scanner);
        this.dbConnectionWrapper = new DbConnectionWrapper(dbConnectionString);
        this.dbContext = new DbContext(dbConnectionWrapper);
        this.panels = new HashMap<>();

        // Registra las instancias de los paneles en el HashMap
        panels.put(Destination.MainMenu, new MainMenuPanel());
        panels.put(Destination.LoginPanel, new LoginPanel());
        panels.put(Destination.StudentRegistrationPanel, new StudentRegistrationPanel());
        panels.put(Destination.PaymentRegistrationPanel, new PaymentRegistrationPanel());
        panels.put(Destination.SearchPanel, new SearchPanel());
        panels.put(Destination.StudentInformationPanel, new StudentInformationPanel());
        panels.put(Destination.GroupQueryPanel, new GroupQueryPanel());
        panels.put(Destination.PaymentQueryPanel, new PaymentQueryPanel());
        panels.put(Destination.ControlPanel, new ControlPanel());
        panels.put(Destination.LoginPanel, new LoginPanel());
    }

    /**
     * Ejecuta un bucle que se encarga de controlar la navegación entre las 
     * diferentes secciones del programa.
     */
    public void run()
    {
        // Verifica que el bucle no se encuentre en ejecución
        if (!isRunning)
        {
            // Indica que el programa ya se encuentra en ejecución
            isRunning = true;
            // Variable que indica si se debe volver a iniciar sesión
            boolean loginAgain = true;

            // Bucle que se ejecutará mientras no se solicite salir del programa
            // Va a repetir el ciclo de inicio de sesión una y otra vez
            do 
            {
                // Variable que almacenará el panel a mostrar
                BasePanel panel;
                // Variables para controlar el desplazamiento entre paneles
                Destination nextDestination;
                Destination currentDestination;
                Destination previousDestination;
                
                // Obtiene la instancia del panel de inicio de sesión
                panel = panels.get(Destination.LoginPanel);
                // Muestra el panel de inicio de sesión
                nextDestination = panel.show(this);

                // Verifica si se indicó salir del programa o si no se ha
                // realizado la conexión con la base de datos
                if (nextDestination == Destination.Exit || !isConnected)
                {
                    // Termina el bucle
                    break;
                }

                // Establece el destino actual como el menú principal
                currentDestination = Destination.MainMenu; 
                // Establece el próximo destino también como el menú principal
                nextDestination = currentDestination;
                
                // Bucle que se ejecutará infinitamente hasta que se indique que
                // se debe salir de la sesión para cambiar de cuenta o salir del
                // programa
                do
                {
                    previousDestination = currentDestination;
                    // Obtiene la instancia del panel dada por la  variable de
                    // próximo destino
                    panel = panels.get(nextDestination);
                    // Establece el destino actual
                    currentDestination = nextDestination;
                    // Muestra el panel actual y espera a obtener un nuevo
                    // destino. 
                    nextDestination = panel.show(this);

                    // Verifica si se indicó un destino nulo
                    if (nextDestination == null)
                    {
                        // Establece el menú principal como destino
                        nextDestination = Destination.MainMenu;
                    }
                    // Verifica si se indicó salir de programa
                    else if (nextDestination == Destination.Exit)
                    {
                        // Establece que no se deberá volver a iniciar sesión
                        loginAgain = false;
                        break;
                    }
                    // Verifica si se indicó salir de la sesión sesión
                    else if (nextDestination == Destination.LoginPanel)
                    {
                        // Verifica si el panel anterior era el menú principal
                        if (currentDestination == Destination.MainMenu)
                        {
                            // Cierra la conexión con la base de datos
                            dbConnectionWrapper.close();
                            break;
                        }
                        // Si no lo es, lo dirige al menú principal
                        else
                        {
                            nextDestination = Destination.MainMenu;
                        }
                    }
                    // Verifica si se indicó regresar al panel anterior
                    else if (nextDestination == Destination.Back)
                    {
                        nextDestination = previousDestination;
                    }
                    else if (!panels.containsKey(nextDestination))
                    {
                        nextDestination = Destination.MainMenu;
                    }
                } while (true);
                
            } while (loginAgain);

            // Cierra la conexión con la base de datos
            dbConnectionWrapper.close();
        }
    }

    /**
     * Muestra un panel y regresa luego de terminar.
     * 
     * @param destination Destino
     */
    public void goToAndReturn(Destination destination)
    {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    /**
     * Obtiene la instancia compartida de Scanner
     * 
     * @return Objeto Scanner
     */
    public Scanner getScanner()
    {
        return scanner;
    }

    /**
     * Obtiene la instancia compartida de DbContext.
     * 
     * @return Objeto DbContext
     */
    public DbContext getDbContext() 
    {
        return dbContext;
    }

    /**
     * Obtiene la instancia compartida de Console.
     * 
     * @return Objeto Console
     */
    public Console getConsole() 
    {
        return console;
    }

    /**
     * Devuelve un valor que indica si se ha conectado con la base de datos.
     * 
     * @return TRUE si es así, de lo contrario FALSE
     */
    public boolean isConnected()
    {
        return isConnected;
    }

    /**
     * Crea un objeto para crear menús interactivos.
     * 
     * @return Objeto Menu
     */
    public Menu createMenu()
    {
        return new Menu(scanner);
    }

    /**
     * Realiza el inicio de sesión.
     * 
     * @param user Nombre de usuario
     * @param password Contraseña
     * @throws SQLException Error de acceso
     * @throws CommunicationsException Error de conexión
     */
    public void login(String user, String password) throws SQLException, CommunicationsException
    {
        //dbConnectionWrapper.create(user, password);
        isConnected = true;
    }
}
