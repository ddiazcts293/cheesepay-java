package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.util.*;
import com.axolutions.db.type.*;
import com.axolutions.panel.args.SearchType;

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

    public StudentInformationPanel(AppContext appContext)
    {
        super(appContext);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args) 
    {
        System.out.println("Panel de información de alumno");

        Menu menu = appContext.createMenu();
        String option;
        Student student = null;

        System.out.println("\n¿Conoce la matricula del alumno?");
        menu.addItem("s", "Si");
        menu.addItem("n", "No");
        option = menu.show();

        switch (option) 
        {
            case "s":
                student = getStudent();            
                break;
            case "n":
            {
                var result = appContext.goToAndReturn(
                    Location.SearchPanel, 
                    Location.StudentInformationPanel,
                    SearchType.Student);
                
                if (result instanceof Student)
                {
                    student = (Student)result;
                }
                
                break;
            }
        }

        if (student != null)
        {
            String studentInfo = String.format(
                "Alumno: %s - %s %s %s", 
                student.enrollment,
                student.name,
                student.firstSurname,
                student.lastSurname);
            
            menu.clearItems();
            menu.addItem("i", "Ver informacion personal");
            menu.addItem("t", "Ver tutores registrados");
            menu.addItem("g", "Ver los grupos en los que ha estado inscrito");
            menu.addItem("h", "Consultar historial de pagos");
            menu.addItem("e", "Regresar");
            
            do
            {
                System.out.println(studentInfo);
                option = menu.show("Seleccione una opción");

                switch (option) {
                    case "i":
                        showStudentInfo(student);
                        break;
                    case "t":
                        showStudentTutors(student);
                        break;
                    case "g":
                        showStudentGroups(student);
                        break;
                    case "h":
                        showStudentInvoices(student);
                        break;
                    default:
                        break;
                }
            }
            while (option.compareTo("e") != 0);
        }
        else
        {
            System.out.println("No se seleccionó a un estudiante");
        }

        return null;
    }

    private Student getStudent()
    {
        String enrollment = console.readString(
            "Ingrese la matricula del alumno",
            5, 
            5);

        try 
        {
            return dbContext.getStudent(enrollment);    
        } 
        catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    private void showStudentInfo(Student student)
    {
        String info = 
            "Matricula: " + student.enrollment + "\n" +
            "Nombre: " + student.name + "\n" +
            "Apellido paterno: " + student.firstSurname + "\n" +
            "Apellido materno: " + student.lastSurname + "\n" +
            "Genero: " + student.gender + "\n" +
            "Edad: " + student.age + " años\n" +
            "Fecha de nacimiento: " + student.dateOfBirth + "\n" +
            "CURP" + student.curp + "\n" +
            "NSS" + student.nss + "\n" +
            "Calle: " + student.addressStreet + "\n" +
            "Numero: " + student.addressNumber + "\n" +
            "Colonia " + student.addressDistrict + "\n" +
            "CP: " + student.addressPostalCode;

        System.out.println("Información de estudiante");
        System.out.println(info);
    }

    private void showStudentTutors(Student student)
    {
        Menu menu = appContext.createMenu();

        try 
        {    
            var tutors = dbContext.getStudentTutors(student.enrollment);
            int count = 0;

            for (Tutor tutor : tutors) 
            {
                String option = Integer.toString(count++);
                String displayText = String.format(
                    "%s %s %s", 
                    tutor.name,
                    tutor.firstSurname,
                    tutor.lastSurname);

                menu.addItem(option, displayText);
            }

            menu.addItem("r", "Regresar");

            do {
                String option = menu.show("Seleccione una opción");
                
                if (option.compareTo("r") == 0)
                {
                    return;
                }

                int index = Integer.parseInt(option);
                var tutorSelected = tutors[index];
                showTutorInfo(tutorSelected);
            
            } while (true);
        } 
        catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showStudentInvoices(Student student)
    {
        try 
        {
            var invoices = dbContext.getStudentInvoices(student.enrollment);
            
            for (Invoice invoice : invoices) 
            {
                String displayText = String.format(
                    "#%d %s (%s - %s) $%.2f", 
                    invoice.folio,
                    invoice.date,
                    invoice.period.startingDate,
                    invoice.period.endingDate,
                    invoice.totalAmount);
                
                System.out.println(displayText);
            }
        }
        catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showStudentGroups(Student student)
    {
        try 
        {
            var groups = dbContext.getStudentGroups(student.enrollment);
            
            for (Group group : groups) 
            {
                String displayText = String.format(
                    "%s %d %s (%s - %s)", 
                    group.level.description,
                    group.grade,
                    group.letter,
                    group.period.startingDate,
                    group.period.endingDate);
                
                System.out.println(displayText);
            }
        }
        catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showTutorInfo(Tutor tutor)
    {
        String info = 
            "Numero: " + tutor.number + "\n" +
            "Nombre: " + tutor.name + "\n" +
            "Apellido paterno: " + tutor.firstSurname + "\n" +
            "Apellido materno: " + tutor.lastSurname + "\n" +
            "Parentesco: " + tutor.kinship + "\n" +
            "Correo electronico: " + tutor.email + " años\n" +
            "RFC: " + tutor.rfc + "\n" +
            "Telefonos: ";
        
        for (String phone : tutor.phones) 
        {
            info += phone + " ";
        }

        info = info.trim();

        System.out.println("Información de tutor");
        System.out.println(info);
    }
}
