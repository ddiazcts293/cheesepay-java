package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.EducationLevel;
import com.axolutions.db.type.Group;
import com.axolutions.db.type.ScholarPeriod;
import com.axolutions.db.type.Student;
import com.axolutions.util.Menu;

public class GroupQueryPanel extends BasePanel
{
    /**
     * DOING: Panel de consulta de grupos
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

        // Declara las variables para almacenar el 
        EducationLevel selectedLevel = null;
        ScholarPeriod selectedPeriod = null;
        String option;
        Menu menu = createMenu("Filtrado de grupos");
        menu.addItem("c", "Seleccionar ciclo escolar");
        menu.addItem("n", "Seleccionar nivel educativo");
        menu.addItem("v", "Volver al menú principal");

        do
        {
            option = menu.show("Seleccione una opción: ");
            
            switch (option) 
            {
                case "n":
                    selectedLevel = selectEducationLevel();
                    break;
                case "c":
                    selectedPeriod = selectScholarPeriod();
                    break;
                default:
                    return null;
            }

            if (selectedLevel != null && selectedPeriod != null)
            {
                showGroups(selectedLevel, selectedPeriod);
                selectedLevel = null;
                selectedPeriod = null;
            }
        } while (true);
    }

    private EducationLevel selectEducationLevel()
    {
        EducationLevel selection = null;
        EducationLevel[] levels;
        String option;

        try 
        {
            levels = dbContext.getEducationLevels();
        }
        catch (Exception e) 
        {
            System.out.println("Error al obtener los datos de los niveles " +
                "educativos");

            return null;
        }

        Menu menu = createMenu("Niveles educativos");
        for (int i = 0; i < levels.length; i++) 
        {
            var level = levels[i];
            menu.addItem(Integer.toString(i), level.description);
        }

        menu.addBlankLine();
        menu.addItem("v", "Volver al menú principal");

        option = menu.show("Seleccione una opción");
        if (!option.equalsIgnoreCase("v"))
        {
            int index = Integer.parseInt(option);
            selection = levels[index];
        }
        
        return selection;
    }

    private ScholarPeriod selectScholarPeriod()
    {
        ScholarPeriod selection = null;
        ScholarPeriod[] periods;
        String option;

        try 
        {
            periods = dbContext.getScholarPeriods();
        }
        catch (Exception e) 
        {
            System.out.println("Error al obtener los datos de ciclos " +
                "escolares");
            return null;
        }

        Menu menu = createMenu("Ciclos escolares");
        for (int i = 0; i < periods.length; i++) 
        {
            var level = periods[i];
            String description = String.format("%d - %d", 
                level.startingDate.getYear(),
                level.endingDate.getYear());

            menu.addItem(Integer.toString(i), description);
        }

        menu.addBlankLine();
        menu.addItem("v", "Volver al menú principal");

        option = menu.show("Seleccione una opción");
        if (!option.equalsIgnoreCase("v"))
        {
            int index = Integer.parseInt(option);
            selection = periods[index];
        }
        
        return selection;
    }

    private void showGroups(EducationLevel level, ScholarPeriod period)
    {
        Group[] groups;

        try 
        {
            groups = dbContext.getGroups(period.code, level.code);
        }
        catch (Exception e) 
        {
            System.out.println("Error al obtener los grupos");
            return;
        }

        String text = String.format(
            "Mostrando grupos para %s en el ciclo escolar %d-%d\n",
            level.description,
            period.startingDate.getYear(),
            period.endingDate.getYear());
        
        System.out.println(text);
        Menu menu = createMenu();
        menu.setTitle("Nivel\t\tGrupo\tPeriodo\tEstudiantes");
        
        for (int i = 0; i < groups.length; i++) 
        {
            var group = groups[i];
            text = String.format("%s\t%d-%s\t%d-%d\t%d",
                group.level.description,
                group.grade,
                group.letter,
                group.period.startingDate.getYear(),
                group.period.endingDate.getYear(),
                group.studentCount);

            menu.addItem(Integer.toString(i), text);
        }

        menu.addBlankLine();
        menu.addItem("v", "Volver al menú anterior");

        do 
        {
            String option = menu.show("Seleccione un grupo");
            if (option.equalsIgnoreCase("v"))
            {
                return;
            }

            int index = Integer.parseInt(option);
            var group = groups[index];
            showGroupStudents(group);
        } while (true);
    }

    private void showGroupStudents(Group group)
    {
        Student[] students;

        try 
        {
            students = dbContext.getGroupStudents(group);
        }
        catch (Exception e)
        {
            System.out.println("Error al obtener los alumnos de los grupos");
            return;
        }

        System.out.println("Matricula\tNombre\tCiclo\tNivel");

        for (int i = 0; i < students.length; i++) 
        {
            var student = students[i];
            String text = String.format("%s\t%s %s %s\t%d-%d\t%s", 
                student.enrollment,
                student.name,
                student.firstSurname,
                student.lastSurname,
                student.period.startingDate.getYear(),
                student.period.endingDate.getYear(),
                student.level.description);

            System.out.println(text);
        }

        console.pause("Presione ENTER para continar...");
    }
}
