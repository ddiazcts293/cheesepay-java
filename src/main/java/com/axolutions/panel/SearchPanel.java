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

    public SearchPanel(AppContext appContext)
    {
        super(appContext, Location.SearchPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args) 
    {
        // Verifica si el panel es llamado desde otro panel
        if (args.getObj() instanceof SearchType)
        {
            SearchType searchType = (SearchType)args.getObj();
            
            switch (searchType) 
            {
                case Student:
                {
                    var student = searchStudent();
                    return setLocation(Location.Previous, student);
                }
                case Tutor:
                {
                    var tutor = searchTutor();
                    return setLocation(Location.Previous, tutor);
                }
                default:
                    break;
            }
        }
        // De lo contrario, muestra el menú de búsqueda
        else
        {   
            System.out.println("Panel de búsqueda");

            var student = searchStudent();
            if (student != null)
            {
                return setLocation(
                    Location.StudentInformationPanel, 
                    student);
            }
        }

        return null;
    }

    private Student searchStudent()
    {
        Student student = null;
        
        do
        {
            System.out.println("Buscar a un alumno\n");
            System.out.println("Puede ingresar un término que corresponda " +
                "a un nombre, apellido o CURP");
            String text = console.readString("Texto");
    
            if (text.length() != 0)
            {
                try 
                {
                    var list = dbContext.searchForStudents(text);
                    if (list.length == 0)
                    {
                        System.out.println("La busqueda no arrojo resultados");
                    }
                    else
                    {
                        student = selectStudent(list);
                    }    
                } 
                catch (Exception e) 
                {
                    System.out.println(
                        "Error al intentar buscar en la base de datos");
                }
            }

            if (student == null)
            {
                Menu menu = appContext.createMenu();
                menu.setTitle("¿Desea continuar buscando?");
                menu.addItem("s", "Si");
                menu.addItem("n", "No");

                if (menu.show().equalsIgnoreCase("n"))
                {
                    break;
                }
            }
        } while (student == null);

        return student;
    }
    
    private Tutor searchTutor()
    {
        Tutor tutor = null;

        do
        {
            System.out.println("Buscar a un tutor\n");
            System.out.println("Puede ingresar un término que corresponda " +
                "a un nombre, apellido, RFC, correo electrónico, o telefono");
            String text = console.readString("Texto");

            if (text.length() != 0)
            {
                try 
                {
                    var list = dbContext.searchForTutors(text);
                    if (list.length == 0)
                    {
                        System.out.println("La busqueda no arrojo resultados");
                    }
                    else
                    {
                        tutor = selectTutor(list);
                    }

                } 
                catch (Exception e) 
                {
                    System.out.println(
                        "Error al intentar buscar en la base de datos");
                }
            }

            if (tutor == null)
            {
                Menu menu = appContext.createMenu();
                menu.setTitle("¿Desea continuar buscando?");
                menu.addItem("s", "Si");
                menu.addItem("n", "No");

                if (menu.show().equalsIgnoreCase("n"))
                {
                    break;
                }
            }
        } while (tutor == null);

        return tutor;
    }
    
    private Student selectStudent(Student[] students)
    {
        Menu menu = appContext.createMenu();
        int count = 0;

        for (Student student : students) 
        {
            String option = Integer.toString(count++);
            String displayText = String.format(
                "%s\t%s %s %s\t%s", 
                student.enrollment,
                student.name,
                student.firstSurname,
                student.lastSurname,
                student.curp);

            menu.addItem(option, displayText);
        }

        menu.addBlankLine();
        menu.addItem("v", "Volver al menú anterior");

        System.out.println("Alumnos encontrados\n");
        System.out.println("[#]\tMatricula\tNombre completo\tCURP");
        String option = menu.show("Escoja una elemento");

        if (option.equalsIgnoreCase("v"))
        {
            return null;
        }

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
                "%s %s %s\t%s\t%s", 
                tutor.name,
                tutor.firstSurname,
                tutor.lastSurname,
                tutor.email,
                tutor.rfc);

            menu.addItem(option, displayText);
        }

        menu.addBlankLine();
        menu.addItem("v", "Volver al menú anterior");

        System.out.println("Tutores encontrados\n");
        System.out.println("[#]\tNombre completo\tCorreo electronico\tRFC");
        String option = menu.show("Escoja una elemento");

        if (option.equalsIgnoreCase("v"))
        {
            return null;
        }

        int index = Integer.parseInt(option);
        return tutors[index];
    }
}
