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

        // Bucle para repetir el menú luego de ver selecciona una opción
        do
        {
            // Muestra un menú para seleccionar un solo grupo
            var selectedGroup = selectFromList(groups, title,
                "[#] - Nivel|Grado y grupo|Periodo|Cantidad de alumnos");

            // Verifica si no se seleccionó algún elemento
            if (selectedGroup == null)
            {
                // Termina el menú
                break;
            }

            // Muestra los alumnos del grupo
            showGroupStudents(selectedGroup);
        
        // Indica que el bucle se ejecutará infinitamente
        } while (true);
    }

    /**
     * Muestra a los alumnos de un grupo.
     * @param group Grupo seleccionado
     */
    private void showGroupStudents(Group group)
    {
        // Bucle que repite el menú
        do
        {
            // Crea una cadena de texto formateada con la información del grupo
            String info = String.format("Información de grupo\n\n" +
                "Identificador: %d\n" +
                "Grado: %d\n" +
                "Letra: %s\n" +
                "Periodo escolar: %d-%d\n" + 
                "Fecha de inicio de curso: %s\n" +
                "Fecha de fin de curso: %s\n" +
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

            // Muestra un menú para seleccionar un solo alumno
            var student = selectFromList(students, info, 
                "[#] - Matricula|Nombre completo|Género|CURP");

            // Verifica no se ha seleccionado un alumno
            if (student == null)
            {
                // Termina el bucle
                break;
            }

            // Navega hacia la pantalla de información de alumno pasando el
            // objeto con la informació del alumno seleccionado
            goTo(Location.StudentInfoPanel, student);
            
        // Repite el bucle infinitamente
        } while (true);
    }
}
