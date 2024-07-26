package com.axolutions;

import java.util.Scanner;
import java.sql.SQLException;
import java.util.HashMap;
import com.axolutions.db.*;
import com.axolutions.section.*;
import com.axolutions.util.Console;
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
     * Almacena las instancias de todas las secciones del programa basándose en
     * un identificador asignado.
     */
    private HashMap<Destination, SectionBase> sectionInstances;

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
        this.dbConnectionWrapper = new DbConnectionWrapper(dbConnectionString);
        this.dbContext = new DbContext(dbConnectionWrapper);
        this.sectionInstances = new HashMap<>();

        // Registra las instancias de las pantallas en el HashMap
        sectionInstances.put(Destination.MainMenu, new MainMenuSection());
        sectionInstances.put(Destination.LoginSection, new LoginSection());
        sectionInstances.put(Destination.StudentRegistrationSection, new StudentRegistrationSection());
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
                // Variable que almacenará la sección mostrada
                SectionBase section;
                // Variables para almacenar los destinos próximo destino, actual
                // y anterior
                Destination nextDestination;
                Destination currentDestination;
                Destination previousDestination;
                
                // Obtiene la instancia de la sección de inicio de sesión
                section = sectionInstances.get(Destination.LoginSection);
                // Muestra la sección de inicio de sesión
                nextDestination = section.show(this);

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
                    // Obtiene la instancia de la sección indicada por la 
                    // variable de próximo destino
                    section = sectionInstances.get(nextDestination);
                    // Establece el destino actual
                    currentDestination = nextDestination;
                    // Muestra la sección actual y espera a obtener un nuevo
                    // destino. 
                    nextDestination = section.show(this);

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
                    else if (nextDestination == Destination.LoginSection)
                    {
                        // Verifica si la sección anterior era el menú principal
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
                    // Verifica si se indicó regresar a la sección anterior
                    else if (nextDestination == Destination.Back)
                    {
                        nextDestination = previousDestination;
                    }
                    else if (!sectionInstances.containsKey(nextDestination))
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
     * Muestra una sección y regresa luego de terminar.
     * 
     * @param destination Destino
     */
    public void goToAndReturn(Destination destination)
    {

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
     * Realiza el inicio de sesión.
     * 
     * @param user Nombre de usuario
     * @param password Contraseña
     * @throws SQLException Error de acceso
     * @throws CommunicationsException Error de conexión
     */
    public void login(String user, String password) throws SQLException, CommunicationsException
    {
        dbConnectionWrapper.create(user, password);
        isConnected = true;
    }
}
