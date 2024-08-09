package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.*;
import com.axolutions.db.type.fee.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.Month;
import java.time.LocalDate;

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
        selectedTutor = helper.selectFromList(
            "Seleccione a un tutor",
            "Parentesco|Nombre|Correo electronico|RFC",
            tutors);

        // Verifica si no se seleccionó a un tutor
        if (selectedTutor == null)
        {
            return null;
        }

        if (isNewStudent)
        {
            //registerNewStudentPayment(student, selectedTutor);
        }
        else
        {
            createPayment(student, selectedTutor);
        }

        return null;
    }

    /**
     * Crea un pago.
     * @param student Alumno
     * @param tutor Tutor
     */
    private void createPayment(Student student, Tutor tutor)
    {
        HashMap<String, Fee> fees = new HashMap<>();
        ArrayList<String> feeStrings = new ArrayList<>();

        LocalDate date = LocalDate.now();
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

        // Bucle para permitir agregar varios cobros
        do
        {
            // Pregunta por el tipo de cobro a agregar
            feeType = helper.selectFeeType();
            if (feeType == FeeType.Unknown)
            {
                break;
            }
/* 
            // Hace el pago correspondiente
            switch (feeType)
            {
                // Inscripciones
                case Enrollment:
                {
                    fee = helper.selectEnrollmentFee(group.period);

                    if (fee != null && !fees.containsKey(fee.code))
                    {
                        String line = String.format(
                            "Inscripció para %s (%d-%d)|$%.2f",
                            fee.enrollment.level.description, // nivel
                            fee.period.startingDate.getYear(),
                            fee.period.endingDate.getYear(),
                            fee.enrollment.cost); // costo

                        fees.put(fee.code, fee);
                        feeStrings.add(line);
                        totalAmount += fee.enrollment.cost;
                    }
                    break;
                }
                // Mensualidades
                case Monthly:
                    fee = helper.selectMonthlyFee(group.period, group.level);

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

            System.out.println();

            // Imprime los cobros agregados al pago
            String header = "Concepto|Costo";
            console.printAsTable(header, feeStrings.toArray());

            // Crea una cadena con la información del pago
            String info = String.format("Información de pago:\n" + 
                "Alumno: %s %s %s\n" +
                "Tutor: %s %s %s\n" +
                "Fecha de pago: %s\n\n" +
                "Total: $%.2f\n",
                student.name,
                student.firstSurname,
                student.lastSurname,
                tutor.name,
                tutor.firstSurname,
                tutor.lastSurname,
                date,
                totalAmount);
            
            // Pregunta si se desea realiza otro pago
            option = helper.createMenu(info)
                .addItem("A", "Agregar más cobros")
                .addItem("T", "Terminar y registrar pago")
                .addItem("C", "Cancelar")
                .show("Seleccione una opción");

            switch (option) 
            {
                // Terminar y tegistrar pagos
                case "T":
                {
                    FeeXX[] array = new FeeXX[fees.size()];
                    fees.values().toArray(array);
                    registerPayment(student, tutor, date, totalAmount, array);
                    break;
                }
                // Cancelar
                case "C":
                    // Pregunta si realmente quiere cancelar la operación
                    option = helper.showYesNoMenu("¿Está seguro?");
                    if (option.equalsIgnoreCase("S"))
                    {
                        // Termina el proceso de registro de pagos
                        return;
                    }
                default:
                    break;
            }
            */

        option = "";

        // Repite el bucle mientras se elija "Si"
        } while (option.equalsIgnoreCase("A"));
    }

    /**
     * Registra un pago para un alumno de nuevo ingreso.
     * @param student Alumno
     * @param tutor Tutor
     */
    private void createNewStudentPayment(Student student, Tutor tutor)
    {
        Group[] groups;
        Group selectedGroup = null;
        EducationLevel selectedLevel = null;
        SchoolPeriod currentPeriod = null;
        int grade;

        // Solicita un nivel educativo
        selectedLevel = helper.selectEducationLevel();
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
     * Registra un pago.
     * @param student
     * @param tutor
     * @param fees
     * @return
     */
    private void registerPayment(
        Student student, 
        Tutor tutor,
        LocalDate date,
        float totalAmount,
        Fee[] fees)
    {
        try 
        {
            dbContext.registerPayment(
                student.studentId, 
                tutor.number, 
                date, 
                totalAmount, 
                fees);
        } 
        catch (Exception e) 
        {
            System.out.println("Error al registrar el pago");
        }
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
