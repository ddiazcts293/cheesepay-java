package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.util.*;
import com.axolutions.db.type.*;
import com.axolutions.panel.args.SearchType;

/**
 * Representa el panel de información de alumno.
 */
public class StudentInformationPanel extends BasePanel
{
    /**
     * DOING: Panel de información de alumno
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
    public StudentInformationPanel(AppContext appContext)
    {
        super(appContext, Location.StudentInformationPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        System.out.println("Panel de información de alumno");

        // Declara una variable para almacenar al alumno
        Student student = null;

        // Verifica si la última ubicación es el panel de búsqueda y si el
        // objeto transferido es del tipo Alumno
        if (args.getLastLocation() == Location.SearchPanel
            && args.getObj() instanceof Student)
        {
            // Si es así, se convierte y asigna el objeto trasnferido
            student = (Student)args.getObj();
        }
        else
        {
            // Crea un nuevo menú
            Menu menu = appContext.createMenu();
            String option;
            menu.setTitle("¿Conoce la matricula del alumno?");
            menu.addItem("s", "Si");
            menu.addItem("n", "No");
            menu.addItem("c", "Cancelar");

            // Muestra el menú y espera una opción
            option = menu.show();

            // Procesa la opción escogida
            switch (option)
            {
                // Si
                case "S":
                case "s":
                    // Se llama a una función que obtiene un alumno por medio de
                    // su matricula
                    student = getStudent();
                    break;
                // No
                case "N":
                case "n":
                {
                    // Se dirige al panel de búsqueda para buscar a un alumno
                    var result = goTo(Location.SearchPanel, SearchType.Student);

                    // Verifica si el objeto resultante sea del tipo Alumno
                    if (result instanceof Student)
                    {
                        // Si es así, convierte y asigna el objeto transferido
                        student = (Student)result;
                    }

                    break;
                }
                default:
                    break;
            }
        }

        // Verifica que se haya establecido un objeto Alumno
        if (student != null)
        {
            // Crea un nuevo menú y le añade algunas opciones
            String option;
            Menu menu = appContext.createMenu();
            menu.addItem("r", "Registrar pago");
            menu.addItem("i", "Ver informacion personal");
            menu.addItem("t", "Gestionar tutores registrados");
            menu.addItem("g", "Ver grupos en los que ha estado");
            menu.addItem("h", "Consultar historial de pagos");
            menu.addItem("v", "Volver al menú principal");

            // Inicia un bucle
            do
            {
                // Crea una cadena formateada con el nombre del alumno
                String studentInfo = String.format(
                    "Alumno: %s - %s %s %s",
                    student.enrollment,
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
                    // Ver información
                    case "i":
                    case "I":
                        showStudentInfo(student);
                        break;
                    // Gestionar tutores
                    case "t":
                    case "T":
                        manageStudentTutors(student);
                        break;
                    // Ver grupos
                    case "g":
                    case "G":
                        showStudentGroups(student);
                        break;
                    // Consultar historial de pagos
                    case "h":
                    case "H":
                        showStudentInvoices(student);
                        break;
                    default:
                        break;
                }
            }
            // Repite mientras no se elija "Volver" en el menú
            while (!option.equalsIgnoreCase("v"));
        }
        else
        {
            System.out.println("No se seleccionó a ningún estudiante");
        }

        // Retorna al panel anterior
        return null;
    }

    /**
     * Solicita al usuario que ingrese una matricula para localizar a un alumno.
     * @return Objeto Alumno encontrado o null si este no fue encontrado o si se
     * canceló la operación
     */
    private Student getStudent()
    {
        // Declara una variable para almacenar al alumno localizado
        Student student = null;

        // Bucle que permite repetir la búsqueda de un alumno
        do
        {
            // Solicita al usuario que ingrese la matricula de un alumno
            String enrollment = console.readString(
                "Ingrese la matricula del alumno (5 caracteres)",
                5);

            // Bloque para intentar obtener al alumno
            try
            {
                // Realiza una consulta en la base de datos
                student = dbContext.getStudent(enrollment);
            }
            catch (Exception e)
            {
                // Avisa al usuario de que hubo un error
                System.out.println("Error al intentar consultar datos");
            }

            // Verifica si no se estableció un objeto alumno
            if (student == null)
            {
                System.out.println("No se pudo localizar la matricula");

                // De ser así, crea un nuevo menú para preguntar si se 
                Menu menu = appContext.createMenu();
                menu.addItem("s", "Si");
                menu.addItem("n", "No");
                menu.setTitle("¿Desea volver a intentarlo?");

                // Muestra el menú y espera por una opción
                String option = menu.show();

                // Verifica si la opción selecciona es "No"
                if (option.equalsIgnoreCase("n"))
                {
                    // Termina el bucle
                    break;
                }
            }

        // Indica que el bucle deberá ejecutarse mientras la variable Alumno sea
        // null
        } while (student == null);

        // Devuelve el resultado obtenido
        return student;
    }

    /**
     * Muestra la información de un alumno.
     * @param student Objeto con la información de un alumno
     */
    private void showStudentInfo(Student student)
    {
        // Declara un arreglo para almacenar los tutores de un alumno
        Tutor tutors[];

        // Crea una cadena de texto formateada con la información del alumno
        String info = "Información de estudiante\n\n" +
            "Matricula: " + student.enrollment + "\n" +
            "Nombre: " + student.name + "\n" +
            "Apellido paterno: " + student.firstSurname + "\n" +
            "Apellido materno: " + student.lastSurname + "\n" +
            "Genero: " + student.gender + "\n" +
            "Edad: " + student.age + " años\n" +
            "Fecha de nacimiento: " + student.dateOfBirth + "\n" +
            "CURP: " + student.curp + "\n" +
            "NSS: " + student.nss + "\n" +
            "Calle: " + student.addressStreet + "\n" +
            "Numero: " + student.addressNumber + "\n" +
            "Colonia: " + student.addressDistrict + "\n" +
            "Código Postal: " + student.addressPostalCode;

        // Bloque para intentar obtener a los tutores registrados del alumno
        try
        {
            // Realiza una consulta en la base de datos
            tutors = dbContext.getStudentTutors(student.enrollment);

            // Añade una sección para mostrar los tutores registrados
            info += "\n\nTutores registrados\n";

            // Bucle que recorre el arreglo de tutores obtenidos
            for (var tutor : tutors)
            {
                // Agrega la información del tutur al texto de información
                info += String.format(
                    "\n%s %s %s - %s",
                    tutor.name,
                    tutor.firstSurname,
                    tutor.lastSurname,
                    tutor.kinship);
            }
        }
        catch (Exception e)
        {
            // Avisa al usuario de que hubo un error
            System.out.println("Error al obtener tutores asociados a alumno");
            // Crea un arreglo vacío
            tutors = new Tutor[0];
        }

        // Imprime la información del tutor
        System.out.println(info);

        // Crea un menú y le añade algunas opciones
        String option;
        Menu menu = appContext.createMenu();
        menu.addItem("e", "Editar información del alumno");
        menu.addItem("t", "Gestionar tutores registrados");
        menu.addItem("v", "Volver al menú anterior");

        // Bucle que permite repetir el menú
        do
        {
            // Muestra el menú y espera por una opción
            option = menu.show("Seleccione una opción");

            // Procesa la opción escogida
            switch (option) 
            {
                // Editar información
                case "e":
                case "E":
                    editStudentInfo(student);
                    break;
                // Gestionar tutores
                case "t":
                case "T":
                    manageStudentTutors(student);
                    break;
                default:
                    break;
            }

        // Repite mientras no se elija "Volver"
        } while (!option.equalsIgnoreCase("v"));
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
        Menu menu = appContext.createMenu();
        menu.addItem("a", "Actualizar");
        menu.addItem("v", "Volver a ingresar datos");
        menu.addItem("c", "Cancelar");

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
    private void showStudentInvoices(Student student)
    {
        // Bloque para intentar obtener la lista de pagos
        try
        {
            // Realiza una consulta de obtención de pagos para el alumno actual
            var invoices = dbContext.getStudentInvoices(student.enrollment);

            // Bucle que recorre el arreglo de pagos obtenidos
            for (Invoice invoice : invoices)
            {
                // Crea una cadena de texto con la información de cada pago
                String displayText = String.format(
                    "#%d\t%s\t%d-%d\t$%.2f",
                    invoice.folio,
                    invoice.date,
                    invoice.period.startingDate.getYear(),
                    invoice.period.endingDate.getYear(),
                    invoice.totalAmount);

                // Imprime cada registro por línea
                System.out.println(displayText);
            }
        }
        catch (Exception e)
        {
            // Avisa al usuario de que hubo un error
            System.out.println("Error al obtener los pagos del alumno");
        }
    }

    /**
     * Muestra la lista de grupos en los que ha pertenecido un alumno.
     * @param student Objeto con la información de un alumno
     */
    private void showStudentGroups(Student student)
    {
        // Bloque para intentar obtener los grupos
        try
        {
            // Realiza la consulta de obtención de grupos para el alumno actual
            var groups = dbContext.getStudentGroups(student.enrollment);

            // Bloque que recorre la lista de grupos obtenidos
            for (Group group : groups)
            {
                // Crea una cadena de texto con la información de cada pago
                String displayText = String.format(
                    "%s\t%d-%s\t%d-%d",
                    group.level.description,
                    group.grade,
                    group.letter,
                    group.period.startingDate.getYear(),
                    group.period.endingDate.getYear());

                // Imprime cada registro por línea
                System.out.println(displayText);
            }
        }
        catch (Exception e)
        {
            // Avisa al usuario de que hubo un error
            System.out.println("Error al intentar obtener los grupos del alumno");
        }
    }

    /**
     * Gestiona los tutores de un alumno.
     * @param student Objeto con la información de un alumno
     */
    private void manageStudentTutors(Student student)
    {
        // Declara un arreglo para almacenar los tutores obtenidos
        Tutor[] tutors;

        // Bloque para intentar obtener los tutores
        try
        {
            // Realiza la consulta de obtención de tutores
            tutors = dbContext.getStudentTutors(student.enrollment);
        }
        catch (Exception e)
        {
            // Avisa al usuario de que hubo un error
            System.out.println("Error al obtener tutores asociados a alumno");
            // Termina la función
            return;
        }

        // Crea un nuevo menú
        System.out.println("Tutores registrados");
        Menu menu = appContext.createMenu(
            "Seleccione a un tutor para ver la información de este o " +
            "elija una acción a realizar");

        // Bucle que recorre la lista de tutores obtenidos
        for (int i = 0; i < tutors.length; i++)
        {
            // Obtiene al tutor en la posición dada por el ciclo
            var tutor = tutors[i];
            // Crea una cadena de texto formateada con la información del tutor
            String optionText = String.format(
                "%s - %s %s %s",
                tutor.kinship,
                tutor.name,
                tutor.firstSurname,
                tutor.lastSurname);

            // Añade al tutor como opción en el menú
            menu.addItem(Integer.toString(i), optionText);
        }

        // Agrega algunas opciones adicionales al menú para que aparezcan al 
        // final de este
        menu.addBlankLine(); // Linea en blanco
        menu.addItem("a", "Agregar a un tutor");
        menu.addItem("v", "Volver al menú anterior");

        // Bucle para repetir el menú cada vez que se selecciona ver a un tutor
        do
        {
            // Muestra el menú y espera a que el usuario seleccione una opción
            String option = menu.show("Seleccione una opción");

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
        // Dirige al panel de registro para registrar a un nuevo tutor
        var result = goTo(Location.StudentRegistrationPanel, student);

        // Verifica si la instancia devuelta corresponde a un objeto Tutor
        if (result instanceof Tutor)
        {
            // Bloque para intentar realizar la asociación
            try 
            {
                // Convierte y asigna el objeto con la información de un tutor
                Tutor tutor = (Tutor)result;
                dbContext.registerStudentWithTutor(student, tutor);
            }
            catch (Exception e)
            {
                // Avisa al usuario de que hubo un error
                System.out.println(
                    "Error al intentar asociar el alumno con el tutor");
            }
        }
    }

    /**
     * Muestra la información de un tutor.
     * @param tutor Objeto con la información de un tutor
     */
    private void showTutorInfo(Tutor tutor)
    {
        // Crea un menú y le añade algunas opciones
        String option;
        Menu menu = appContext.createMenu();
        menu.addItem("e", "Editar correo eléctronico");
        menu.addItem("a", "Agregar número telefónico");
        menu.addItem("q", "Quitar número telefónico");
        menu.addItem("v", "Volver al menú anterior");

        // Bucle que repite el menú
        do
        {
            // Crea una cadena de texto formateada con la información del tutor
            String info = "Información de tutor\n\n" + 
                "Nombre: " + tutor.name + "\n" +
                "Apellido paterno: " + tutor.firstSurname + "\n" +
                "Apellido materno: " + tutor.lastSurname + "\n" +
                "Parentesco: " + tutor.kinship + "\n" +
                "Correo electronico: " + tutor.email + "\n" +
                "RFC: " + tutor.rfc + "\n" +
                "Telefonos: ";

            // Bucle que recorre la lista de números de telefonos
            for (var phone : tutor.phones)
            {
                // Agrega el número de teléfono a la lista
                info += "\n- " + phone.phone;
            }
            
            // Imprime la información del tutor
            System.out.println(info);

            // Muestra el menú y espera por una opción
            option = menu.show("Seleccione una opción");
            
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
        Menu menu = appContext.createMenu();

        // Bucle que repite el menú una y otra vez
        do
        {
            // Limpia el menú
            menu.clearItems();

            // Bucle que recorre cada número en la lista de números de teléfono
            for (int i = 0; i < tutor.phones.size(); i++)
            {
                // Obtiene el número de teléfono basado en el índice dado
                var phone = tutor.phones.get(i);
                menu.addItem(Integer.toString(i), phone.phone);
            }

            // Agrega una línea blanca y una opción para regresar
            menu.addBlankLine();
            menu.addItem("v", "Volver al menú anterior");

            System.out.println("Quitando números de teléfono");

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

        // Repite el bucle infinitamente
        } while (true);
    }
}
