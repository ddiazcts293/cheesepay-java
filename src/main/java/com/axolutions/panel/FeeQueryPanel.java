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

        // Crea un menú y le añade algunas opciones
        String option;
        Menu menu = createMenu("Filtrado de cobros")
            .addItem("I", "Inscripciones")
            .addItem("M", "Mensualidades")
            .addItem("U", "Uniformes")
            .addItem("P", "Papelería")
            .addItem("X", "Mantenimiento")
            .addItem("E", "Eventos especiales")
            .addItem("V", "Volver al menú principal");

        // Bucle que repite el menú luego de haber selecciona una opción
        do
        {
            // Solicita al usuario que seleccione un ciclo escolar
            ScholarPeriod selectedPeriod = selectScholarPeriod();

            // Verifica si no se seleccionó un ciclo
            if (selectedPeriod == null)
            {
                // De ser así, termina el bucle
                break;
            }

            do
            {
                // Muestra el menú de seleción de categoría y espera por una
                // opción
                option = menu.show("Seleccione una opción");

                // Procesa la opción elegida
                switch (option)
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
            
            // Es repitido mientras no se elija "Volver"
            } while (!option.equalsIgnoreCase("V"));

        // Es repetido infinitamente
        } while (true);

        return null;
    }

    /**
     * Muestra el costo de inscripción para un ciclo escolar determinado.
     * @param period Ciclo escolar
     */
    private void showEnrollmentFees(ScholarPeriod period)
    {
        do
        {
            System.out.println("Inscripciones");

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

            // Bloque para intentar obtener el cobro de inscripcion
            try
            {
                fee = dbContext.getEnrollmentFee(period, level);
            }
            catch (Exception e)
            {
                // Avisa del error
                System.out.println(
                    "Error al intentar obtener el costo de inscripción");

                // Termina la función
                return;
            }

            // Verifica si no se obtuvo un cobro de inscripción
            if (fee == null || fee.enrollment == null)
            {
                    // Avisa del error
                    System.out.println(
                    "No hay cobros de inscripción disponibles");

                // Termina la función
                return;
            }

            // Crea una cadena fomateada
            String info = String.format(
                "Costo de inscripción\n\n" +
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

            // Imprime la información del cobro
            System.out.println(info);
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
        do
        {
            System.out.println("Mensualidades");

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

            // Bloque para intentar obtener el cobro de inscripcion
            try
            {
                fees = dbContext.getMonthlyFees(period, level);
            }
            catch (Exception e)
            {
                // Avisa del error
                System.out.println(
                    "Error al intentar obtener los costos de mensualidades");

                // Termina la función
                return;
            }

            // Verifica si no se obtuvieron cobros de mensualidad
            if (fees.length == 0)
            {
                    // Avisa del error
                    System.out.println(
                    "No hay cobros de mensualidad disponibles");

                // Termina la función
                return;
            }

            // Crea una cabecera para la tabla
            String header = "Mes|Es vacacional|Costo";

            // Crea un arreglo de cadenas para colocar cada una de las
            // mensualidades
            String[] list = new String[fees.length];

            // Bucle que recorre la lista de costos para crear cadenas formateadas
            for (int i = 0; i < fees.length; i++)
            {
                var item = fees[i];

                // Crea una cadena de texto para cada costo
                list[i] = String.format("%s|%s|$%.2f",
                    getMonthName(item.monthly.month), // Mes
                    item.monthly.isVacationMonth ? "si" : "no", // Mes vacacional
                    item.monthly.cost); // Costo
            }

            String info = String.format(
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

            System.out.println(info);
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
        do
        {
            System.out.println("Uniformes");

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

            // Bloque para intentar obtener las tarifas o cobros
            try
            {
                fees = dbContext.getUniformFees(period, level);
            }
            catch (Exception e)
            {
                // Avisa del error
                System.out.println(
                    "Error al intentar obtener los costos de uniforme");

                // Termina la función
                return;
            }

            // Verifica si no se obtuvieron tarifas
            if (fees.length == 0)
            {
                    // Avisa del error
                    System.out.println(
                    "No hay cobros de uniformes disponibles");

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

            String info = String.format(
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

            System.out.println(info);
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
        do
        {
            System.out.println("Papelería");

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

            // Bloque para intentar obtener el cobro de papeleria
            try
            {
                fees = dbContext.getStationeryFees(period, level);
            }
            catch (Exception e)
            {
                // Avisa del error
                System.out.println(
                    "Error al intentar obtener los costos de papelería");

                // Termina la función
                return;
            }

            // Verifica si no se obtuvieron cobros
            if (fees.length == 0)
            {
                // Avisa del error
                System.out.println("No hay cobros de papelería disponibles");

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

            String info = String.format(
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

            System.out.println(info);
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
                "Error al intentar obtener los costos de mantenimiento");

            // Termina la función
            return;
        }

        // Verifica si no se obtuvo un cobro de mantenimiento
        if (fee == null || fee.maintenance == null)
        {
                // Avisa del error
                System.out.println(
                "No hay cobro un de mantenimiento disponible");

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
        System.out.println("Eventos especiales");
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
