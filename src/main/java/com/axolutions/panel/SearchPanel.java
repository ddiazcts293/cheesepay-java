package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.*;
import com.axolutions.panel.args.SearchType;

/**
 * Representa el panel de búsqueda de alumnos.
 */
public class SearchPanel extends BasePanel
{
    /**
     * DONE: Panel de búsqueda
     *
     * En este panel se podrá realizar la busqueda de tutores y alumnos
     *
     * Algortimo:
     * - Buscar a un alumno
     *
     * Para ambos casos:
     * 1. Inicio
     * 3. Solicitar un dato (se puede preguntar a partir de una lista de campos
     *    o simplemente solicitarlo y buscar coincidencias en todos los campos)
     * 4. Realizar la consulta
     * 5. Mostrar los resultados obtenidos
     * 6. Preguntar si desea ver la información de un elemento, en cuyo caso se
     *    pide que seleccione el número de elemento, o bien si desea volver al
     *    menú principal.
     * 7. Fin
     */

    /**
     * Crea un nuevo objeto SearchPanel.
     * @param appContext Instancia del objeto AppContext
     */
    public SearchPanel(AppContext appContext)
    {
        super(appContext, Location.SearchPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        System.out.println("Panel de búsqueda");

        // Verifica si el panel es llamado desde otro panel
        if (args.getObj() instanceof SearchType)
        {
            // Convierte y asigna el indicado de búsqueda
            SearchType searchType = (SearchType)args.getObj();

            // Procesa el tipo de búsqueda
            switch (searchType)
            {
                // Alumno
                case Student:
                {
                    // Llama a la función de búsqueda y devuelve el resultado
                    var student = searchStudent();
                    return setLocation(Location.Previous, student);
                }
                // Tutor
                case Tutor:
                {
                    // Llama a la función de búsqueda y devuelve el resultado
                    var tutor = searchTutor();
                    return setLocation(Location.Previous, tutor);
                }
                default:
                    break;
            }
        }

        // De lo contrario, dirige al búsqueda de alumnos
        var student = searchStudent();

        // Verifica si se pudo localizar a un alumno
        if (student != null)
        {
            // Dirige al panel de información de alumno
            return setLocation(Location.StudentInfoPanel, student);
        }

        // Retorna nulo
        return null;
    }

    /**
     * Realiza la búsqueda de un alumno.
     * @return Objeto con la información de un alumno
     */
    private Student searchStudent()
    {
        // Declara una variable para almacenar a un alumno
        Student student = null;

        // Bucle para repetir la búsqueda
        do
        {
            // Imprime información
            String info = "\nBuscar a un alumno\n" +
                "Puede ingresar un término que corresponda a un nombre, " +
                "apellido o CURP\n";
            System.out.println(info);

            // Solicita un término de búsqueda
            String text = console.readString("Texto");
            System.out.println();

            // Verifica que la longitud de la cadena ingresada sea mayor que 0
            if (text.length() > 0)
            {
                // Bloque para intentar buscar
                try
                {
                    // Obtiene una lista de coincidencias
                    var list = dbContext.searchForStudents(text);
                    // Verifica si la lista contiene elementos
                    if (list.length > 0)
                    {
                        // De ser así, solicita al usuario que seleccione un
                        // elemento de la lista
                        student = selectStudent(list);
                    }
                    else
                    {
                        // De lo contrario, muestra un mensaje
                        System.out.println("La busqueda no arrojó resultados");
                    }
                }
                catch (Exception e)
                {
                    // Muestra un mensaje de error
                    System.out.println(
                        "Error al intentar buscar en la base de datos");
                }
            }

            // Verifica si no se selccionó a un alumno
            if (student == null)
            {
                // De ser así, pregunta si desea volver a realizar otra búsqueda
                String option = showYesNoMenu("¿Desea continuar buscando?");

                // Verifica si la opción escogida es "No"
                if (option.equalsIgnoreCase("n"))
                {
                    // Termina el bucle
                    break;
                }
            }

        // Repite mientras no se selccione a un alumno
        } while (student == null);

        // Retorna el alumno seleccionado o nulo
        return student;
    }

    /**
     * Realiza la búsqueda de un tutor.
     * @return Objeto con la información de un tutor
     */
    private Tutor searchTutor()
    {
        // Declara una variable para almacenar a un tutor
        Tutor tutor = null;

        // Bucle para repetir la búsqueda
        do
        {
            // Imprime información
            String info = "\nBuscar a un tutor\n" +
                "Puede ingresar un término que corresponda a un nombre, " +
                "apellido, RFC o correo electrónico\n";
            System.out.println(info);

            // Solicita un término de búsqueda
            String text = console.readString("Texto");

            // Verifica que la longitud de la cadena ingresada sea mayor que 0
            if (text.length() != 0)
            {
                // Bloque para intentar buscar
                try
                {
                    // Obtiene una lista de coincidencias
                    var list = dbContext.searchForTutors(text);
                    // Verifica si la lista contiene elementos
                    if (list.length > 0)
                    {
                        // De ser así, solicita al usuario que seleccione un
                        // elemento de la lista
                        tutor = selectTutor(list);
                    }
                    else
                    {
                        // De lo contrario, muestra un mensaje
                        System.out.println("La busqueda no arrojo resultados");
                    }
                }
                catch (Exception e)
                {
                    // Muetra un mensaje de error
                    System.out.println(
                        "Error al intentar buscar en la base de datos");
                }
            }

            // Verifica si no se seleccionó a un tutor
            if (tutor == null)
            {
                // De ser así, pregunta si desea volver a realizar otra búsqueda
                String option = showYesNoMenu("¿Desea continuar buscando?");

                // Verifica si la opción escogida es "No"
                if (option.equalsIgnoreCase("n"))
                {
                    // Termina el bucle
                    break;
                }
            }

        // Repite mientras no se seleccione a un tutor
        } while (tutor == null);

        // Retorna el tutor seleccionado
        return tutor;
    }

    /**
     * Realiza la selección de un alumno.
     * @param students Lista de alumnos
     * @return Objeto con la información de un alumno
     */
    private Student selectStudent(Student[] students)
    {
        // Crea una cadena de texto para mostrar como cabecera del menú
        String title = "Alumnos encontrados: " + students.length + "\n\n" +
            "Seleccione a un alumno o elija una acción a realizar";
     
        // Crea un nuevo menú, lo muestra y espera por una opción
        return selectFromList(students, title,
            "[#] - Matricula|Nombre completo|Género|CURP");
    }

    /**
     * Realiza la selección de un tutor.
     * @param tutors Lista de tutores
     * @return Objeto con la información de un tutor
     */
    private Tutor selectTutor(Tutor[] tutors)
    {
        // Crea una cadena de texto para mostrar como cabecera
        String title = "Tutores encontrados: " + tutors.length + "\n\n" +
            "Seleccione a un tutor o elija una acción a realizar";

        // Crea un nuevo menú, lo muestra y espera por una opción
        return selectFromList(tutors, title,
            "[#] - Parentesco|Nombre|Correo electronico|RFC");
    }
}
