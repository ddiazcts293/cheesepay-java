package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.util.*;
import com.axolutions.db.type.*;
import com.axolutions.db.type.fee.FeeType;
import com.axolutions.panel.args.SearchType;

/**
 * Representa el panel de información de alumno.
 */
public class InfoPanel extends BasePanel
{
    /**
     * DONE: Panel de información de alumno
     *
     * En este panel se mostrara toda la información concerniente a un alumno
     *
     * Algoritmo
     * 1. Inicio
     * 2. Preguntar si se conoce la matricula del alumno, si se conoce,
     *    solicitarla; sino, buscar en el panel de busqueda
     * 3. Preguntar que desea hacer:
     *    - modificar su información personal
     *    - consultar la información del alumno
     *    - consultar los grupos en los que ha estado inscrito por ciclo y nivel
     *      educativo
     *    - consultar los pagos que este ha realizado ya sea por categoría y/o
     *      ciclo escolar
     *    - ver sus tutores y sus teléfonos registrados
     * 4. Fin
     */

    /**
     * Crea un nuevo objeto StudentInformationPanel
     * @param appContext Instancia del objeto AppContext
     */
    public InfoPanel(AppContext appContext)
    {
        super(appContext, Location.InfoPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        System.out.println("Panel de información");

        // Declara una variable para almacenar al alumno
        Student student = null;

        // Verifica si se ha transferido un objeto Alumno
        if (args.getObj() instanceof Student)
        {
            // De ser así, convierte el tipo de la referencia a un objeto Alumno
            student = (Student)args.getObj();
        }
        // De lo contrario, verifica si se ha transferido un objeto Tutor
        else if (args.getObj() instanceof Tutor)
        {
            // De ser así, convierte el tipo de referencia a un objeto Tutor
            var tutor = (Tutor)args.getObj();
            // Despliega la información del tutor
            showTutorInfo(tutor);
        }
        // De lo contrario,
        else
        {
            // Solicita que se ingrese la matricula del alumno para buscarlo
            student = getStudentById();
        }

        // Verifica que se haya establecido un objeto Alumno
        if (student != null)
        {
            // Crea un nuevo menú y le añade algunas opciones
            String option;
            Menu menu = createMenu();
            menu.addItem("R", "Registrar pago");
            menu.addItem("I", "Ver informacion personal");
            menu.addItem("T", "Gestionar tutores registrados");
            menu.addItem("H", "Consultar historial de pagos");
            menu.addItem("V", "Volver al menú principal");

            // Inicia un bucle
            do
            {
                // Crea una cadena formateada con la matricula y el nombre del
                // alumno
                String studentInfo = String.format(
                    "\nAlumno: %s - %s %s %s",
                    student.studentId,
                    student.name,
                    student.firstSurname,
                    student.lastSurname);

                // Establece un título para el menú
                menu.setTitle(studentInfo);

                // Muesta el menú y espera una opción
                option = menu.show("Seleccione una opción");

                // Procesa la opción escogida
                switch (option)
                {
                    // Registrar pago
                    case "R":
                        goTo(Location.PaymentRegistrationPanel, student);
                        break;
                    // Ver información
                    case "I":
                        showStudentInfo(student);
                        break;
                    // Gestionar tutores
                    case "T":
                        manageStudentTutors(student);
                        break;
                    // Consultar historial de pagos
                    case "H":
                        showStudentPayments(student);
                        break;
                    default:
                        break;
                }
            }
            // Repite mientras no se elija "Volver" en el menú
            while (!option.equalsIgnoreCase("v"));
        }
        
        // Retorna al panel anterior
        return null;
    }

    /**
     * Obtiene a un alumno por su matricula.
     * @return Objeto con la información del alumno o nulo si se canceló el
     * proceso
     */
    private Student getStudentById()
    {
        System.out.println();

        // Declara una variable para almacenar el alumno una vez encontrado
        Student student = null;

        // Bucle para repetir la búsqueda de un alumno por medio de su matricula
        do
        {
            // Solicita al usuario que ingrese la matricula de un alumno
            String studentId = console.readString(
                "Ingrese la matricula del alumno (5 caracteres)",
                0,
                5);

            // Bloque para intentar obtener al alumno
            try
            {
                // Realiza una consulta en la base de datos
                student = dbContext.getStudent(studentId);
            }
            catch (Exception e)
            {
                // Avisa al usuario de que hubo un error
                System.out.println("Error al intentar consultar datos");
            }

            // Verifica si no se obtuvo a un alumno
            if (student == null)
            {
                // De ser así, crea un nuevo menú para preguntar si se desea
                // volver a intentar
                String title = "\nNo se pudo localizar la matricula\n" +
                    "¿Desea volver a intentarlo?";

                // Muestra el menú y espera por una opción
                String option = showYesNoMenu(title);

                // Verifica si la opción selecciona es "No"
                if (option.equalsIgnoreCase("n"))
                {
                    // Termina el bucle
                    break;
                }
            }

        // El bucle deberá ejecutarse mientras no se haya seleccionado a un
        // alumno
        } while (student == null);

        return student;
    }

    /**
     * Muestra la información de un alumno.
     * @param student Objeto con la información de un alumno
     */
    private void showStudentInfo(Student student)
    {
        // Declar las variables para el menú
        String option;

        // Bucle que permite repetir y actualizar el menú
        do
        {
            // Imprime la información del alumno
            System.out.printf("\nInformación general de alumno\n" +
                "Matricula: %s\n" +
                "Nombre: %s\n" +
                "Apellido paterno: %s\n" +
                "Apellido materno: %s\n",
                student.studentId,
                student.name,
                student.firstSurname,
                student.lastSurname != null ? student.lastSurname : "N/A");

            // Intenta obtener el grupo actual del alumno
            try 
            {
                var group = dbContext.getStudentCurrentGroup(student.studentId);
            
                System.out.printf("\nEscolaridad\n" +
                    "Grupo: %d-%s\n" +
                    "Nivel educativo: %s\n" +
                    "Ciclo escolar: %d-%d\n",
                    group.grade,
                    group.letter,
                    group.level.description,
                    group.period.startingDate.getYear(),
                    group.period.endingDate.getYear());
            } 
            catch (Exception e) 
            {
                System.out.println(
                    "Error al intentar obtener la información del grupo " +
                    "actual del alumno");
            }

            System.err.printf("\nInformación personal\n" +
                "Genero: %s\n" +
                "Edad: %s años\n" +
                "Fecha de nacimiento: %s\n" +
                "CURP: %s\n" +
                "NSS: %s\n" +
                "Calle: %s\n" +
                "Numero: %s\n" +
                "Colonia: %s\n" +
                "Código Postal: %s\n\n",
                student.gender,
                student.age,
                student.dateOfBirth,
                student.curp,
                student.ssn != null ? student.ssn : "N/A",
                student.addressStreet,
                student.addressNumber,
                student.addressDistrict,
                student.addressPostalCode);

            // Intenta obtener a los tutores registrados con el alumno
            try
            {
                Tutor tutors[] = dbContext.getStudentTutors(student.studentId);

                // Imprime la tabla de tutores
                System.out.println("Tutores registrados");
                console.printAsTable(
                    "Parentesco|Nombre|Correo electrónico|RFC",
                    tutors);
            }
            catch (Exception e)
            {
                System.out.println(
                    "Error al intentar obtener los tutores asociados con el " +
                    "alumno");
            }

            // Intenta obtener los grupos en los que el alumno ha estado
            try
            {
                Group[] groups = dbContext.getStudentGroups(student.studentId);

                // Verifica si el alumno ha estado en algún grupo
                if (groups.length > 0)
                {
                    // Crea un arreglo de cadenas para mostrar cada grupo
                    String[] lines = new String[groups.length];

                    // Bucle que recorre el arreglo de grupos para obtener la
                    // información requerida de cada uno
                    for (int i = 0; i < groups.length; i++) 
                    {
                        var group = groups[i];

                        lines[i] = String.format(
                            "%s|%d-%s|%d-%d", // Cadena formateada
                            group.level.description, // Nivel educativo
                            group.grade, // Grado
                            group.letter, // Letra
                            group.period.startingDate.getYear(), // Ciclo inicio
                            group.period.endingDate.getYear()); // Ciclo fin
                    }

                    // Imprime la tabla de grupos
                    System.out.println("Grupos en los que ha estado");
                    console.printAsTable("Nivel|Grupo|Ciclo escolar", lines);
                }
                else
                {
                    // De lo contrario, informa que no es el caso
                    System.out.println(
                        "El alumno aun no ha estado en ningún grupo");
                }
            }
            catch (Exception e)
            {
                // Avisa al usuario de que hubo un error
                System.out.println(
                    "Error al intentar obtener los grupos en los que ha " +
                    "estado inscrito el alumno");
            }

            // Crea un menú, lo muestra y espera por una opción
            option = createMenu()
                .addItem("E", "Editar información del alumno")
                .addItem("T", "Gestionar tutores registrados")
                .addItem("V", "Volver al menú anterior")
                .show("Seleccione una opción");

            // Procesa la opción escogida
            switch (option) 
            {
                // Editar información
                case "E":
                    editStudentInfo(student);
                    break;
                // Gestionar tutores
                case "T":
                    manageStudentTutors(student);
                    break;
                default:
                    break;
            }

        // Repite mientras no se elija "Volver"
        } while (!option.equalsIgnoreCase("V"));
    }

    /**
     * Edita la información de un alumno.
     * @param student Objeto con la información de un alumno
     */
    private void editStudentInfo(Student student)
    {
        // Declara una serie de variables para almacenar los datos leídos
        String option;
        String gender;
        String addressStreet;
        String addressNumber;
        String addressDistrict;
        String addressPostalCode;

        // Crea un menú y le añade algunas opciones
        Menu menu = createMenu("¿Continuar?")
            .addItem("a", "Si, actualizar")
            .addItem("v", "No, volver a ingresar datos")
            .addItem("c", "No, cancelar");

        // Crea un bucle que permite repetir la obtención de datos de un alumno
        do
        {
            System.out.println("Editando información\n" +
                "Presione ENTER para omitir un campo\n");

            gender = console.readString(
                "Genero (opcional, 10 caracteres max.)",
                0, 10);
            addressStreet = console.readString(
                "Calle (requerido, 10 caracteres max.)",
                0, 10);
            addressNumber = console.readString(
                "Numero (requerido, 20 caracteres max.)",
                0, 20);
            addressDistrict = console.readString(
                "Colonia (requerido, 30 caracteres max.)",
                0, 30);
            addressPostalCode= console.readString(
                "Codigo postal (requerido, 5 caracteres max.)",
                0, 5);

            // Muestra el menú para confirmar y espera por una opción
            option = menu.show();

        // Repite mientras la opción escogida sea "Volver"
        } while (option.equalsIgnoreCase("v"));

        // Verifica si se escogió "Actualizar"
        if (option.equalsIgnoreCase("a"))
        {
            // De ser así, establece los valores de aquellas variables no vacías
            // que fueron leidas en el objeto Alumno

            if (!gender.isBlank())
            {
                student.gender = gender;
            }
            if (!addressStreet.isBlank())
            {
                student.addressStreet = addressStreet;
            }
            if (!addressNumber.isBlank())
            {
                student.addressNumber = addressNumber;
            }
            if (!addressDistrict.isBlank())
            {
                student.addressDistrict = addressDistrict;
            }
            if (!addressPostalCode.isBlank())
            {
                student.addressPostalCode = addressPostalCode;
            }

            // Bloque para intentar actualizar la información del alumno
            try
            {
                // Realiza una consulta de actualización
                dbContext.updateStudentInfo(student);
            }
            catch (Exception e)
            {
                // Avisa al usuario de que hubo un error
                System.out.println(
                    "Error al intentar actualizar el registro de alumno");
            }
        }
    }

    /**
     * Muestra la lista de pagos efectuados por un alumno.
     * @param student Objeto con la información de un alumno
     */
    private void showStudentPayments(Student student)
    {
        // TODO: Mostrar pagos filtrados por categoria

        do 
        {
            PaidFee[] paidFees;
            String[] list;
            String header;

            // Pregunta por el tipo de cobro a buscar
            FeeType type = selectFeeType();
            if (type == FeeType.Unknown)
            {
                break;
            }

            try
            {
                switch (type) 
                {
                    case Enrollment:
                    {
                        System.out.println("\nMostrando pagos de inscripciones");

                        paidFees = dbContext.getPaidEnrollmentFees(student.studentId);
                        header = "Folio|Fecha de pago|Nivel educativo|Ciclo escolar|Costo|Tutor";
                        list = new String[paidFees.length];
                        for (int i = 0; i < paidFees.length; i++) 
                        {
                            var item = paidFees[i];

                            list[i] = String.format(
                                "#%d|%s|%s|%d-%d|$%.2f|%s",
                                item.paymentFolio,
                                item.date,
                                item.level.description,
                                item.period.startingDate.getYear(),
                                item.period.endingDate.getYear(),
                                item.cost,
                                item.tutorName);
                        }

                        console.printAsTable(header, list);

                        break;
                    }
                    case Monthly:
                    {
                        var period = selectScholarPeriod();
                        if (period != null)
                        {
                            System.out.printf(
                                "\nMostrando pagos de mensualidades ciclo escolar %d-%d\n",
                                period.startingDate.getYear(),
                                period.endingDate.getYear());
    
                            paidFees = dbContext.getPaidMonthlyFees(student.studentId, period.code);
                            header = "Folio|Fecha de pago|Mes pagado|Nivel educativo|Costo|Tutor";
                            list = new String[paidFees.length];
                            for (int i = 0; i < paidFees.length; i++) 
                            {
                                var item = paidFees[i];
    
                                list[i] = String.format(
                                    "#%d|%s|%s|%s|$%.2f|%s",
                                    item.paymentFolio,
                                    item.date,
                                    item.concept,
                                    item.level.description,
                                    item.period.startingDate.getYear(),
                                    item.period.endingDate.getYear(),
                                    item.cost,
                                    item.tutorName);
                            }
    
                            console.printAsTable(header, list);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
            catch (Exception e)
            {
                System.out.println("Error al obtener los pagos del alumno");
                break;
            }

            console.pause("Presione ENTER para continuar");

        } while (true);

        /* 

        // Declara una variable para almacenar los pagos de un alumno
        Payment[] payments;

        // Bloque para intentar obtener la lista de pagos
        try
        {
            // Realiza una consulta de obtención de pagos para el alumno actual
            payments = dbContext.getStudentPayments(student.studentId);
        }
        catch (Exception e)
        {
            // Avisa al usuario de que hubo un error
            System.out.println("Error al obtener los pagos del alumno");
            // Termina la función
            return;
        }

        // Verifica si el alumno tiene pagos registrados
        if (payments.length > 0)
        {
            System.out.println("Pagos realizados por el alumno");

            // Bucle que recorre el arreglo de pagos obtenidos
            for (Payment payment : payments)
            {
                // Crea una cadena de texto con la información de cada pago
                String displayText = String.format(
                    "#%d\t%s\t%d-%d\t$%.2f",
                    payment.folio,
                    payment.date,
                    payment.period.startingDate.getYear(),
                    payment.period.endingDate.getYear(),
                    payment.totalAmount);

                // Imprime cada registro por línea
                System.out.println(displayText);
            }
        }
        else
        {
            // De lo contrario, avisa que no se han registrado pagos
            System.out.println("El alumno no tiene pagos registrados");
        }*/
    }

    /**
     * Gestiona los tutores de un alumno.
     * @param student Objeto con la información de un alumno
     */
    private void manageStudentTutors(Student student)
    {
        // Bucle para repetir el menú cada vez que se selecciona ver a un tutor
        do
        {
            // Declara un arreglo para almacenar los tutores obtenidos
            Tutor[] tutors;

            // Bloque para intentar obtener los tutores
            try
            {
                // Realiza la consulta de obtención de tutores
                tutors = dbContext.getStudentTutors(student.studentId);
            }
            catch (Exception e)
            {
                // Avisa al usuario de que hubo un error
                System.out.println(
                    "Error al obtener tutores asociados a alumno");
                
                // Termina el bucle
                break;
            }

            // Crea una cadena de texto para la cabecera del menú
            String title = "Tutores registrados\n\n" +
                "Seleccione a un tutor para ver la información de este o " +
                "elija una acción a realizar";
            
            // Crea un nuevo menú, lo muestra y espera a que el usuario 
            // seleccione una opción
            String option = createMenu(title)
                .setHeader("[#] - Parentesco|Nombre|Correo electronico|RFC")
                .addItems(tutors) // Agrega la lista de tutores al menú
                .addItem("a", "Agregar a un tutor")
                .addItem("v", "Volver al menú anterior")
                .show("Seleccione una opción");

            // Verifica si la opción elegida fue "Agregar tutor"
            if (option.equalsIgnoreCase("a"))
            {
                // Agrega a un nuevo tutor para el alumno actual
                addTutor(student);
                // Comienza un nuevo ciclo
                continue;
            }
            // Verifica si la opción elegida fue "Volver"
            else if (option.equalsIgnoreCase("v"))
            {
                // Sale del bucle para regresar al menú anterior
                break;
            }

            // A partir de este punto, se asume que se eligió el número de un
            // tutor mostrado, por lo que se convierte la opción elegida a
            // número y se obtiene un objeto tutor presente en el arreglo de 
            // tutores para mostrar su información
            int index = Integer.parseInt(option);
            var tutorSelected = tutors[index];

            // Muestra la información del tutor seleccionado
            showTutorInfo(tutorSelected);

        // Se ejecutará de manera indefinida
        } while (true);
    }

    /**
     * Agrega a un tutor.
     * @param student Objeto con la información de un tutor
     */
    private void addTutor(Student student)
    {
        // Declara una variable para almacenar al objeto que contiene la 
        // información de un tutor
        Tutor tutor = null;

        // Pregunta si el tutor ya fue registrado anteriormente
        String option = showYesNoMenu(
            "¿El tutor se encuentra registrado?");
        
        // Procesa la opción escogida
        switch (option) 
        {
            // Si
            case "S":
            {
                // Dirige al panel de búsqueda de tutor para buscarlo
                var result = goTo(Location.SearchPanel, SearchType.Tutor);
                // Verifica si el resultado obtenido corresponde a un Tutor
                if (result instanceof Tutor)
                {
                    // Convierte y asigna el objeto
                    tutor = (Tutor)result;
                }
                break;
            }
            case "N":
            {
                // Dirige al panel de registro para registrar a un nuevo tutor
                var result = goTo(Location.EnrollmentPanel, student);
                
                // Verifica si la instancia devuelta corresponde a un objeto Tutor
                if (result instanceof Tutor)
                {
                    // Convierte y asigna el objeto con la información de un tutor
                    tutor = (Tutor)result;
                }
            }
            default:
                break;
        }

        // Verifica si se obtuvo a un tutor
        if (tutor != null)
        {
            // De ser así, intenta realizar la asociación
            try 
            {
                dbContext.registerStudentWithTutor(student, tutor);
            }
            catch (Exception e)
            {
                // Avisa al usuario de que hubo un error
                System.out.println(
                    "Error al intentar asociar el alumno con el tutor");
            }
        }
        else
        {
            System.out.println("No se seleccionó a ningún tutor");
        }
    }

    /**
     * Muestra la información de un tutor.
     * @param tutor Objeto con la información de un tutor
     */
    private void showTutorInfo(Tutor tutor)
    {
        // Declara una variable para almacenar la opción escogida
        String option;
        
        // Bucle que repite el menú luego de realizar una acción en él
        do
        {
            // Crea un menú y le añade algunas opciones
            Menu menu = createMenu()
                .addItem("e", "Editar correo eléctronico")
                .addItem("a", "Agregar número telefónico");

            // Crea una cadena de texto formateada con la información del tutor
            String info = String.format("Información de tutor\n\n" +
                "Nombre: %s\n" +
                "Apellido paterno: %s\n" +
                "Apellido materno: %s\n" +
                "Parentesco: %s\n" +
                "Correo electronico: %s\n" +
                "RFC: %s",
                tutor.name,
                tutor.firstSurname,
                tutor.lastSurname != null ? tutor.lastSurname : "N/A",
                tutor.kinship,
                tutor.email,
                tutor.rfc);

            // Verifica si el tutor tiene números de telefono registrados
            if (tutor.phones.size() > 0)
            {
                info += "\n\nTelefonos:";

                // Bucle que recorre la lista de números de telefonos
                for (var phone : tutor.phones)
                {
                    // Agrega el número de teléfono al texto de información
                    info += "\n- " + phone.phone;
                }
            
                // Agrega la opción para quitar un número
                menu.addItem("q", "Quitar número telefónico");
            }

            // Agrega la opción para volver al menú anterior
            menu.addItem("v", "Volver al menú anterior");

            // Establece el título del menú
            menu.setTitle(info);

            // Muestra el menú y espera por una opción
            option = menu.show("Seleccione una acción");
            
            // Procesa la opción escogida
            switch (option)
            {
                // Editar información
                case "e":
                case "E":
                    editTutorEmail(tutor);
                    break;
                // Agregar número de teléfono
                case "a":
                case "A":
                    addTutorPhone(tutor);
                    break;
                // Quitar número de teléfono
                case "q":
                case "Q":
                    removeTutorPhone(tutor);
                    break;
                default:
                    break;
            }

        // Repite mientras no se elija "Volver"
        } while (!option.equalsIgnoreCase("v"));
    }

    /**
     * Edita la información de un tutor.
     * @param tutor Objeto con la información de un tutor
     */
    private void editTutorEmail(Tutor tutor)
    {
        // Obtiene una nueva dirección de correo electrónico
        String email = console.readString(
            "Correo electronico (40 caracteres max.)",
            10, 40);

        // Bloque para intentar actualizar la información
        try
        {
            dbContext.updateTutorEmail(tutor);
            tutor.email = email;
        }
        catch (Exception e)
        {
            // Avisa al usuario de que hubo un error
            System.out.println(
                "Error al intentar actualizar el registro de tutor");
        }
    }

    /**
     * Agrega un nuevo número de teléfono.
     * @param tutor Objeto con la información de un tutor
     */
    private void addTutorPhone(Tutor tutor)
    {
        // Crea un nuevo número de teléfono
        TutorPhone phone = new TutorPhone();
        // Solicita el número
        phone.phone = console.readString(
            "Número de teléfono (15 caracteres max.)",
            10,
            15);

        // Agrega el núumero a la lista
        tutor.phones.add(phone);

        // Bloque para intentar eliminar un número de teléfono
        try
        {
            dbContext.addTutorPhone(tutor, phone);
        }
        catch (Exception e)
        {
            System.out.println(
                "Error al intentar agregar el número de teléfono");
        }
    }

    /**
     * Quita un número de teléfono.
     * @param tutor Objeto con la información de un tutor
     */
    private void removeTutorPhone(Tutor tutor)
    {
        // Crea un menú vacío para mostrar los números de teléfono registrados
        Menu menu = createMenu();

        // Bucle que repite el menú una y otra vez mientras el tutor tenga al
        // menos un número de teléfono
        while (tutor.phones.size() > 0)
        {
            // Limpia el menú
            menu.clearItems();
            // Agrega la lista de números al menú
            menu.addItems(tutor.phones.toArray());
            // Agrega una línea blanca y una opción para regresar
            menu.addBlankLine();
            menu.addItem("v", "Volver al menú anterior");
            // Establece el título del menú
            menu.setTitle("Quitando números de teléfono");

            // Muestra el menú y espera por una opción
            String option = menu.show("Seleccione una opción");

            // Verifica si la opción escogida es "Volver"
            if (option.equalsIgnoreCase("v"))
            {
                // Termina la función actual
                return;
            }

            // A partir de este punto, se considera que el valor escogido es un
            // tutor, por lo que se obtentra el objeto tutor con el índice dado
            int index = Integer.parseInt(option);
            var phoneSelected = tutor.phones.get(index);

            // Bloque para intentar eliminar un número de teléfono
            try
            {
                // Quita un número de teléfono
                dbContext.deleteTutorPhone(phoneSelected);
                tutor.phones.remove(index);
            }
            catch (Exception e)
            {
                System.out.println(
                    "Error al intentar eliminar el número de telefono");
            }
        }
    }
}
