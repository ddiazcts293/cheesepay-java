package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.*;
import com.axolutions.panel.args.SearchType;
import com.axolutions.util.Menu;

/**
 * Representa el panel de registro de alumno.
 */
public class EnrollmentPanel extends BasePanel
{
    /**
     * DONE: Panel de registro de alumnos
     *
     * Aquí es donde se lleva a cabo el registro de alumnos y tutores. Para
     * ello, es necesario recabar toda la información de estos, pero OJO, pueden
     * haber casos en los que un tutor es responsable de varios alumnos, por lo
     * que se deberá implementar un mecanismo para buscar a un tutor, ya sea por
     * medio de su número, nombre o RFC.
     *
     * 1. Inicio
     * 2. ----
     * 3. Preguntar si el tutor tiene a otros alumnos registrados. Si es así,
     *    buscar al tutor (llamar a la sección de busqueda). De lo contrario,
     *    registrarlo
     * 4. Recabar toda la información del alumno
     * 5. Pedir confirmación antes de registrar
     * 6. Registrar información de alumno en BD
     * 7. Ir al panel de pagos para registrar tanto la inscripción como la
     *    mensualidad que corresponde.
     * 8. Preguntar si desea agregar a otro estudiante
     * 9. Fin
     */

    /**
     * Crea un nuevo objeto StudentRegistrationPanel
     * @param appContext Instancia del objeto AppContext 
     */
    public EnrollmentPanel(AppContext appContext)
    {
        super(appContext, Location.EnrollmentPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        System.out.println("Panel de registro de alumnos");

        // Declara una variable para almacenar el tutor responsable del alumno
        Tutor tutor = null;
        
        // Verifica si la última ubicación es el panel de información de alumnos
        if (args.getLastLocation() == Location.StudentInfoPanel)
        {
            // Si es así, llama a la función de registrar tutor para recabar
            // información y almacenarla en la base de datos
            tutor = registerTutor();
            // Retorna el objeto Tutor registrado
            return setLocation(Location.Previous, tutor);
        }

        // Crea un nuevo menú y le añade algunas opciones
        String option;
        Menu menu = createMenu();
        menu.setTitle("¿El tutor del alumno está registrado?");
        menu.addItem("s", "Si");
        menu.addItem("n", "No");
        
        // Muestra el menú y espera una opción
        option = menu.show();

        // Procesa la opción escogida
        switch (option)
        {
            // Si
            case "s":
            case "S":
            {
                // Dirige al panel de búsqueda indicando que se deberá buscar a 
                // un tutor
                var result = goTo(Location.SearchPanel, SearchType.Tutor);
                
                // Verifica que el objeto devuelto corresponda a un tutor
                if (result instanceof Tutor)
                {
                    // Establece en la variable tutor el objeto devuelto
                    tutor = (Tutor)result;
                }

                break;
            }
            // No
            case "n":
            case "N":
                // Registra a un nuevo tutor
                tutor = registerTutor();
                break;
            default:
                break;
        }

        // Verifica si se ha seleccionado a un tutor
        if (tutor != null)
        {
            // Borra la lista de elementos en el menú anteriormente creado y 
            // añade algunas opciones
            menu.clearItems();
            menu.setTitle("¿Desea registrar a otro alumno?");
            menu.addItem("s", "Si");
            menu.addItem("n", "No");

            // Inicia un bucle
            do
            {
                // Registra a un nuevo alumno
                Student student = registerStudent(tutor);

                // Verifica si realmente se ha registrado a un tutor
                if (student == null)
                {
                    System.out.println("Registro de alumno cancelado");
                }

                // Muestra el menú y espera una opción
                option = menu.show();

            // Repite mientras se elija "Si" en el menú
            } while (option.equalsIgnoreCase("s"));
        }
        else
        {
            System.out.println("No se ha seleccionado a un tutor");
        }

        // Retorna al panel anterior
        return null;
    }

    /**
     * Recaba la información de un nuevo tutor y la registra en la base de 
     * datos.
     * @return Objeto Tutor creado o null si se canceló la operación
     */
    private Tutor registerTutor()
    {
        // Declara la variable para almacenar al tutor que se registrará
        Tutor tutor;
        
        // Crea un nuevo menú y le agrega algunas opciones
        String option;
        Menu menu = createMenu();
        menu.setTitle("¿Desea confirmar el registro?");
        menu.addItem("r", "Sí, registrar");
        menu.addItem("v", "No, volver a ingresar datos");
        menu.addItem("c", "No, cancelar");

        // Bucle que permite repetir la obtención de datos en caso de que el
        // usuario lo necesite
        do
        {
            System.out.println("Registrando tutor\n");

            // Crea un nuevo objeto Tutor
            tutor = new Tutor();
            tutor.name = console.readString(
                "Nombre de pila (requerido, 30 caracteres max.)",
                3, 30);
            tutor.firstSurname = console.readString(
                "Apellido paterno (requerido, 30 caracteres max.)",
                3, 30);
            tutor.lastSurname = console.readString(
                "Apellido materno (opcional, 30 caracteres max.)",
                0,  30);
            tutor.email = console.readString(
                "Correo electronico (requerido, 40 caracteres max.)",
                10, 40);
            tutor.rfc = console.readString(
                "RFC (requerido, 13 caracteres)", 13);
            tutor.kinship = console.readString(
                "Parentesco (requerido, 10 caracteres)",
                3, 10);

            // Muestra el menú para confirmar el registro y espera que el 
            // usuario seleccione una opción
            option = menu.show();

            // Si se eligio "Registrar", realiza el registro
            if (option.equalsIgnoreCase("r"))
            {
                // Bloque para intentar realizar el registro
                try
                {
                    // Llama a la función que registra al tutor
                    dbContext.registerTutor(tutor);
                    // En caso de no fallar, termina el bucle y retorna el
                    // objeto Tutor creado
                    return tutor;
                }
                catch (Exception e)
                {
                    // Crea y muestra un submenú para preguntar si desea volver
                    // a ingresar los datos o si desea cancelar el registro
                    option = createMenu()
                        .setTitle("Error al guardar los cambios")
                        .addItem("v", "Volver a ingresar datos")
                        .addItem("c", "Cancelar")
                        .show();
                }
            }

            // Si se eligio "Cancelar", termina el bucle
            if (option.equalsIgnoreCase("c"))
            {
                // Como no se registro a un tutor, retorna null
                return null;
            }

        // Indica que el bucle deberá ejecutarse infinitamente
        } while (true);
    }

    /**
     * Recaba la información de un nuevo alumno y la registra en la base de 
     * datos.
     * @param tutor Objeto Tutor que es responsable del alumno
     * @return Objeto Alumno creado o null si se cancelo la operación
     */
    private Student registerStudent(Tutor tutor)
    {
        // Declara las variables para almacenar al alumno que se registra
        Student student;
        String option;

        // Crea un nuevo menú y le agrega algunas opciones
        Menu menu = createMenu();
        menu.setTitle("¿Desea confirmar el registro?");
        menu.addItem("r", "Sí, Registrar");
        menu.addItem("v", "No, volver a ingresar datos");
        menu.addItem("c", "No, cancelar");

        // Bucle que permite repetir la obtención de datos en caso de que el
        // usuario lo necesite
        do
        {
            System.out.println("Registrando alumno\n");

            // Crea un nuevo objeto Alumno 
            student = new Student();
            student.name = console.readString(
                "Nombre de pila (requerido, 30 caracteres max.)",
                3, 30);
            student.firstSurname = console.readString(
                "Apellido paterno (requerido, 30 caracteres max.)",
                3, 30);
            student.lastSurname = console.readString(
                "Apellido materno (opcional, 30 caracteres max.)",
                0, 30);
            student.gender = console.readString(
                "Genero (opcional, 10 caracteres max.)",
                0, 10);
            student.age = console.readInt(
                "Edad (requerido, 3 min.)", 3);
            student.dateOfBirth = console.readDate(
                "Fecha de nacimiento");
            student.curp = console.readString(
                "CURP (requerido, 19 caracteres)",19);
            student.ssn = console.readString(
                "NSS (opcional, 11 caracteres)",
                0, 11);
            student.addressStreet = console.readString(
                "Calle (requerido, 30 caracteres max.)",
                3, 30);
            student.addressNumber = console.readString(
                "Numero (requerido, 20 caracteres max.)",
                1, 20);
            student.addressDistrict = console.readString(
                "Colonia (requerido, 30 caracteres max.)",
                2, 30);
            student.addressPostalCode= console.readString(
                "Codigo postal (requerido, 5 caracteres max.)",
                1, 5);

            // Muestra el menú para confirmar el registro y espera que el 
            // usuario seleccione una opción
            option = menu.show();

            // Si se eligio "Registrar", realiza el registro
            if (option.equalsIgnoreCase("r"))
            {
                // Bloque para intentar realiza el registro
                try
                {
                    // Llama a la función que registra al alumno
                    dbContext.registerStudent(student);
                    dbContext.registerStudentWithTutor(student, tutor);

                    // En caso de no fallar, termina el bucle y retorna el
                    // objeto Alumno creado
                    return student;
                }
                catch (Exception e)
                {
                    // Avisa al usuario de que hubo un error
                    
                    // Crea y muestra un submenú para preguntar si desea volver
                    // a ingresar los datos o si desea cancelar el registro
                    option = createMenu()
                        .setTitle("Error al guardar los cambios")
                        .addItem("v", "Volver a ingresar datos")
                        .addItem("c", "Cancelar")
                        .show();
                }
            }

            // Si se eligio "Cancelar", termina el bucle
            if (option.equalsIgnoreCase("c"))
            {
                // Como no se registro a un alumno, retorna null
                return null;
            }

        // Indica que el bucle deberá ejecutarse infinitamente
        } while (true);
    }
}
