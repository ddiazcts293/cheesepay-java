package com.axolutions.panel;

import com.axolutions.AppContext;

public class StudentRegistrationPanel implements BasePanel 
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
     * 2. Verificar primero que haya cupo para el grado y nivel de educación 
     *    (son 12 alumnos por cada grupo)
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

    @Override
    public Destination show(AppContext appContext) 
    {
        System.out.println("Panel de registro de alumnos");
        
        System.out.print("Presione una tecla...");
        appContext.getScanner().nextLine();

        return Destination.Back;
    }
    
}
