package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.*;
import com.axolutions.panel.args.SearchType;
import com.axolutions.util.Menu;

public class SearchPanel extends BasePanel 
{
    /**
     * DONE: Panel de búsqueda
     * 
     * En este panel se podrá realizar la busqueda de tutores y alumnos
     * 
     * Algortimo:
     * - Buscar a un tutor
     * - Buscar a un alumno
     *
     * Para ambos casos:
     * 1. Inicio
     * 2. Preguntar que desea buscar (tutor o alumno)
     * 3. Solicitar un dato (se puede preguntar a partir de una lista de campos
     *    o simplemente solicitarlo y buscar coincidencias en todos los campos)
     * 4. Realizar la consulta
     * 5. Mostrar los resultados obtenidos
     * 6. Preguntar si desea ver la información de un elemento, en cuyo caso se
     *    pide que seleccione el número de elemento, o bien si desea volver al 
     *    menú principal.
     * 7. Fin
     */

    public SearchPanel(AppContext appContext)
    {
        super(appContext);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args) 
    {
        // Verifica si el panel es llamado desde otro panel
        if (args.obj instanceof SearchType)
        {
            SearchType searchType = (SearchType)args.obj;
            
            switch (searchType) 
            {
                case Student:
                {
                    var student = searchStudent();
                    return nextDestination(Location.Previous, student);
                }
                case Tutor:
                {
                    var tutor = searchTutor();
                    return nextDestination(Location.Previous, tutor);
                }
                default:
                    break;
            }
        }
        // De lo contrario, muestra el menú de búsqueda
        else
        {   
            Menu menu = appContext.createMenu("Panel de búsqueda");
            menu.addItem("1", "Alumno");
            menu.addItem("2", "Tutor");

            switch (menu.show("¿Qué ")) 
            {
                case "1":
                    searchStudent();
                    break;
                case "2":
                    searchTutor();
                    break;
                default:
                    break;
            }
        }

        return null;
    }

    private Student searchStudent()
    {
        Student student = null;

        System.out.println("Buscar a un alumno");
        System.out.println("Puede buscar por medio de un nombre/apellido/CURP");
        String text = console.readString("Dato", 3);

        try 
        {
            var list = dbContext.searchStudents(text);
            if (list.length == 0)
            {
                System.out.println("La busqueda no arrojo resultados");
            }
            else
            {
                student = selectStudent(list);
            }

        } catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
        }

        return student;
    }
    
    private Tutor searchTutor()
    {
        Tutor tutor = null;

        System.out.println("Buscar a un tutor");
        System.out.println("Puede buscar por medio de un nombre/apellido/RFC/correo electrónico/telefono");
        String text = console.readString("Dato", 3);

        try 
        {
            var list = dbContext.searchTutors(text);
            if (list.length == 0)
            {
                System.out.println("La busqueda no arrojo resultados");
            }
            else
            {
                tutor = selectTutor(list);
            }

        } catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
        }

        return tutor;
    }
    
    private Student selectStudent(Student[] students)
    {
        Menu menu = appContext.createMenu("Lista de alumnos");
        int count = 0;

        for (Student student : students) 
        {
            String option = Integer.toString(count++);
            String displayText = String.format(
                "%s - %s %s %s - %s", 
                student.enrollment,
                student.name,
                student.firstSurname,
                student.lastSurname,
                student.curp);

            menu.addItem(option, displayText);
        }

        System.out.println("Matricula - Nombre completo - CURP");
        String option = menu.show("Escoja una elemento");

        int index = Integer.parseInt(option);
        return students[index];
    }

    private Tutor selectTutor(Tutor[] tutors)
    {
        Menu menu = appContext.createMenu();
        int count = 0;

        for (Tutor tutor : tutors) 
        {
            String option = Integer.toString(count++);
            String displayText = String.format(
                "%s %s %s - %s - %s", 
                tutor.name,
                tutor.firstSurname,
                tutor.lastSurname,
                tutor.email,
                tutor.rfc);

            menu.addItem(option, displayText);
        }

        System.out.println("Nombre completo - Correo electronico - RFC");
        String option = menu.show("Escoja una elemento");

        int index = Integer.parseInt(option);
        return tutors[index];
    }
}
