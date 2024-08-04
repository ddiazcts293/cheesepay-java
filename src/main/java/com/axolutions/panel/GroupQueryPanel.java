package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.EducationLevel;
import com.axolutions.db.type.Group;
import com.axolutions.db.type.ScholarPeriod;
import com.axolutions.db.type.Student;
import com.axolutions.util.Menu;

/**
 * Representa el panel de consulta de grupos.
 */
public class GroupQueryPanel extends BasePanel
{
    /**
     * DONE: Panel de consulta de grupos
     *
     * Aquí se muestran a los alumnos que pertenecen a los diferentes grupos,
     * ya sea por ciclo escolar, grado y nivel educativo
     * 1. Inicio
     * 2. Preguntar el ciclo escolar, nivel educativo, grado y grupo ya sea
     *    mostrando un menú para cada opción o que el usuario lo ingrese
     *    directamente
     * 3. Mostrar los alumnos que pertenecen a esos grupos
     * 4. Fin
     *
     * CONSULTAS:
     * - Obtener lista de alumnos de un grupo en un ciclo y nivel educativo
     *   DATOS:
     *   -
     */

    /**
     * Crea un nuevo objeto GroupQueryPanel.
     * @param appContext Instancia del objeto AppContext
     */
    public GroupQueryPanel(AppContext appContext)
    {
        super(appContext, Location.GroupQueryPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        System.out.println("Consulta de grupos");

        // Declara las variables para almacenar los parametros de búsqueda
        EducationLevel selectedLevel = null;
        ScholarPeriod selectedPeriod = null;

        // Crea un menú para establecer uno de los parámetros requeridos
        String option;
        Menu menu = createMenu("Filtrado de grupos")
            .addItem("c", "Seleccionar ciclo escolar")
            .addItem("n", "Seleccionar nivel educativo")
            .addItem("v", "Volver al menú principal");

        // Bucle para repetir el menú
        do
        {
            // Muestra el menú y espera por una opción
            option = menu.show("Seleccione una opción");
            // Procesa la opción escogida
            switch (option)
            {
                // Niveles educativos
                case "n":
                case "N":
                    selectedLevel = selectEducationLevel();
                    break;
                // Ciclos escolares
                case "c":
                case "C":
                    selectedPeriod = selectScholarPeriod();
                    break;
                // Otra opción, en este caso "Volver"
                default:
                    return null;
            }

            // Verifica si tanto nivel como periodo fueron seleccionados
            if (selectedLevel != null && selectedPeriod != null)
            {
                // Muestra los grupos
                showGroups(selectedLevel, selectedPeriod);
                // Reestablece los parámetros
                selectedLevel = null;
                selectedPeriod = null;
            }

        // Indica que el bucle se ejecutará infinitamente
        } while (true);
    }

    /**
     * Selecciona un nivel educativo de una lista.
     * @return Objeto que representa un nivel educativo
     */
    private EducationLevel selectEducationLevel()
    {
        // Declara las variables
        EducationLevel selection = null;
        EducationLevel[] levels;

        // Bloque para intentar obtener los niveles educativos
        try
        {
            levels = dbContext.getEducationLevels();
        }
        catch (Exception e)
        {
            /// Avisa de que hubo un error
            System.out.println("Error al obtener los datos de los niveles " +
                "educativos");

            // Termina la función sin devolver nada
            return null;
        }

        // Crea y muestra un menú
        String option = createMenu("Niveles educativos")
            .addItems(levels)
            .addBlankLine()
            .addItem("v", "Volver al menú principal")
            .show("Seleccione una opción");

        // Verifica si la opción escogida no es "Volver"
        if (!option.equalsIgnoreCase("v"))
        {
            // Obtiene el objeto correspondiente al nivel educativo seleccionado
            // basandose en el valor de la opción convertido a entero
            int index = Integer.parseInt(option);
            selection = levels[index];
        }

        // Retorna el nivel educativo seleccionado
        return selection;
    }

    /**
     * Selecciona un periodo escolar de una lista
     * @return Objeto que representa un periodo escolar
     */
    private ScholarPeriod selectScholarPeriod()
    {
        // Declara las variables
        ScholarPeriod selection = null;
        ScholarPeriod[] periods;

        // Bloque para intentar obtener los periodos escolares
        try
        {
            periods = dbContext.getScholarPeriods();
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener los datos de ciclos " +
                "escolares");
            
                // Termina la función sin devolver nada
            return null;
        }

        // Crea y muestra un menú
        String option = createMenu("Ciclos escolares")
            .setHeader("Ciclo|Fechas inicio-fin")
            .addItems(periods)
            .addBlankLine()
            .addItem("v", "Volver al menú principal")
            .show("Seleccione una opción");

        // Verifica si la opción escogida no es "Volver"
        if (!option.equalsIgnoreCase("v"))
        {
            // Obtiene el objeto correspondiente al ciclo escolar seleccionado
            // basandose en el valor de la opción convertido a entero
            int index = Integer.parseInt(option);
            selection = periods[index];
        }

        // Retorna el ciclo escolar seleccionado
        return selection;
    }

    /**
     * Muestra los grupos en un nivel educativo y un ciclo escolar definidos.
     * @param level Nivel educativo
     * @param period Ciclo escolar
     */
    private void showGroups(EducationLevel level, ScholarPeriod period)
    {
        // Declara una variable para almacenar los grupos
        Group[] groups;

        // Bloque para intentar obtener los grupos
        try
        {
            groups = dbContext.getGroups(period.code, level.code);
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener los grupos");
            // Termina la función
            return;
        }

        // Crea una cadena de texto para la cabecera del menú
        String title = String.format(
            "Mostrando grupos para %s en el ciclo escolar %d-%d\n\n" +
            "Seleccione un grupo para obtener un listado de los alumnos",
            level.description,
            period.startingDate.getYear(),
            period.endingDate.getYear());

        // Crea el menú y le añade las opciones
        Menu menu = createMenu(title)
            .setHeader("[#] - Nivel|Grado y grupo|Periodo|Cantidad de alumnos")
            .addItems(groups)
            .addBlankLine()
            .addItem("v", "Volver al menú anterior");

        // Bucle para repetir el menú luego de ver selecciona una opción
        do
        {
            // Muestra el menú y espera por una opción
            String option = menu.show("Seleccione un grupo");

            // Verifica si la opción escogida es "Volver"
            if (option.equalsIgnoreCase("v"))
            {
                // Termina el bucle
                break;
            }

            // Obtiene el objeto correspondiente al grupo seleccionado basandose
            // en el valor de la opción convertida a entero
            int index = Integer.parseInt(option);
            var group = groups[index];
            
            // Muestra los alumnos del grupo
            showGroupStudents(group);
        
        // Indica que el bucle se ejecutará infinitamente
        } while (true);
    }

    /**
     * Muestra a los alumnos de un grupo.
     * @param group Grupo seleccionado
     */
    private void showGroupStudents(Group group)
    {
        // Declara una variable para almacenar la opción escogida
        String option;

        // Bucle que repite el menú
        do
        {
            // Crea una cadena de texto formateada con la información del grupo
            String info = String.format("Información de grupo\n\n" +
                "Identificador: %d\n" +
                "Grado: %d\n" +
                "Letra: %s\n" +
                "Periodo escolar: %d-%d\n" + 
                "Fecha inicial: %s\n" +
                "Fecha final: %s\n" +
                "Cantidad de alumnos: %d\n\n" +
                "Seleccione a un alumno o elija una acción a realizar",
                group.number,
                group.grade,
                group.letter,
                group.period.startingDate.getYear(),
                group.period.endingDate.getYear(),
                group.period.startingDate,
                group.period.endingDate,
                group.studentCount);

            // Declara una variable para almacenar los alumnos del grupo actual
            Student[] students;

            // Bloque para intentar obtener los alumnos del grupo
            try
            {
                students = dbContext.getGroupStudents(group);
            }
            catch (Exception e)
            {
                // Avisa ocurrió un error
                System.out.println(
                    "Error al obtener los alumnos de los grupos");

                // Termina la función
                students = new Student[0];
            }

            // Crea un menú, lo muestra y espera por una opción
            option = createMenu(info)
                .setHeader("[#] - Matricula|Nombre completo|Género|CURP")
                .addItems(students)
                .addBlankLine()
                .addItem("v", "Volver al menú anterior")
                .show();

            // Verifica si la opción escogida es "Volver"
            if (option.equalsIgnoreCase("v"))
            {
                // Finaliza el bucle
                break;
            }

            // A partir de este punto se considera que se eligió a un alumno, 
            // por lo que se convierte la opción escogida a número y se obtiene 
            // el objeto del alumno correspondiente
            int index = Integer.parseInt(option);
            // Navega hacia la pantalla de información de alumno pasando el
            // objeto con la informació del alumno seleccionado
            goTo(Location.StudentInformationPanel, students[index]);
            
        // Repite el bucle infinitamente
        } while (true);
    }
}
