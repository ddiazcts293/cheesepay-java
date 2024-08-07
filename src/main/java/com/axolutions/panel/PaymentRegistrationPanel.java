package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.*;
import com.axolutions.db.type.fee.*;
import com.axolutions.util.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.Month;

public class PaymentRegistrationPanel extends BasePanel
{
    /**
     * DOING: Panel de registro de pagos
     *
     * En este panel se podrán registrar todos los pagos que un alumno efectue
     *
     * ALGORITMO
     * 1. Inicio
     * 2. Preguntar si se conoce la matricula del alumno, si se conoce,
     *    solicitarla; sino, buscar en el panel de busqueda.
     * 3. Seleccionar el tutor que registra el pago
     * 4. Preguntar qué desea pagar
     * 5. Preguntar por el ciclo escolar (por defecto se utilizará el actual)
     * 6. Preguntar si desea agregar otro pago
     * 7. Mostrar el total y pedir confirmación
     * 8. Registrar el pago
     * 9. Si el pago es de inscripción, entonces el alumno deberá ser asignado
     *    a un grupo y nivel educativo
     * 10. Fin
     *
     * CONSULTAS
     * - Registrar un pago que contiene diferentes cobros
     * - Registrar a un alumno en un grupo
     */

    public PaymentRegistrationPanel(AppContext appContext)
    {
        super(appContext, Location.PaymentRegistrationPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        System.out.println("Panel de registro de pagos");

        boolean isNewStudent = false;
        Student student = null;
        Tutor selectedTutor = null;
        Tutor[] tutors;

        // Obtiene la instancia del alumno transferido desde otro panel
        if (args.getObj() instanceof Student)
        {
            student = (Student)args.getObj();
        }
        else
        {
            System.out.println("Este panel siempre debe recibir la " +
                "información de un alumno");

            return null;
        }

        try
        {
            // Intenta obtener la lista de tutores registrados
            tutors = dbContext.getStudentTutors(student.studentId);
            // Intenta averiguar si el alumno debe pagar inscripción
            isNewStudent = dbContext.checkForEnrollmentNeeded(
                student.studentId);
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener la información del alumno");
            return null;
        }

        // Verifica si el alumno no tiene tutores registrados
        if (tutors.length == 0)
        {
            System.out.println(
                "Para poder registrar un pago, es necesario que el alumno " +
                "tenga registrado, al menos, a un tutor.");

            console.pause("Presione ENTER para regresar al menú principal");
            return null;
        }

        // Solicita que se seleccione a un tutor
        selectedTutor = selectFromList(
            tutors,
            "Seleccione a un tutor",
            "[#] - Parentesco|Nombre|Correo electronico|RFC");

        // Verifica si no se seleccionó a un tutor
        if (selectedTutor == null)
        {
            return null;
        }

        if (isNewStudent)
        {
            registerNewStudentPayment(student, selectedTutor);
        }
        else
        {
            registerPayment(student, selectedTutor);
        }

        return null;
    }

    /**
     * Registra un pago.
     * @param student Alumno
     * @param tutor Tutor
     */
    private void registerPayment(Student student, Tutor tutor)
    {
        HashMap<String, Fee> fees = new HashMap<>();
        ArrayList<String> feeStrings = new ArrayList<>();

        float totalAmount = 0f;
        FeeType feeType;
        String option;
        Group group;
        Fee fee;

        // Intenta obtener el grupo actual del alumno
        try
        {
            group = dbContext.getStudentCurrentGroup(student.studentId);
        }
        catch (Exception e)
        {
            System.out.println(
                "Error al intentar obtener la información del grupo " +
                "actual del alumno");

            return;
        }

        // Bucle para repetir el agregado de cobros
        do
        {
            // Pregunta por el tipo de cobro a realizar
            feeType = selectFeeType();
            if (feeType == FeeType.Unknown)
            {
                break;
            }

            // Hace el pago correspondiente
            switch (feeType)
            {
                // Inscripciones
                case Enrollment:
                {
                    fee = selectEnrollmentFee(group.period);

                    if (fee != null && !fees.containsKey(fee.code))
                    {
                        String line = String.format(
                            "Inscripció para %s (%d-%d)|$%.2f",
                            fee.monthly.level.description, // nivel
                            fee.period.startingDate.getYear(),
                            fee.period.endingDate.getYear(),
                            fee.monthly.cost); // costo

                        fees.put(fee.code, fee);
                        feeStrings.add(line);
                        totalAmount += fee.monthly.cost;
                    }
                    break;
                }
                // Mensualidades
                case Monthly:
                    fee = selectMonthlyFee(group.period, group.level);

                    if (fee != null && !fees.containsKey(fee.code))
                    {
                        String line = String.format(
                            "Pago mensualidad %s para %s (%d-%d)|$%.2f",
                            getMonthName(fee.monthly.month), // Mes
                            fee.monthly.level.description, // nivel
                            fee.period.startingDate.getYear(),
                            fee.period.endingDate.getYear(),
                            fee.monthly.cost); // costo

                        fees.put(fee.code, fee);
                        feeStrings.add(line);
                        totalAmount += fee.monthly.cost;
                    }
                    break;
                // Uniformes
                case Uniform:
                    fee = selectUniformFee(group.period, group.level);

                    if (fee != null && !fees.containsKey(fee.code))
                    {
                        String line = String.format(
                            "%s (%s) para %s|$%.2f",
                            fee.uniform.concept,
                            fee.uniform.size,
                            fee.uniform.level.description,
                            fee.uniform.cost);

                        fees.put(fee.code, fee);
                        feeStrings.add(line);
                        totalAmount += fee.uniform.cost;
                    }
                    break;
                // Papeleria
                case Stationery:
                    fee = selectStationeryFee(group.period, group.level);

                    if (fee != null && !fees.containsKey(fee.code))
                    {
                        String line = String.format(
                            "%s para %s|$%.2f",
                            fee.stationery.concept,
                            fee.stationery.level.description,
                            fee.stationery.cost);

                        fees.put(fee.code, fee);
                        feeStrings.add(line);
                        totalAmount += fee.stationery.cost;
                    }
                    break;
                case SpecialEvent:
                    fee = selectSpecialEventFee(group.period);

                    if (fee != null && !fees.containsKey(fee.code))
                    {
                        String line = String.format(
                            "%s %s|$%.2f",
                            fee.specialEvent.concept,
                            fee.specialEvent.scheduledDate,
                            fee.specialEvent.cost);

                        fees.put(fee.code, fee);
                        feeStrings.add(line);
                        totalAmount += fee.specialEvent.cost;
                    }
                    break;
                case Maintenance:
                    fee = selectMaintenanceFee(group.period);

                    if (fee != null && !fees.containsKey(fee.code))
                    {
                        String line = String.format(
                            "%s|$%.2f",
                            fee.maintenance.concept,
                            fee.maintenance.cost);

                        fees.put(fee.code, fee);
                        feeStrings.add(line);
                        totalAmount += fee.maintenance.cost;
                    }
                    break;
                default:
                    break;
            }

            // Pregunta si se desea realiza otro pago
            String header = "Concepto|Costo";
            console.printAsTable(header, feeStrings.toArray());
            System.out.printf("Total: $%.2f", totalAmount);

            option = showYesNoMenu("¿Desea agregar otro cobro?");

        // Repite el bucle mientras se elija "Si"
        } while (option.equalsIgnoreCase("S"));
    }

    /**
     * Registra un pago para un alumno de nuevo ingreso.
     * @param student Alumno
     * @param tutor Tutor
     */
    private void registerNewStudentPayment(Student student, Tutor tutor)
    {
        Group[] groups;
        Group selectedGroup = null;
        EducationLevel selectedLevel = null;
        ScholarPeriod currentPeriod = null;
        int grade;

        // Solicita un nivel educativo
        selectedLevel = selectEducationLevel();
        // Verifica si no se seleccionó un nivel educativo
        if (selectedLevel == null)
        {
            // Termina el proceso
            return;
        }

        System.out.printf(
            "\nPagos de alumno de nuevo ingreso ciclo escolar: %d-%d\n",
            currentPeriod.startingDate.getYear(),
            currentPeriod.endingDate.getYear());

        try
        {
            // Intenta obtener los grupos existentes para el nivel educativo
            // seleccionado en el ciclo escolar actual
            groups = dbContext.getGroups(currentPeriod.code, selectedLevel.code);
        }
        catch (Exception e)
        {
            System.out.println(
                "Error al obtener los grupos en el ciclo actual y nivel " +
                "educativo seleccionado");

            return;
        }
    }

    /**
     * Selecciona un cobro de inscripción para un ciclo escolar determinado.
     * @param period Ciclo escolar
     */
    private Fee selectEnrollmentFee(ScholarPeriod period)
    {
        // Declara una variable para almacenar los cobros consultados
        Fee[] fees;

        // Bloque para intentar obtener el cobro
        try
        {
            fees = dbContext.getEnrollmentFees(period.code);
        }
        catch (Exception e)
        {
            // Avisa del error
            System.out.println(
                "Error al consultar información de cobro de inscripción");

            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            // Avisa del error
            System.out.println("No hay costos de inscripciones disponibles");
            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "Inscripciones\n\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s",
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate);

        // Crea una cabecera para la tabla
        String header = "Nivel educativo|Costo";

        // Crea un menú de selección
        Menu menu = createMenu(info);
        menu.setHeader(header);

        // Bucle que recorre la lista de cobros para crear cadenas formateadas
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información de cobro
            Fee fee = fees[i];

            // Crea una cadena de texto para cada cobro
            String text = String.format("%s|$%.2f",
                fee.enrollment.level.description,
                fee.enrollment.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        String option = menu.show("Seleccione una talla");
        if (option.equalsIgnoreCase("V"))
        {
            return null;
        }

        int index = Integer.parseInt(option);
        return fees[index];
    }

    /**
     * Selecciona un cobro de mensualidad para un ciclo escolar determinado.
     * @param period Ciclo escolar
     * @param level Nivel educativo
     */
    private Fee selectMonthlyFee(ScholarPeriod period, EducationLevel level)
    {
        // Declara una variable para almacenar los cobros
        Fee[] fees;

        // Bloque para intentar obtener los cobros
        try
        {
            fees = dbContext.getMonthlyFees(period.code, level.code);
        }
        catch (Exception e)
        {
            // Avisa del error
            System.out.println("Error al consultar costos de mensualidades");
            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            // Avisa del error
            System.out.println("No hay costos de mensualidades disponibles");
            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "Mensualidades\n\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n" +
            "Nivel educativo: %s",
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

        // Bucle que recorre la lista de cobros para crear cadenas formateadas
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información de cobro
            Fee fee = fees[i];

            // Crea una cadena de texto para cada cobro
            String text = String.format("%s|%s|$%.2f",
                getMonthName(fee.monthly.month), // Mes
                fee.monthly.isVacationMonth ? "si" : "no", // Mes vacacional
                fee.monthly.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        String option = menu.show("Seleccione una talla");
        if (option.equalsIgnoreCase("V"))
        {
            return null;
        }

        int index = Integer.parseInt(option);
        return fees[index];
    }

    /**
     * Selecciona un cobro de uniforme para un ciclo escolar determinado.
     * @param period Ciclo escolar
     * @param level Nivel educativo
     */
    private Fee selectUniformFee(ScholarPeriod period, EducationLevel level)
    {
        // Declara una variable para almacenar los cobros consultados
        Fee[] fees;

        // Bloque para intentar obtener los cobros
        try
        {
            fees = dbContext.getUniformFees(period.code, level.code);
        }
        catch (Exception e)
        {
            // Avisa del error
            System.out.println("Error al consultar costos de uniformes");
            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            // Avisa del error
            System.out.println("No hay costos de uniformes disponibles");
            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "Uniformes\n\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n" +
            "Nivel educativo: %s", +
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

        // Bucle que recorre la lista de cobros para crear cadenas formateadas
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información de cobro
            Fee fee = fees[i];

            // Crea una cadena de texto para cada cobro
            String text = String.format("%s|%s|%s|$%.2f",
                fee.uniform.concept, // Concepto
                fee.uniform.size, // Talla
                fee.uniform.type, // Tipo
                fee.uniform.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        String option = menu.show("Seleccione una talla");
        if (option.equalsIgnoreCase("V"))
        {
            return null;
        }

        int index = Integer.parseInt(option);
        return fees[index];
    }

    /**
     * Selecciona un cobro de papelería para un ciclo escolar determinado.
     * @param period Ciclo escolar
     * @param level Nivel educativo
     */
    private Fee selectStationeryFee(ScholarPeriod period, EducationLevel level)
    {
        // Declara una variable para almacenar los cobros consultados
        Fee[] fees;

        // Bloque para intentar obtener los cobros
        try
        {
            fees = dbContext.getStationeryFees(period.code, level.code);
        }
        catch (Exception e)
        {
            // Avisa del error
            System.out.println("Error al consultar costos de papelería");
            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            // Avisa de que no hay cobros
            System.out.println("No hay costos de papelería disponibles");
            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "Papelería\n\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n" +
            "Nivel educativo: %s",
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            period.startingDate,
            period.endingDate,
            level);

        // Crea una cabecera para la tabla
        String header = "Concepto|Grado|Costo";

        Menu menu = createMenu(info);
        menu.setHeader(header);

        // Bucle que recorre la lista de pagos para crear cadenas formateadas
        for (int i = 0; i < fees.length; i++)
        {
            // Objeto con la información del cobro
            Fee fee = fees[i];

            // Crea una cadena de texto para cada cobro
            String text = String.format("%s|%s|$%.2f",
                fee.stationery.concept, // Concepto
                fee.stationery.grade, // Grado
                fee.stationery.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        String option = menu.show("Seleccione una cobro de papeleria");
        if (option.equalsIgnoreCase("V"))
        {
            return null;
        }

        int index = Integer.parseInt(option);
        return fees[index];
    }

    /**
     * Selecciona un cobro de mantenimiento para un ciclo escolar determinado.
     * @param period Ciclo escolar
     */
    private Fee selectMaintenanceFee(ScholarPeriod period)
    {
        Fee fee = null;

        // Bloque para intentar obtener el cobro de mantenimiento
        try
        {
            fee = dbContext.getMaintenanceFee(period.code);
        }
        catch (Exception e)
        {
            // Avisa del error
            System.out.println("Error al consultar costos de mantenimiento");
            // Termina la función
            return null;
        }

        // Verifica si no se obtuvo un cobro de mantenimiento
        if (fee == null || fee.maintenance == null)
        {
            // Avisa del error
            System.out.println("No hay costos de mantenimiento disponible");
        }

        return fee;
    }

    /**
     * Selecciona un cobro de evento especial para un ciclo escolar determinado.
     * @param period Ciclo escolar
     */
    private Fee selectSpecialEventFee(ScholarPeriod period)
    {
        // Declara una variable para almacenar los cobros consultados
        Fee[] fees;

        // Bloque para intentar obtener los cobros
        try
        {
            fees = dbContext.getSpecialEventFees(period.code);
        }
        catch (Exception e)
        {
            // Avisa del error
            System.out.println(
                "Error al consultar costos de eventos especiales");

            // Termina la función
            return null;
        }

        // Verifica si no se obtuvieron cobros
        if (fees.length == 0)
        {
            // Avisa de que no hay cobros
            System.out.println(
                "No hay costos de eventos especiales disponibles");

            // Termina la función
            return null;
        }

        // Establece una cadena con la información común
        String info = String.format(
            "Eventos especiales\n\n" +
            "Ciclo escolar: %d-%d\n" +
            "Fecha de inicio de curso: %s\n" +
            "Fecha de fin de curso: %s\n\n",
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
            Fee fee = fees[i];

            // Crea una cadena de texto formateada para cada cobro
            String text = String.format("%s|%s|$%.2f",
                fee.specialEvent.concept, // Concepto
                fee.specialEvent.scheduledDate, // Fecha programada
                fee.specialEvent.cost); // Costo

            menu.addItem(Integer.toString(i), text, true);
        }

        menu.addBlankLine();
        menu.addItem("V", "Volver al menú anterior");

        String option = menu.show("Seleccione una cobro de papeleria");
        if (option.equalsIgnoreCase("V"))
        {
            return null;
        }

        int index = Integer.parseInt(option);
        return fees[index];
    }

    // Funciones auxiliares

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
