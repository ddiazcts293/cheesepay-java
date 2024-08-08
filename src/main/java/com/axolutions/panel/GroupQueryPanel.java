package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.EducationLevel;
import com.axolutions.db.type.Group;
import com.axolutions.db.type.ScholarPeriod;
import com.axolutions.db.type.Student;

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
        System.out.println("Panel de consulta de grupos");

        // Declara las variables para almacenar los parametros de búsqueda
        EducationLevel selectedLevel = null;
        ScholarPeriod selectedPeriod = null;

        // Bucle para repetir el menú de selección de nivel educativo
        do
        {
            selectedLevel = selectEducationLevel();
            // Verifica si no se seleccionó un nivel educativo
            if (selectedLevel == null)
            {
                // Termina el bucle
                break;
            }

            // Bucle para repetir el menú de selección de ciclo escolar
            do
            {
                selectedPeriod = selectScholarPeriod();
                // Verifica si no se seleccionó un ciclo escolar
                if (selectedPeriod == null)
                {
                    // Termina el bucle
                    break;
                }

                showGroups(selectedLevel, selectedPeriod);
            } while (true);
        } while (true);

        return null;
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

        // Intenta obtener los grupos
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
            "\nMostrando grupos para %s en el ciclo escolar %d-%d\n" +
            "Seleccione un grupo para obtener la lista de alumnos en él",
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
            String info = String.format("\nInformación de grupo\n" +
                "Identificador: %d\n" +
                "Grado: %d\n" +
                "Letra: %s\n" +
                "Ciclo escolar: %d-%d\n" +
                "Fecha de inicio del ciclo escolar: %s\n" +
                "Fecha de fin del ciclo escolar: %s\n" +
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

            // Intenta obtener los alumnos del grupo
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

            // Muestra un menú para seleccionar a un solo alumno
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
            goTo(Location.InfoPanel, student);

        // Repite el bucle infinitamente
        } while (true);
    }
}
