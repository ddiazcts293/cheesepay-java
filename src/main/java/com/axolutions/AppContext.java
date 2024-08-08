package com.axolutions;

import java.util.Scanner;
import java.util.HashMap;
import java.sql.SQLException;
import com.axolutions.db.*;
import com.axolutions.panel.*;
import com.axolutions.util.*;
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
    private HashMap<Location, BasePanel> panels;

    /**
     * Constructor principal.
     * @param scanner Instancia de Scanner que lee la entrada de usuario.
     * @param dbConnectionString Cadena de conexión con la base de datos.
     */
    public AppContext(Scanner scanner, String dbConnectionString)
    {
        // Inicialización de atributos
        this.isRunning = false;
        this.scanner = scanner;
        this.console = new Console(scanner);
        this.dbConnectionWrapper = new DbConnectionWrapper(dbConnectionString);
        this.dbContext = new DbContext(dbConnectionWrapper);
        this.panels = new HashMap<>();

        // Crea un arreglo con las instancias de los paneles existentes
        BasePanel[] panelInstances = new BasePanel[]
        {
            new MainMenuPanel(this),
            new LoginPanel(this),
            new EnrollmentPanel(this),
            new SearchPanel(this),
            new InfoPanel(this),
            new GroupQueryPanel(this),
            new FeeQueryPanel(this),
            new ControlPanel(this),
            new PaymentRegistrationPanel(this)
        };

        // Registra las instancias de los paneles en el HashMap
        for (BasePanel panel : panelInstances)
        {
            panels.put(panel.getLocation(), panel);
        }
    }

    /**
     * Ejecuta un bucle que se encarga de controlar la navegación entre las
     * diferentes secciones del programa.
     */
    public void run()
    {
        // Verifica si el bucle se encuentra en ejecución
        if (isRunning)
        {
            // De ser así, sale de la función
            return;
        }

        // Establece que el bucle ya se encuentra en ejecución
        isRunning = true;
        // Variable que indica si se debe volver a iniciar sesión
        boolean loginAgain = true;

        // Bucle que se ejecutará mientras no se solicite salir del programa
        // Repite indefinidamente el ciclo de inicio de sesión
        do
        {
            // Variable que almacenará la instancia del panel a mostrar
            BasePanel panel;
            // Variables para controlar la ubicaciones de desplazamiento entre
            // los diferentes paneles
            Location requestedLocation;
            //Location currentLocation;
            Location lastLocation;
            // Variables que contienen información transferida entre paneles
            PanelTransitionArgs transition;
            Object obj = null;

            // Borra el contenido de la pantalla
            console.clearDisplay();
            // Obtiene la instancia del panel de inicio de sesión
            panel = panels.get(Location.LoginPanel);
            // Muestra el panel de inicio de sesión
            transition = panel.show(null);

            // Verifica si se indicó salir del programa o si la conexión con la
            // base de datos no fue establecida
            if (transition == null ||
                transition.getRequestedLocation() == Location.Exit ||
                !isConnected)
            {
                // Termina el bucle
                break;
            }

            // Establece el destino actual como el menú principal
            //currentLocation = Location.MainMenu;
            // Establece el próximo destino también como el menú principal
            requestedLocation = Location.MainMenu;

            // Bucle que se ejecutará indefinidamente para realizar la
            // navegación entre paneles mientras no se solicite salir del
            // programa
            do
            {
                // Borra todo el contenido de la pantalla
                console.clearDisplay();

                // Establece como destino anterior el destino que era actual en
                // la iteración previa
                lastLocation = panel.getLocation();
                // Obtiene la instancia del panel dada por la variable de
                // próximo destino
                panel = panels.get(requestedLocation);
                // Establece el destino actual
                //currentLocation = requestedLocation;
                // Muestra el panel actual y espera a obtener un objeto de
                // transición
                transition = panel.show(
                    new PanelTransitionArgs(
                        panel.getLocation(),
                        lastLocation,
                        obj));

                // Verifica si tanto el objeto de transición como el nuevo
                // destino son nulos
                if (transition == null ||
                    transition.getRequestedLocation() == null)
                {
                    // Establece el menú principal como destino
                    requestedLocation = Location.MainMenu;
                    obj = null;
                }
                // De lo contrario
                else
                {
                    // Obtiene el nuevo destino
                    requestedLocation = transition.getRequestedLocation();
                    obj = transition.getObj();
                }

                // Verifica si se indicó salir de programa
                if (requestedLocation == Location.Exit)
                {
                    // Establece que no se deberá volver a iniciar sesión
                    loginAgain = false;
                    // Finaliza el bucle
                    break;
                }
                // Verifica si se indicó cambiar de cuenta
                else if (requestedLocation == Location.LoginPanel)
                {
                    // Verifica si el panel mostrado era el menú principal
                    if (panel.getLocation() == Location.MainMenu)
                    {
                        // Cierra la conexión con la base de datos
                        dbConnectionWrapper.close();
                        break;
                    }
                    // Si no lo es, lo dirige al menú principal
                    else
                    {
                        requestedLocation = Location.MainMenu;
                    }
                }
                // Verifica si se indicó regresar al panel anterior
                else if (requestedLocation == Location.Previous)
                {
                    requestedLocation = lastLocation;
                }
                else if (!panels.containsKey(requestedLocation))
                {
                    requestedLocation = Location.MainMenu;
                }
            } while (true);

        } while (loginAgain);

        // Cierra la conexión con la base de datos
        dbConnectionWrapper.close();
    }

    /**
     * Muestra un panel solicitado.
     * @param args Argumento que es transferido a la ubicación solicitada
     * @return Objeto devuelto desde la ubicación solicitada
     */
    public Object goTo(PanelTransitionArgs args)
    {
        // Verifica si el argumento es nulo o la ubicación no está registrada
        // en el mapa de paneles
        if (args == null || !panels.containsKey(args.getRequestedLocation()))
        {
            // Retorna null de ser así
            return null;
        }

        // Obtiene la instancia del panel solicitado
        var panel = panels.get(args.getRequestedLocation());
        // Muestra el panel y espera a que este devuelva un objeto
        var result = panel.show(args);

        // Verifica si el resultado devuelto es nulo
        if (result == null)
        {
            // De ser así, retorna nulo
            return null;
        }

        // Retorna el objeto devuelto desde el panel solicitado
        return result.getObj();
    }

    /**
     * Obtiene la instancia compartida de Scanner
     * @return Objeto Scanner
     */
    public Scanner getScanner()
    {
        return scanner;
    }

    /**
     * Obtiene la instancia compartida de DbContext.
     * @return Objeto DbContext
     */
    public DbContext getDbContext()
    {
        return dbContext;
    }

    /**
     * Obtiene la instancia compartida de Console.
     * @return Objeto Console
     */
    public Console getConsole()
    {
        return console;
    }

    /**
     * Devuelve un valor que indica si se ha conectado con la base de datos.
     * @return TRUE si es así, de lo contrario FALSE
     */
    public boolean isConnected()
    {
        return isConnected;
    }

    /**
     * Realiza el inicio de sesión.
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
