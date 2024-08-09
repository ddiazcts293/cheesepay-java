package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.DbContext;
import com.axolutions.db.type.EducationLevel;
import com.axolutions.db.type.SchoolPeriod;
import com.axolutions.db.type.fee.EnrollmentFee;
import com.axolutions.db.type.fee.Fee;
import com.axolutions.db.type.fee.FeeType;
import com.axolutions.db.type.fee.MaintenanceFee;
import com.axolutions.db.type.fee.MonthlyFee;
import com.axolutions.db.type.fee.SpecialEventFee;
import com.axolutions.db.type.fee.StationeryFee;
import com.axolutions.db.type.fee.UniformFee;
import com.axolutions.util.Console;
import com.axolutions.util.Menu;
import java.time.Month;

public class PanelHelper
{
    private AppContext appContext;
    private Console console;
    private DbContext dbContext;

    public PanelHelper(AppContext appContext)
    {
        this.appContext = appContext;
        this.console = appContext.getConsole();
        this.dbContext = appContext.getDbContext();
    }

    /**
     * Crea un menú de opciones.
     * @return Objeto Menú
     */
    public Menu createMenu()
    {
        return new Menu(appContext.getScanner(), console, null);
    }

    /**
     * Crea un menú de opciones.
     * @param title Título del menú
     * @return Objeto menú
     */
    public Menu createMenu(String title)
    {
        return new Menu(appContext.getScanner(), console, title);
    }

    /**
     * Muestra un menú de Si/No.
     * @param title Título del menú
     * @return Opción elegida (S o N)
     */
    public String showYesNoMenu(String title)
    {
        return createMenu()
            .setTitle(title)
            .addItem("S", "Si")
            .addItem("N", "No")
            .show();
    }

    /**
     * Muestra un menú que permite seleccionar un elemento de una arreglo.
     * @param title Título de la tabla
     * @param header Cabecera de las columnas de la tabla
     * @param items Arreglo de elementos
     * @param <T> Tipo de elemento
     * @return Elemento seleccionado o nulo si el arreglo no contiene elementos
     * o si no se escogió ninguno
     */
    public <T> T selectFromList(String title, String header, T[] items)
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
                .addBlankLine()
                .addItem("V", "Volver al menú anterior")
                .show("Seleccione una opción");

            // Verifica si la opción escogida no es "Volver"
            if (!option.equalsIgnoreCase("V"))
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
     * Muestra un menú que permite seleccionar un tipo de cobro.
     * @return Tipo de cobro
     */
    public FeeType selectFeeType()
    {
        return selectFeeType("\nSeleccione un tipo de cobro");
    }

    /**
     * Muestra un menú que permite seleccionar un tipo de cobro.
     * @param title Título del menú
     * @return Tipo de cobro
     */
    public FeeType selectFeeType(String title)
    {
        // Crea un menú para seleccionar una categoría
        String selectedCategory;
        Menu menu = createMenu(title)
            .addItem("I", "Inscripciones")
            .addItem("M", "Mensualidades")
            .addItem("U", "Uniformes")
            .addItem("P", "Papelería")
            .addItem("X", "Mantenimiento")
            .addItem("E", "Eventos especiales")
            .addBlankLine()
            .addItem("V", "Volver al menú anterior");

        // Muestra el menú de seleción de categoría y espera por una opción
        selectedCategory = menu.show();
        
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
     * Muestra un menú que permite seleccionar un nivel educativo.
     * @return Objeto que representa un nivel educativo
     */
    public EducationLevel selectEducationLevel()
    {
        return selectEducationLevel("\nSeleccione un nivel educativo");
    }

    /**
     * Muestra un menú que permite seleccionar un nivel educativo.
     * @param title Título del menú
     * @return Objeto que representa un nivel educativo
     */
    public EducationLevel selectEducationLevel(String title)
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

        return selectFromList(title, "Descripción", levels);
    }

    /**
     * Muestra un menú que permite seleccionar un ciclo escolar.
     * @return Objeto que representa un ciclo escolar
     */
    public SchoolPeriod selectSchoolPeriod()
    {
        return selectSchoolPeriod("\nSeleccione un ciclo escolar");
    }

    /**
     * Muestra un menú que permite seleccionar un ciclo escolar.
     * @param title Título del menú
     * @return Objeto que representa un ciclo escolar
     */
    public SchoolPeriod selectSchoolPeriod(String title)
    {
        // Declara un arreglo para contener los ciclos escolares
        SchoolPeriod[] periods;

        // Intentar obtener los ciclos escolares
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

        // Retorna la selección hecha en el menú
        return selectFromList(title, "Ciclo|Fecha inicial|Fecha final", periods);
    }

    /**
     * Muestra un menú que permite seleccionar un cobro de inscripción para un
     * ciclo escolar determinado.
     * @param period Ciclo escolar
     * @param title Título del menú
     */
    public Fee selectEnrollmentFee(SchoolPeriod period, String title)
    {
        // Declara un arreglo para almacenar los cobros consultados
        EnrollmentFee[] fees;

        try
        {
            // Intenta obtener los cobros
            fees = dbContext.getEnrollmentFees(period.code);
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener los cobros de inscripciones");

            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            System.out.println(
                "\nNo hay cobros de inscripciones para el ciclo escolar " +
                "indicado");
            
            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "%s\n\n" +
            "Inscripciones\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s",
            title,
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate);

        // Crea una cabecera para la tabla
        String header = "Nivel educativo|Costo";

        // Crea un menú de selección
        Menu menu = createMenu(info);
        menu.setHeader(header);

        // Bucle que recorre el arreglo de cobros para crear cadenas formateadas
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información de cobro
            var item = fees[i];

            // Crea una cadena de texto para cada cobro
            String text = String.format("%s|$%.2f",
                item.level.description,
                item.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        // Añade una línea en blanco como separador
        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        // Muestra el menú
        String option = menu.show("Seleccione una opción");

        // Verifica si la opción elegida es "Volver"
        if (option.equalsIgnoreCase("V"))
        {
            // Termina la función
            return null;
        }

        // Obtiene el indice del cobro seleccionado convirtiendo la opción
        // elegida a número
        int index = Integer.parseInt(option);
        return fees[index];
    }

    /**
     * Muestra un menú que permite seleccionar un cobro de mensualidad para un 
     * ciclo escolar determinado.
     * @param period Ciclo escolar
     * @param level Nivel educativo
     * @param title Título del menú
     */
    public Fee selectMonthlyFee(
        SchoolPeriod period, 
        EducationLevel level, 
        String title)
    {
        // Declara un arreglo para almacenar los cobros consultados
        MonthlyFee[] fees;

        try
        {
            // Intenta obtener los cobros
            fees = dbContext.getMonthlyFees(period.code, level.code);
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener los cobros de mensualidades");
         
            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            System.out.println(
                "\nNo hay cobros de mensualidades para el ciclo escolar y " +
                "nivel educativo indicados");

            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "%s\n\n" +
            "Mensualidades\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n" +
            "Nivel educativo: %s",
            title,
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate,
            level);

        // Crea una cabecera para la tabla
        String header = "Mes|Es vacacional|Costo";

        // Crea un menú de selección
        Menu menu = createMenu(info);
        menu.setHeader(header);

        // Bucle que recorre el arreglo de cobros para crear cadenas formateadas
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información de cobro
            var item = fees[i];

            // Crea una cadena de texto para cada cobro
            String text = String.format("%s|%s|$%.2f",
                getMonthName(item.dueDate.getMonth()), // Mes
                item.isVacationMonth ? "si" : "no", // Mes vacacional
                item.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        // Añade una línea en blanco como separador
        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        // Muestra el menú
        String option = menu.show("Seleccione una opción");

        // Verifica si la opción elegida es "Volver"
        if (option.equalsIgnoreCase("V"))
        {
            return null;
        }

        // Obtiene el indice del cobro seleccionado convirtiendo la opción
        // elegida a número
        int index = Integer.parseInt(option);
        return fees[index];
    }

    /**
     * Muestra un menú que permite seleccionar un cobro de uniforme para un 
     * ciclo escolar determinado.
     * @param period Ciclo escolar
     * @param level Nivel educativo
     * @param title Título del menú
     */
    public Fee selectUniformFee(
        SchoolPeriod period, 
        EducationLevel level,
        String title)
    {
        // Declara un arreglo para almacenar los cobros consultados
        UniformFee[] fees;

        try
        {
            // Intenta obtener los cobros
            fees = dbContext.getUniformFees(period.code, level.code);
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener los cobros de uniformes");

            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            System.out.println(
                "\nNo hay cobros de uniformes para el ciclo escolar y nivel " +
                "educativo indicados");
         
            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "%s\n\n" +
            "Uniformes\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n" +
            "Nivel educativo: %s",
            title,
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate,
            level);

        // Crea una cabecera para la tabla
        String header = "Concepto|Talla|Tipo de uniforme|Costo";

        // Crea un menú de selección
        Menu menu = createMenu(info);
        menu.setHeader(header);

        // Bucle que recorre el arreglo de cobros para crear cadenas formateadas
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información de cobro
            var fee = fees[i];

            // Crea una cadena de texto para cada cobro
            String text = String.format("%s|%s|%s|$%.2f",
                fee.concept, // Concepto
                fee.size, // Talla
                fee.type, // Tipo
                fee.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        // Añade una línea en blanco como separador
        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        // Muestra el menú
        String option = menu.show("Seleccione una talla");
        
        // Verifica si la opción elegida es "Volver"
        if (option.equalsIgnoreCase("V"))
        {
            // Termina la función
            return null;
        }

        // Obtiene el indice del cobro seleccionado convirtiendo la opción
        // elegida a número
        int index = Integer.parseInt(option);
        return fees[index];
    }

    /**
     * Muestra un menú que permite seleccionar un cobro de papelería para un 
     * ciclo escolar determinado.
     * @param period Ciclo escolar
     * @param level Nivel educativo
     * @param title Título del menú
     */
    public Fee selectStationeryFee(
        SchoolPeriod period, 
        EducationLevel level,
        String title)
    {
        // Declara un arreglo para almacenar los cobros consultados
        StationeryFee[] fees;

        try
        {
            // Intenta obtener los cobros
            fees = dbContext.getStationeryFees(period.code, level.code);
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener los cobros de papelería");
            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            System.out.println(
                "\nNo hay cobros de papelería para el ciclo escolar y nivel " +
                "educativo indicados");

            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "%s\n\n" +
            "Papelería\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n" +
            "Nivel educativo: %s",
            title,
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate,
            level);

        // Crea una cabecera para la tabla
        String header = "Concepto|Grado|Costo";

        Menu menu = createMenu(info);
        menu.setHeader(header);

        // Bucle que recorre el arreglo de cobros para crear cadenas formateadas
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información del cobro
            var fee = fees[i];

            // Crea una cadena de texto para cada cobro
            String text = String.format("%s|%s|$%.2f",
                fee.concept, // Concepto
                fee.grade, // Grado
                fee.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        // Añade una línea en blanco como separador
        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        // Muestra el menú
        String option = menu.show("Seleccione una opción");
        
        // Verifica si la opción elegida es "Volver"
        if (option.equalsIgnoreCase("V"))
        {
            return null;
        }

        // Obtiene el indice del cobro seleccionado convirtiendo la opción
        // elegida a número
        int index = Integer.parseInt(option);
        return fees[index];
    }

    /**
     * Muestra un menú que permite seleccionar un cobro de mantenimiento para un
     * ciclo escolar determinado.
     * @param period Ciclo escolar
     * @param title Título del menú
     */
    public Fee selectMaintenanceFee(SchoolPeriod period, String title)
    {
        // Declara la variable para almacenar el cobro
        MaintenanceFee fee;

        try
        {
            // Intenta obtener el cobro de mantenimiento
            fee = dbContext.getMaintenanceFee(period.code);
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener el cobro de mantenimiento");
            
            // Termina la función
            return null;
        }

        // Verifica si no se obtuvo un cobro de mantenimiento
        if (fee == null)
        {
            // Avisa del error
            System.out.println(
                "\nNo hay un cobro de mantenimiento para el ciclo escolar " +
                " indicado");

            // Termina la función
            return null;
        }

        // Imprime la información del cobro
        System.out.printf(
            "%s\n\n" +
            "Costos de mantenimiento\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n" +
            "Concepto: %s\n" +
            "Costo: $%.2f\n",
            title,
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate,
            fee.concept,
            fee.cost);

        // Muestra un menú para confirmar la selección
        String option = showYesNoMenu("\n¿Desea seleccionar este cobro?");

        // Verifica si la opción elegida es "Si"
        if (option.equalsIgnoreCase("S"))
        {
            // Termina la función retornando el cobro
            return fee;
        }

        // Retorna ningún elemento
        return null;
    }

    /**
     * Muestra un menú que permite seleccionar un cobro de evento especial para
     * un ciclo escolar determinado.
     * @param period Ciclo escolar
     * @param title Título del menú
     */
    public Fee selectSpecialEventFee(SchoolPeriod period, String title)
    {
        // Declara un arreglo para almacenar los cobros consultados
        SpecialEventFee[] fees;

        try
        {
            // Intenta obtener los cobros
            fees = dbContext.getSpecialEventFees(period.code);
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener los cobros de eventos " +
                "especiales");

            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            System.out.println(
                "No hay cobros de eventos especiales para el ciclo escolar " +
                "indicado");

            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "%s\n\n" +
            "Eventos especiales\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s",
            title,
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate);

        // Crea una cabecera para la tabla
        String header = "Concepto|Fecha programada|Costo";

        Menu menu = createMenu(info);
        menu.setHeader(header);

        // Bucle que recorre la lista de cobros
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información del cobro
            var fee = fees[i];

            // Crea una cadena de texto formateada para cada cobro
            String text = String.format("%s|%s|$%.2f",
                fee.concept, // Concepto
                fee.scheduledDate, // Fecha programada
                fee.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        // Añade una línea en blanco como separador
        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        // Muestra el menú
        String option = menu.show("Seleccione una opción");
        
        // Verifica si la opción elegida es "Volver"
        if (option.equalsIgnoreCase("V"))
        {
            return null;
        }

        // Obtiene el indice del cobro seleccionado convirtiendo la opción
        // elegida a número
        int index = Integer.parseInt(option);
        return fees[index];
    }

    /**
     * Obtiene el nombre de un mes en español.
     * @param month Mes
     * @return Cadena
     */
    public String getMonthName(Month month)
    {
        switch (month)
        {
            case JANUARY:
                return "enero";
            case FEBRUARY:
                return "febrero";
            case MARCH:
                return "marzo";
            case APRIL:
                return "abril";
            case MAY:
                return "mayo";
            case JUNE:
                return "junio";
            case JULY:
                return "julio";
            case AUGUST:
                return "agosto";
            case SEPTEMBER:
                return "septiembre";
            case OCTOBER:
                return "octubre";
            case NOVEMBER:
                return "noviembre";
            case DECEMBER:
                return "diciembre";
            default:
                return "";
        }
    }
}
