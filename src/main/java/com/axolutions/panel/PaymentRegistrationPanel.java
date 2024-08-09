package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.*;
import com.axolutions.db.type.fee.*;
import com.axolutions.util.Menu;

import java.util.ArrayList;
import java.time.*;

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

        Student student = null;
        Tutor selectedTutor = null;
        Group group = null;
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
            // Intenta obtener la lista de tutores registrados con el alumno
            tutors = dbContext.getStudentTutors(student.studentId);

            // Intenta obtener el grupo actual del estudiante
            group = dbContext.getStudentCurrentGroup(student.studentId);
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
            "\nSeleccione a un tutor para registrar en el pago",
            "Parentesco|Nombre|Correo electronico|RFC",
            tutors);

        // Verifica si no se seleccionó a un tutor
        if (selectedTutor == null)
        {
            // Termina la función
            return null;
        }

        /*
         * Aqui entran en juego varias modalidades:
         * - El alumno es de nuevo ingreso -> Paga todo
         * - El alumno acaba de pasar de grado -> Paga todo
         * - El alumno esta cursando actualmente -> Paga algo
         */

        // Verifica si el alumno está asignado a un grupo
        if (group != null)
        {
            // Crea un nuevo pago en el que se puede cobrar por mensualidades,
            // uniformes y/o eventos especiales
            createPayment(student, selectedTutor, group);
        }
        // Si no, verifica si el alumno es de nuevo ingreso, es decir, si no ha
        // estado en algún grupo antes
        else
        {
            // Crea un nuevo pago para un alumno de nuevo ingreso
            createPaymentForNewStudent(student, selectedTutor);
        }

        return null;
    }

    /**
     * Crea un nuevo pago.
     * @param student Alumno
     * @param tutor Tutor
     * @param group Grupo
     */
    private void createPayment(Student student, Tutor tutor, Group group)
    {
        ArrayList<Fee> fees = new ArrayList<>();
        createPayment(student, tutor, group, fees);
    }

    /**
     * Crea un nuevo pago.
     * @param student Alumno
     * @param tutor Tutor
     * @param group Grupo
     * @param fees Lista de pagos
     */
    private boolean createPayment(Student student, Tutor tutor, Group group, ArrayList<Fee> fees)
    {
        // Declara algunas variables de datos generales
        LocalDate date = LocalDate.now(); // Fecha de pago
        float totalAmount = 0f; // Total
        String option;
        boolean successful = false;

        // Crea una cadena de texto que contiene la información del pago
        String title = String.format(
            "\nNuevo pago\n" +
            "Fecha de realización: %s\n" +
            "Matricula: %s\n" +
            "Alumno: %s\n" +
            "Grupo: %d-%s\n" +
            "Nivel educativo: %s\n" +
            "Ciclo escolar: %s\n" +
            "Tutor: %s\n\n" +
            "Lista de cobros",
            date,
            student.studentId,
            student.getFullName(),
            group.grade,
            group.letter,
            group.level.description,
            group.period.getPeriodString(),
            tutor.getFullName());
            
        // Bucle para permitir agregar varios cobros
        do
        {
            // Calcula el total del pago en cada ciclo
            totalAmount = calculateTotalAmount(fees);

            // Crea una cadena de texto que contiene el total y la cantidad de
            // cobros
            String footer = String.format("Total: $%.2f\n" +
                "Cantidad de cobros: %d",
                totalAmount, // Monto total
                fees.size()); // Cantidad de pagos

            // Crea un menú para mostrar todos los cobros en el pago y así poder
            // seleccionar uno para eliminarlo en caso de que sea necesario
            Menu menu = helper.createMenu(title)
                .setHeader("Concepto|Costo")
                .setFooter(footer);

            // Bucle que recorre la lista de cobros
            for (int i = 0; i < fees.size(); i++) 
            {
                // Obtiene el cobro por su indice dado por i
                var item = fees.get(i);
                String text = getFeeText(item);

                menu.addItem(Integer.toString(i), text, true);
            }
            
            menu.addItem("A", "Agregar cobro");

            // Verifica si la lista de cobros no está vacia
            if (!fees.isEmpty())
            {
                menu.addItem("P", "Pagar");
            }

            menu.addItem("C", "Cancelar");

            // Muestra el menú y espera por una opción
            option = menu.show(
                "Elija un cobro para borrarlo de la lista o escoja una " +
                "acción a realizar");

            // Verifica si la opción elegida es "Agregar cobro"
            if (option.equalsIgnoreCase("A"))
            {
                // Llama a la función que permite agregar pagos
                addFeeToPayment(student, group.period, group.level, fees);
            }
            // Verifica si la opción elegida es "Cancelar"
            else if (option.equalsIgnoreCase("C"))
            {
                // Confirma si realmente se desea abandonar
                option = helper.showYesNoMenu("¿Está seguro?");

                // Verifica si la opción recibida es "Si"
                if (option.equalsIgnoreCase("S"))
                {
                    // Termina el bucle
                    break;
                }
            }
            // Verifica si la opción elegida es "Pagar"
            else if (option.equalsIgnoreCase("P"))
            {
                // Registra el pago
                int folio = registerPayment(
                    student, 
                    tutor, 
                    date, 
                    totalAmount, 
                    fees);

                if (folio != -1)
                {
                    System.out.printf(
                        "\nPago #%d registrado correctamente\n",
                        folio);
                    
                    successful = true;
                }
                break;
            }
            // Si no
            else
            {
                // Borra el elemento seleccionado
                int index = Integer.parseInt(option);
                fees.remove(index);
            }

        // Repite el bucle infinitamente
        } while (true);

        return successful;
    }

    /**
     * Registra un pago para un alumno de nuevo ingreso.
     * @param student Alumno
     * @param tutor Tutor
     */
    private void createPaymentForNewStudent(Student student, Tutor tutor)
    {
        /*
         * Comportamiento de ciclos escolares:
         * Por defecto, el programa seleccionará el ciclo actual real, pero si
         * a este le quedan menos de dos meses, selecciona al siguiente
         */

        EducationLevel selectedLevel = null;
        SchoolPeriod currentPeriod = null;
        Group group = null;
        int grade, maxGrade;
        boolean isNewStudent;

        try
        {
            // Intenta averiguar si el alumno es nuevo ingreso
            isNewStudent = dbContext.isNewStudent(student.studentId);

            // Intenta obtener el ciclo actual
            // currentPeriod = dbContext.getCurrentPeriod();
            // Por ahora, se selecciona de una lista
            currentPeriod = helper.selectSchoolPeriod(); 
/*
            // Verifica si el ciclo actual no sea nulo
            if (currentPeriod != null)
            {
                // Obtiene el periodo de fechas entre la fecha actual y la fecha
                // de fin del ciclo
                Period period = Period.between(
                    LocalDate.now(),
                    currentPeriod.endingDate);

                // Obtiene la cantidad de meses restantes
                int remainingMonths = period.getMonths() + period.getYears() * 12;

                // Verifica si la cantidad de meses restantes es menor o igual a
                // dos
                if (remainingMonths <= 2)
                {
                    // De ser así, establece el próximo ciclo escolar como el
                    // periodo
                    currentPeriod = dbContext.getNextPeriod(); // Por ahora no
                }
            }
 */
            if (currentPeriod == null)
            {
                System.out.println(
                    "No se han registrado un nuevo ciclo escolar para la " +
                    "fecha actual");
            
                // Finaliza la función
                return;
            }
        }
        catch (Exception e)
        {
            System.out.println(
                "Error al obtener la información del alumno");
            
            // Finaliza la función
            return;
        }

        if (isNewStudent)
        {
            System.out.printf(
                "\nRegistro de pagos para un alumno de nuevo ingreso\n" +
                "Ciclo escolar: %s\n",
                currentPeriod.getPeriodString());
        }
        else
        {
            Group lastGroup = null;
            
            try 
            {
                lastGroup = dbContext.getStudentLastGroup(student.studentId);
            } 
            catch (Exception e) 
            {
                
            }

            System.out.printf(
                "\nRegistro de pagos para un alumno que ha cursado previamente\n" +
                "Ciclo escolar: %s\n",
                currentPeriod.getPeriodString());
            
            if (lastGroup != null)
            {
                System.out.printf(
                "Grupo anterior: %d-%s\n" +
                "Escolaridad: %s\n",
                lastGroup.grade,
                lastGroup.letter,
                lastGroup.level.description);
            }
        }

        // Solicita un nivel educativo
        selectedLevel = helper.selectEducationLevel();
        // Verifica si no se seleccionó un nivel educativo
        if (selectedLevel == null)
        {
            // Termina el proceso
            return;
        }

        try
        {
            // Intenta obtener el grado máximo
            maxGrade = dbContext.getMaxGrade(selectedLevel.code);
        }
        catch (Exception e)
        {
            System.out.println(
                "Error al obtener el grado máximo para el nivel seleccionado");
            
            // Termina el proceso
            return;
        }

        // Pide que se ingrese un grado
        String prompt = String.format(
            "Ingrese el grado del alumno (1-%d)",
            maxGrade);

        grade = console.readInt(prompt, 1, maxGrade);
        
        try 
        {
            // Intenta obtener el grupo asociado al grado
            group = dbContext.getGroup(
                selectedLevel.code, 
                currentPeriod.code, 
                grade);
        }
        catch (Exception e) 
        {
            System.out.println(
                "Error al obtener el grupo para el grado y nivel seleccionado");
            
            // Termina el proceso
            return;
        }

        ArrayList<Fee> fees = new ArrayList<>();
        try
        {
            // Intenta obtener el cobro de inscripción
            fees.add(dbContext.getEnrollmentFee(
                currentPeriod.code, 
                selectedLevel.code));
            // Intenta obtener el cobro de papeleria
            fees.add(dbContext.getStationeryFee(
                currentPeriod.code, 
                selectedLevel.code, 
                grade));
            // Intenta obtener el cobro de mensualidad
            fees.add(dbContext.getMonthlyFee(
                currentPeriod.code,
                selectedLevel.code, 
                //LocalDate.now()
                currentPeriod.startingDate
                ));
            // Intenta obtener el cobro de mantenimiento
            fees.add(dbContext.getMaintenanceFee(currentPeriod.code));
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener los cobros requeridos");

            // Termina el proceso
            return;
        }

        // Llama a la función para crear el pago y proceder a registrarlo
        boolean paymentRegistered = createPayment(student, tutor, group, fees);

        // Verifica si se pudo registrar el pago
        if (paymentRegistered)
        {
            try 
            {
                // Intenta registrar el alumno en el grupo
                dbContext.registerStudentInGroup(student, group);
            }
            catch (Exception e)
            {
                System.out.println("Error al registrar el alumno en un grupo");
            }
        }
    }

    /**
     * Agrega un cobro a un pago.
     * @param student Alumno
     * @param period Ciclo escolar
     * @param level Nivel educativo
     * @param fees Lista de cobros
     */
    private void addFeeToPayment(
        Student student, 
        SchoolPeriod period, 
        EducationLevel level,
        ArrayList<Fee> fees)
    {
        // Declara la variable para almacenar el cobro seleccionado
        Fee fee;

        // Bucle para repetir el menú de selección de categoría
        do
        {
            fee = null;

            // Muestra el menú de tipo de pago y espera por una opción
            String selectedCategory = helper.createMenu()
                .setTitle("\nSeleccione un tipo de cobro")
                .addItem("M", "Mensualidades")
                .addItem("U", "Uniformes")
                .addItem("E", "Eventos especiales")
                .addBlankLine()
                .addItem("C", "Cancelar")
                .show();

            // Procesa la categoría elegida
            switch (selectedCategory)
            {
                // Mensualidad
                case "M":
                    fee = helper.selectMonthlyFee(
                        period, 
                        level, 
                        "\nSeleccione una mensualidad para pagar");

                    break;
                // Uniforme
                case "U":
                    fee = helper.selectUniformFee(
                        period, 
                        level, 
                        "\nSeleccione un cobro de uniforme para pagar");

                    break;
                // Evento especial
                case "E":
                    fee = helper.selectSpecialEventFee(
                        period, 
                        "\nSeleccione un evento especial para pagar");

                    break;

                // Preterminado; cancelar
                default:
                    return;
            }

            // Bucle que verifica que no existan cobros repetidos
            for (var itemInList : fees) 
            {
                if (itemInList.code.equals(fee.code))
                {
                    System.out.println(
                        "No es posible repetir un mismo cobro en un solo pago");

                    fee = null;
                }
            }

        // Repetir mientras no se seleccione un cobro
        } while (fee == null);

        // Agrega el núevo cobro a la lista
        fees.add(fee);
    }

    /**
     * Calcula el monto total de un pago
     * @param fees Lista de cobros
     * @return
     */
    private float calculateTotalAmount(ArrayList<Fee> fees)
    {
        float result = 0f;

        for (var item : fees) 
        {
            result += item.cost;
        }

        return result;
    }

    /**
     * Obtiene una cadena que representa un cobro.
     * @param fee Cobro
     * @return
     */
    private String getFeeText(Fee fee)
    {
        if (fee instanceof EnrollmentFee)
        {
            var enrollment = (EnrollmentFee)fee;
            return String.format(
                "Inscripción para %s (%d-%d)|$%.2f",
                enrollment.level.description, // nivel
                enrollment.period.startingDate.getYear(),
                enrollment.period.endingDate.getYear(),
                enrollment.cost); // costo
        }
        else if (fee instanceof MonthlyFee)
        {
            var monthly = (MonthlyFee)fee;
            return String.format(
                "Mensualidad %s para %s (%d-%d)|$%.2f",
                helper.getMonthName(monthly.dueDate.getMonth()), // Mes
                monthly.level.description, // nivel
                monthly.period.startingDate.getYear(), // Ciclo
                monthly.period.endingDate.getYear(),
                monthly.cost); // costo
        }
        else if (fee instanceof UniformFee)
        {
            var uniform = (UniformFee)fee;
            return String.format(
                "%s (%s) para %s|$%.2f",
                uniform.concept,
                uniform.size,
                uniform.level.description,
                uniform.cost);
        }
        else if (fee instanceof StationeryFee)
        {
            var stationery = (StationeryFee)fee;
            return String.format(
                "%s para %s|$%.2f",
                stationery.concept,
                stationery.level.description,
                stationery.cost);
        }
        else if (fee instanceof SpecialEventFee)
        {
            var specialEvent = (SpecialEventFee)fee;
            return String.format(
                "%s %s|$%.2f",
                specialEvent.concept,
                specialEvent.scheduledDate,
                specialEvent.cost);
        }
        else if (fee instanceof MaintenanceFee)
        {
            var maintenance = (MaintenanceFee)fee;
            return String.format(
                "%s|$%.2f",
                maintenance.concept,
                maintenance.cost);

        }
        else
        {
            return "";
        }
    }

    /**
     * Registra un pago en la base de datos.
     * @param student
     * @param tutor
     * @param date
     * @param totalAmount
     * @param fees
     */
    private int registerPayment(
        Student student, 
        Tutor tutor,
        LocalDate date,
        float totalAmount,
        ArrayList<Fee> fees)
    {
        try 
        {
            Fee[] feeArray = new Fee[fees.size()];
            fees.toArray(feeArray);

            int paymentFolio = dbContext.registerPayment(
                student.studentId, 
                tutor.number, 
                date, 
                totalAmount, 
                feeArray);

            return paymentFolio;
        } 
        catch (Exception e) 
        {
            System.out.println("Error al intentar registrar el pago");
            return -1;
        }
    }
}
