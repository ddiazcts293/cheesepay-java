package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.DbContext;
import com.axolutions.db.type.EducationLevel;
import com.axolutions.db.type.ScholarPeriod;
import com.axolutions.db.type.fee.FeeType;
import com.axolutions.util.Console;
import com.axolutions.util.Menu;

/**
 * Representa la base para los diferentes tipos de paneles.
 */
public abstract class BasePanel
{
    /**
     * Constructor base.
     * @param appContext Instancia del objeto AppContext
     * @param location Ubicación que corresponde al panel
     */
    public BasePanel(AppContext appContext, Location location)
    {
        this.location = location;
        this.appContext = appContext;
        this.dbContext = appContext.getDbContext();
        this.console = appContext.getConsole();
    }

    private Location location;
    protected AppContext appContext;
    protected DbContext dbContext;
    protected Console console;

    /**
     * Obtiene la ubicación que corresponde al panel.
     * @return Location
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Muestra el panel.
     * @param args Objeto que contiene los datos de transición provistos por el
     * último panel.
     * @return Instancia de objeto PanelTransitionArgs
     */
    public abstract PanelTransitionArgs show(PanelTransitionArgs args);

    /**
     * Establece una nueva ubicación.
     * @param requestedLocation Ubicación solicitada
     * @return Objeto PanelTransitionArgs
     */
    protected PanelTransitionArgs setLocation(Location requestedLocation)
    {
        return setLocation(requestedLocation, null);
    }

    /**
     * Establece una nueva ubicación.
     * @param requestedLocation Ubicación solicitada
     * @param obj Objeto a transferir
     * @return Objeto PanelTransitionArgs
     */
    protected PanelTransitionArgs setLocation(
        Location requestedLocation,
        Object obj)
    {
        return new PanelTransitionArgs(requestedLocation, location, obj);
    }

    /**
     * Dirige a una nueva ubicación y regresa una vez que se terminen las
     * operaciones en él.
     * @param requestedLocation Ubicación solicitada
     * @return Objeto devuelto desde la ubicación solicitada
     */
    protected Object goTo(Location requestedLocation)
    {
        return goTo(requestedLocation);
    }

    /**
     * Dirige a una nueva ubicación transfiriendo un objeto y regresa una vez
     * que se terminen las operaciones en él.
     * @param requestedLocation Ubicación solicitada
     * @param obj Objeto a transferir
     * @return Objeto devuelto desde la ubicación solicitada
     */
    protected Object goTo(Location requestedLocation, Object obj)
    {
        var args = new PanelTransitionArgs(requestedLocation, location, obj);
        return appContext.goTo(args);
    }

    /**
     * Crea un menú de opciones.
     * @return Objeto Menú
     */
    protected Menu createMenu()
    {
        return new Menu(appContext.getScanner(), console, null);
    }

    /**
     * Crea un menú de opciones.
     * @param title Título del menú
     * @return Objeto menú
     */
    protected Menu createMenu(String title)
    {
        return new Menu(appContext.getScanner(), console, title);
    }

    /**
     * Muestra un menú de Si/No.
     * @param title Título del menú
     * @return Opción elegida (S o N)
     */
    protected String showYesNoMenu(String title)
    {
        return createMenu()
            .setTitle(title)
            .addItem("S", "Si")
            .addItem("N", "No")
            .show();
    }

    /* Funciones comunes entre paneles */

    /**
     * Permite selecciona un elemento de una arreglo.
     * @param <T> Tipo de elemento
     * @param items Arreglo de elementos
     * @param title Título de la tabla
     * @param header Cabecera de las columnas de la tabla
     * @return Elemento seleccionado o nulo si el arreglo no contiene elementos
     * o si no se escogió ninguno
     */
    protected <T> T selectFromList(T[] items, String title, String header)
    {
        // Declara la variable para almacenar el elemento seleccionado
        T selectedItem = null;

        // Verifica si la lista contiene más de un elemento
        if (items.length > 0)
        {
            // Crea y muestra un menú
            String option = createMenu(title)
                .setHeader(header)
                .addItems(items)
                //.addBlankLine()
                .addItem("v", "Volver al menú anterior")
                .show("Seleccione una opción");

            // Verifica si la opción escogida no es "Volver"
            if (!option.equalsIgnoreCase("v"))
            {
                // Obtiene el objeto correspondiente a la opción seleccionada
                // basandose en el valor de la opción convertida a entero
                int index = Integer.parseInt(option);
                selectedItem = items[index];
            }
        }
        else
        {
            // No hace nada más que imprimir un mensaje
            System.out.println("\nNo hay elementos disponibles");
        }

        return selectedItem;
    }

    /**
     * Permite seleccionar un tipo de cobro de una lista.
     * @return Tipo de cobro
     */
    protected FeeType selectFeeType()
    {
        // Crea un menú para seleccionar una categoría
        String selectedCategory;
        Menu menu = createMenu("Seleccione un tipo de cobros")
            .addItem("I", "Inscripciones")
            .addItem("M", "Mensualidades")
            .addItem("U", "Uniformes")
            .addItem("P", "Papelería")
            .addItem("X", "Mantenimiento")
            .addItem("E", "Eventos especiales")
            .addBlankLine()
            .addItem("V", "Volver al menú anterior");

        // Muestra el menú de seleción de categoría y espera por una opción
        selectedCategory = menu.show("Seleccione una opción");
        
        // Procesa la opción elegida y retorna el valor correspondiente
        switch (selectedCategory)
        {
            case "I": // Inscripciones"
                return FeeType.Enrollment;
            case "M": // Mensualidades"
                return FeeType.Monthly;
            case "U": // Uniformes"
                return FeeType.Uniform;
            case "P": // Cobros de papelería"
                return FeeType.Stationery;
            case "X": // Cobros de mantenimiento"
                return FeeType.Maintenance;
            case "E": // Eventos especiales"
                return FeeType.SpecialEvent;
            default: // Preterminado: volver al menú anterior
                return FeeType.Unknown;
        }
    }

    /**
     * Permite seleccionar un nivel educativo de una lista.
     * @return Objeto que representa un nivel educativo
     */
    protected EducationLevel selectEducationLevel()
    {
        EducationLevel[] levels;

        try
        {
            levels = dbContext.getEducationLevels();
        }
        catch (Exception e)
        {
            System.out.println(
                "Error al obtener los datos de los niveles educativos");
            
            return null;
        }

        return selectFromList(levels,
            "\nNiveles educativos",
            "[#] - Descripción");
    }

    /**
     * Permite selecciona un periodo escolar de una lista
     * @return Objeto que representa un periodo escolar
     */
    protected ScholarPeriod selectScholarPeriod()
    {
        // Declara las variables
        ScholarPeriod[] periods;

        // Bloque para intentar obtener los periodos escolares
        try
        {
            periods = dbContext.getScholarPeriods();
        }
        catch (Exception e)
        {
            System.out.println(
                "Error al obtener los datos de ciclos escolares");

            // Termina la función sin devolver nada
            return null;
        }

        // Crea y muestra un menú
        return selectFromList(periods,
            "\nCiclos escolares",
            "[#] - Ciclo|Fecha inicial|Fecha final");
    }
}
