package com.axolutions.panel;

import java.time.Month;

import com.axolutions.AppContext;
import com.axolutions.db.type.*;
import com.axolutions.db.type.fee.*;
import com.axolutions.util.Menu;

public class FeeQueryPanel extends BasePanel
{
    /**
     * DOING: Panel de consulta de cobros
     *
     * En este panel se podrán consultar los costos de cada cobro disponible,
     * según la categoría, ciclo escolar y nivel educativo
     *
     * ALGORITMO:
     * 1. Inicio
     * 2. Preguntar la categoría de pago, ciclo y nivel educativo (si aplica)
     * 3. Mostrar precios de cobros
     * 4. Fin
     *
     * CONSULTAS:
     * - Obtener, consultas individuales, los listados de costos de los
     *   cobros para cada ciclo escolar y nivel educativo.
     *   Datos: identificador de cobro, concepto (si lo tiene), costo, fechas
     *   de periodo escolar y descripción de nivel educativo (si lo tiene)
     *
     * - Obtener la cantidad de pagos realizados para cada categoria agrupado
     *   por ciclo escolar
     *   Datos: categoria de cobro, fechas de perido escolar, cantidad de pagos
     *
     * - Obtener el costo de un paquete de papeleria para un grado, nivel
     *   educativo y ciclo escolar
     *   Datos: identificador de cobro, concepto, grado, costo, fechas
     *   de periodo escolar y descripción de nivel educativo
     *
     * - Obtener el costo de un uniforme para un nivel educativo, tipo y ciclo
     *   escolar
     *   Datos: identificador de cobro, concepto, talla, descripción de tipo de
     *   uniforme, periodo escolar y nivel educativo
     */

    public FeeQueryPanel(AppContext appContext)
    {
        super(appContext, Location.FeeQueryPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        System.out.println("Consulta de cobros");

        // Crea un menú para seleccionar una categoría
        String selectedCategory;
        Menu menu = createMenu("Filtrado de cobros")
            .addItem("I", "Inscripciones")
            .addItem("M", "Mensualidades")
            .addItem("U", "Uniformes")
            .addItem("P", "Papelería")
            .addItem("X", "Mantenimiento")
            .addItem("E", "Eventos especiales")
            .addItem("V", "Volver al menú principal");

        // Bucle que repite el menú de categoría al momento de regresar desde
        // el menú de selección de ciclo escolar
        do
        {
            // Muestra el menú de seleción de categoría y espera por una opción
            selectedCategory = menu.show("Seleccione una opción");

            // Verifica si se eligió "Volver al menú principal"
            if (selectedCategory.equalsIgnoreCase("v"))
            {
                // Termina el menú
                break;
            }

            // Bucle que repite el menú de selección de ciclo escolar cada vez 
            // que se solicita regresar desde las secciones de cada categoría
            do
            {
                // Solicita al usuario que seleccione un ciclo escolar
                ScholarPeriod selectedPeriod = selectScholarPeriod();

                // Verifica si no se seleccionó un ciclo
                if (selectedPeriod == null)
                {
                    // De ser así, repite el ciclo y regresa al menú de categoría
                    break;
                }

                // Procesa la opción elegida y navega hacia el menú correspondiente
                switch (selectedCategory)
                {
                    case "I": // Inscripciones"
                        showEnrollmentFees(selectedPeriod);
                        break;
                    case "M": // Mensualidades"
                        showMonthlyFees(selectedPeriod);
                        break;
                    case "U": // Uniformes"
                        showUniformFees(selectedPeriod);
                        break;
                    case "P": // Cobros de papelería"
                        showStationeryFees(selectedPeriod);
                        break;
                    case "X": // Cobros de mantenimiento"
                        showMaintenanceFees(selectedPeriod);
                        break;
                    case "E": // Eventos especiales"
                        showSpecialEventsFees(selectedPeriod);
                        break;
                    default:
                        break;
                }
            
            // Bucle repetido infinitamente
            } while (true);

        // Bucle repetido infinitamente
        } while (true);

        return null;
    }

    /**
     * Muestra el costo de inscripción para un ciclo escolar determinado.
     * @param period Ciclo escolar
     */
    private void showEnrollmentFees(ScholarPeriod period)
    {
        // Bucle que repite el menú de selección de nivel educativo
        do
        {
            // Solicita que se elija un nivel educativo
            var level = selectEducationLevel();
            // Verifica si no se escogió uno
            if (level == null)
            {
                // De ser así, finaliza la función
                return;
            }

            // Declara una variable para almacenar el cobro consultado
            Fee fee = null;

            // Bloque para intentar obtener el cobro
            try
            {
                fee = dbContext.getEnrollmentFee(period, level);
            }
            catch (Exception e)
            {
                // Avisa del error
                System.out.println(
                    "Error al consultar información de cobro de inscripción");

                // Termina la función
                return;
            }

            // Verifica si no se obtuvo un cobro
            if (fee == null || fee.enrollment == null)
            {
                // Avisa del error
                System.out.println("No hay ningún cobro de inscripción disponible");

                // Termina la función
                return;
            }

            // Imprime la información del cobro
            System.out.printf(
                "Costos de inscripciones\n\n" +
                "Ciclo escolar: %d-%d\n" +
                "Fecha de inicio de curso: %s\n" +
                "Fecha de fin de curso: %s\n" +
                "Nivel educativo: %s\n" +
                "Costo: $%.2f\n",
                fee.period.startingDate.getYear(),
                fee.period.endingDate.getYear(),
                fee.period.startingDate,
                fee.period.endingDate,
                fee.enrollment.level,
                fee.enrollment.cost);

            console.pause("Presione ENTER para continuar...");

        // Repite infinitamente
        } while (true);
    }

    /**
     * Muestra los costos de las mensualidades para un ciclo escolar determinado.
     * @param period Ciclo escolar
     */
    private void showMonthlyFees(ScholarPeriod period)
    {
        // Bucle que repite el menú de selección de nivel educativo
        do
        {
            // Solicita que se elija un nivel educativo
            var level = selectEducationLevel();
            // Verifica si no se escogió uno
            if (level == null)
            {
                // De ser así, finaliza la función
                return;
            }

            // Declara una variable para almacenar los cobros
            Fee[] fees;

            // Bloque para intentar obtener los cobros
            try
            {
                fees = dbContext.getMonthlyFees(period, level);
            }
            catch (Exception e)
            {
                // Avisa del error
                System.out.println("Error al consultar costos de mensualidad");

                // Termina la función
                return;
            }

            // Verifica si no se obtuvieron cobros
            if (fees.length == 0)
            {
                    // Avisa del error
                    System.out.println(
                    "No hay costos de mensualidad disponibles");

                // Termina la función
                return;
            }

            // Crea una cabecera para la tabla
            String header = "Mes|Es vacacional|Costo";

            // Crea un arreglo de cadenas para colocar cada una de los cobros
            String[] list = new String[fees.length];

            // Bucle que recorre la lista de cobros para crear cadenas formateadas
            for (int i = 0; i < fees.length; i++)
            {
                var item = fees[i];

                // Crea una cadena de texto para cada cobro
                list[i] = String.format("%s|%s|$%.2f",
                    getMonthName(item.monthly.month), // Mes
                    item.monthly.isVacationMonth ? "si" : "no", // Mes vacacional
                    item.monthly.cost); // Costo
            }

            // Imprime la información común
            System.out.printf(
                "Costos de mensualidades\n\n" +
                "Ciclo escolar: %d-%d\n" +
                "Fecha de inicio de curso: %s\n" +
                "Fecha de fin de curso: %s\n" +
                "Nivel educativo: %s\n",
                period.startingDate.getYear(),
                period.endingDate.getYear(),
                period.startingDate,
                period.endingDate,
                level);

            // Imprime la tabla de cobros
            console.printAsTable(header, list);
            console.pause("Presione ENTER para continuar...");

        // Repite infinitamente
        } while (true);
    }

    /**
     * Muestra los costos de los uniformes para un ciclo escolar determinado.
     * @param period Ciclo escolar
     */
    private void showUniformFees(ScholarPeriod period)
    {
        // Bucle que repite el menú de selección de nivel educativo
        do
        {
            // Solicita que se elija un nivel educativo
            var level = selectEducationLevel();
            // Verifica si no se escogió uno
            if (level == null)
            {
                // De ser así, finaliza la función
                return;
            }

            // Declara una variable para almacenar los cobros consultados
            Fee[] fees;

            // Bloque para intentar obtener los cobros
            try
            {
                fees = dbContext.getUniformFees(period, level);
            }
            catch (Exception e)
            {
                // Avisa del error
                System.out.println(
                    "Error al consultar costos de uniforme");

                // Termina la función
                return;
            }

            // Verifica si no se obtuvieron tarifas
            if (fees.length == 0)
            {
                    // Avisa del error
                    System.out.println(
                    "No hay costos de uniformes disponibles");

                // Termina la función
                return;
            }

            // Crea una cabecera para la tabla
            String header = "Concepto|Talla|Tipo de uniforme|Costo";

            // Crea un arreglo de cadenas para colocar cada una de las
            // mensualidades
            String[] list = new String[fees.length];

            // Bucle que recorre la lista de pagos para crear cadenas formateadas
            for (int i = 0; i < fees.length; i++)
            {
                // Objeto con la información de cobro
                var fee = fees[i];

                // Crea una cadena de texto para cada costo
                list[i] = String.format("%s|%s|%s|$%.2f",
                    fee.uniform.concept, // Concepto
                    fee.uniform.size, // Talla
                    fee.uniform.type, // Tipo
                    fee.uniform.cost); // Costo
            }

            // Imprime información común
            System.out.printf(
                "Costos de uniformes\n\n" +
                "Ciclo escolar: %d-%d\n" +
                "Fecha de inicio de curso: %s\n" +
                "Fecha de fin de curso: %s\n" +
                "Nivel educativo: %s\n",
                period.startingDate.getYear(),
                period.endingDate.getYear(),
                period.startingDate,
                period.endingDate,
                level);

            // Imprime la tabla de cobros
            console.printAsTable(header, list);
            console.pause("Presione ENTER para continuar...");

        // Repite infinitamente
        } while (true);
    }

    /**
     * Muestra los costos de papelería para un ciclo escolar determinado.
     * @param period Ciclo escolar
     */
    private void showStationeryFees(ScholarPeriod period)
    {
        // Bucle para repetir la selección de nivel educativo
        do
        {
            // Solicita que se elija un nivel educativo
            var level = selectEducationLevel();
            // Verifica si no se escogió uno
            if (level == null)
            {
                // De ser así, finaliza la función
                return;
            }

            // Declara una variable para almacenar los cobros consultados
            Fee[] fees;

            // Bloque para intentar obtener los cobros de papeleria
            try
            {
                fees = dbContext.getStationeryFees(period, level);
            }
            catch (Exception e)
            {
                // Avisa del error
                System.out.println(
                    "Error al consultar costos de papelería");

                // Termina la función
                return;
            }

            // Verifica si no se obtuvieron cobros
            if (fees.length == 0)
            {
                // Avisa del error
                System.out.println("No hay costos de papelería disponibles");

                // Termina la función
                return;
            }

            // Crea una cabecera para la tabla
            String header = "Concepto|Grado|Costo";

            // Crea un arreglo de cadenas para colocar cada una de las
            // mensualidades
            String[] list = new String[fees.length];

            // Bucle que recorre la lista de pagos para crear cadenas formateadas
            for (int i = 0; i < fees.length; i++)
            {
                // Objeto con la información del cobro
                var item = fees[i];

                // Crea una cadena de texto para cada costo
                list[i] = String.format("%s|%s|$%.2f",
                    item.stationery.concept, // Concepto
                    item.stationery.grade, // Grado
                    item.stationery.cost); // Costo
            }

            // Imprime información común
            System.out.printf(
                "Costos de papelería\n\n" +
                "Ciclo escolar: %d-%d\n" +
                "Fecha de inicio de curso: %s\n" +
                "Fecha de fin de curso: %s\n" +
                "Nivel educativo: %s\n",
                period.startingDate.getYear(),
                period.endingDate.getYear(),
                period.startingDate,
                period.endingDate,
                level);

            // Imprime la tabla de cobros 
            console.printAsTable(header, list);
            console.pause("Presione ENTER para continuar...");

        // Repite infinitamente
        } while (true);
    }

    /**
     * Muestra los costos de mantenimiento para un ciclo escolar determinado.
     * @param period
     */
    private void showMaintenanceFees(ScholarPeriod period)
    {
        System.out.println("Mantenimiento");

        // Declara una variable para almacenar el cobro consultado
        Fee fee;

        // Bloque para intentar obtener el cobro de mantenimiento
        try
        {
            fee = dbContext.getMaintenanceFee(period);
        }
        catch (Exception e)
        {
            // Avisa del error
            System.out.println(
                "Error al consultar costos de mantenimiento");

            // Termina la función
            return;
        }

        // Verifica si no se obtuvo un cobro de mantenimiento
        if (fee == null || fee.maintenance == null)
        {
                // Avisa del error
                System.out.println(
                "No hay costos de mantenimiento disponible");

            // Termina la función
            return;
        }

        String info = String.format(
            "Costos de mantenimiento\n\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n" +
            "Concepto: %s\n" +
            "Costo: $%.2f\n",
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate,
            fee.maintenance.concept,
            fee.maintenance.cost);

        System.out.println(info);

        console.pause("Presione ENTER para continuar...");
    }

    /**
     * Muestra los costos de eventos especiales para un ciclo escolar 
     * determinado.
     * @param period
     */
    private void showSpecialEventsFees(ScholarPeriod period)
    {
        // Declara una variable para almacenar los cobros consultados
        Fee[] fees;

        // Bloque para intentar obtener los cobros
        try
        {
            fees = dbContext.getSpecialEventFees(period);
        }
        catch (Exception e)
        {
            // Avisa del error
            System.out.println("Error al consultar costos de eventos especiales");

            // Termina la función
            return;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            // Avisa de que no hay cobros
            System.out.println("No hay costos de eventos especiales disponibles");

            // Termina la función
            return;
        }

        // Crea una cabecera para la tabla
        String header = "Concepto|Fecha programada|Costo";

        // Crea un arreglo de cadenas para colocar las cadenas formateadas
        String[] list = new String[fees.length];

        // Bucle que recorre la lista de cobros
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información del cobro
            var item = fees[i];

            // Crea una cadena de texto formateada para cada costo
            list[i] = String.format("%s|%s|$%.2f",
                item.specialEvent.concept, // Concepto
                item.specialEvent.scheduledDate, // Fecha programada
                item.specialEvent.cost); // Costo
        }

        // Muestra la información común para no mostrar datos repetidos
        System.out.printf(
            "Costos de eventos especiales\n\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n\n",
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate);

        // Imprime una tabla de los cobros
        console.printAsTable(header, list);
        console.pause("Presione ENTER para continuar...");
    }

    /* Funciones auxiliares */

    /**
     * Obtiene el nombre de un mes en español.
     * @param month Mes
     * @return Cadena
     */
    private String getMonthName(Month month)
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
