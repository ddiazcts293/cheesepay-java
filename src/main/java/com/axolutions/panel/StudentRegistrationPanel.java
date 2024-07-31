package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.type.*;
import com.axolutions.panel.args.SearchType;
import com.axolutions.util.Menu;

public class StudentRegistrationPanel extends BasePanel 
{
    /**
     * TODO: Panel de registro de alumnos
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

    public StudentRegistrationPanel(AppContext appContext)
    {
        super(appContext);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args) 
    {
        System.out.println("Panel de registro de alumnos");
        
        Menu menu = appContext.createMenu();
        String option;
        Tutor tutor = null;

        System.out.println("¿El tutor del alumno está registrado?");
        menu.addItem("s", "Si");
        menu.addItem("n", "No");
        option = menu.show();

        switch (option)
        {
            case "s":
            {
                var result = appContext.goToAndReturn(
                    Location.SearchPanel,
                    Location.StudentRegistrationPanel,
                    SearchType.Tutor);

                if (result instanceof Tutor)
                {
                    tutor = (Tutor)tutor;
                }

                break;
            }
            case "n":
                tutor = createTutor();
                break;
            default:
                break;
        }

        if (tutor != null)
        {
            Student student = createStudent();

            if (student != null)
            {
                registerStudent(tutor, student);
            }
            else
            {
                System.out.println("Operación cancelada");
            }
        }
        else
        {
            System.out.println("No se ha seleccionado a un tutor");
        }

        return null;
    }

    private Tutor createTutor()
    {
        Tutor newTutor = new Tutor();
        String option;
        Menu menu = appContext.createMenu();
        menu.addItem("1", "Continuar");
        menu.addItem("2", "Volver a ingresar datos");
        menu.addItem("3", "Cancelar");
        
        do
        {
            newTutor.name = console.readString(
                "Nombre de pila (requerido, 30 caracteres max.)", 
                3, 30);
            newTutor.firstSurname = console.readString(
                "Apellido paterno (requerido, 30 caracteres max.)", 
                3);
            newTutor.lastSurname = console.readString(
                "Apellido materno (opcional, 30 caracteres max.)",
                0, 
                30);
            newTutor.email = console.readString(
                "Correo electronico (requerido)", 
                10,
                40);
            newTutor.rfc = console.readString(
                "RFC (requerido, 13 caracteres)",
                14,
                14);
            newTutor.kinship = console.readString(
                "Parentesco (requerido, 10 caracteres)",
                3,
                10);

            option = menu.show();
        } while (option.compareTo("2") == 0);

        switch (option) 
        {
            case "3":
                return null;
            default:
                return newTutor;
        }
    }

    private Student createStudent()
    {
        Student student = new Student();
        String option;
        Menu menu = appContext.createMenu();
        menu.addItem("1", "Continuar");
        menu.addItem("2", "Volver a ingresar datos");
        menu.addItem("3", "Cancelar");
        
        do
        {
            student.name = console.readString(
                "Nombre de pila (requerido, 30 caracteres max.)", 
                3, 30);
            student.firstSurname = console.readString(
                "Apellido paterno (requerido, 30 caracteres max.)", 
                3);
            student.lastSurname = console.readString(
                "Apellido materno (opcional, 30 caracteres max.)",
                0, 
                30);
            student.gender = console.readString(
                "Genero (opcional, 10 caracteres max.)",
                0,
                10);
            student.age = console.readInt(
                "Edad (requerido, 3 min.)",
                3);
            student.dateOfBirth = console.readDate(
                "Fecha de nacimiento");
            student.curp = console.readString(
                "CURP (requerido, 19 caracteres)",
                19,
                20);
            student.nss = console.readString(
                "NSS (opcional, 11 caracteres)",
                11,
                12);
            student.addressStreet = console.readString(
                "Calle (requerido, 10 caracteres max.)",
                3,
                10);
            student.addressNumber = console.readString(
                "Numero (requerido, 20 caracteres max.)",
                1,
                20);
            student.addressDistrict = console.readString(
                "Colonia",
                2,
                30);
            student.addressPostalCode= console.readString(
                "Codigo postal (requerido, 5 caracteres max.)",
                1,
                5);

            option = menu.show();
        } while (option.compareTo("2") == 0);

        switch (option) {
            case "3":
                return null;
            default:
                return student;
        }
    }

    private void registerStudent(Tutor tutor, Student student)
    {
        try 
        {
            System.out.println("Registrando tutor...");
            dbContext.registerTutor(tutor);
            System.out.println("Registrando alumno...");


        } 
        catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
