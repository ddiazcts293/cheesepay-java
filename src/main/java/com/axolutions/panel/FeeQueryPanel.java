/**
 * Panel de consulta de cobros
 *
 * En este panel se podrán consultar los costos de cada cobro disponible,
 * según la categoría, ciclo escolar y nivel educativo
 *
 */

package com.axolutions.panel;

import com.axolutions.AppContext;
import com.axolutions.db.query.payment.StudentPayment;
import com.axolutions.db.type.*;
import com.axolutions.db.type.fee.*;

public class FeeQueryPanel extends BasePanel
{
    public FeeQueryPanel(AppContext appContext)
    {
        super(appContext, Location.FeeQueryPanel);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        System.out.println("Panel de consulta de cobros");

        // Bucle para repetir el menú de categoría de pago
        do
        {
            // Pregunta por el tipo de cobro a buscar
            FeeType type = helper.selectFeeType();

            // Verifica si no se eligió ninguna categoría
            if (type == FeeType.Unknown)
            {
                // Termina el bucle del menú de categoría
                break;
            }

            // Muestra el menú de ciclo escolar
            showSchoolPeriodMenu(type);

        // Bucle repetido infinitamente
        } while (true);

        return null;
    }

    /**
     * Muestra un menú que permite seleccionar un ciclo escolar para desplegar 
     * el listado de alumnos con un cobro específico.
     * @param feeType Tipo de cobro
     */
    private void showSchoolPeriodMenu(FeeType feeType)
    {
        String title = "\nSeleccione un cobro para mostrar el listado de " +
            "alumnos que realizaron ese pago o elija una acción a realizar";

        // Bucle para repetir el menú de ciclo escolar
        do
        {
            // Cobro seleccionado
            Fee selectedFee = null;
            // Solicita al usuario que seleccione un ciclo escolar
            SchoolPeriod selectedPeriod = helper.selectSchoolPeriod();

            // Verifica si no se seleccionó un ciclo
            if (selectedPeriod == null)
            {
                // De ser así, sale del bucle del menú de ciclo escolar
                break;
            }

            // Verifica si para la categoría de cobro anteriormente seleccionado
            // corresponde a una que necesite conocer el nivel educativo
            if (feeType == FeeType.Uniform ||
                feeType == FeeType.Stationery ||
                feeType == FeeType.Monthly)
            {
                // Muestra el menu del nivel educativo
                showEducationLevelMenu(feeType, selectedPeriod);
            }
            else
            {
                do
                {
                    // Procesa la categoría de cobro elegida
                    switch (feeType)
                    {
                        // Inscripciones
                        case Enrollment:
                            selectedFee = helper.selectEnrollmentFee(
                                selectedPeriod,
                                title);

                            break;
                        // Evento especial
                        case SpecialEvent:
                            selectedFee = helper.selectSpecialEventFee(
                                selectedPeriod,
                                title);

                            break;
                        // Mantenimiento
                        case Maintenance:
                            selectedFee = helper.selectMaintenanceFee(
                                selectedPeriod,
                                title);

                            break;
                        default:
                            break;
                    }
                    
                    // Muestra el listado alumnos que realizaron el pago por el 
                    // cobro seleccionado
                    if (selectedFee != null)
                    {
                        showStudentsWithPaidFee(selectedFee);
                    }

                // Repite mientras el cobro seleccionado no sea nulo
                } while (selectedFee != null);
            }

        // Bucle repetido infinitamente
        } while (true);
    }

    /**
     * Muestra un menú que permite seleccionar un nivel educativo para desplegar 
     * el listado de alumnos con un cobro específico.
     * @param feeType Tipo de cobro
     * @param period Ciclo escolar
     */
    private void showEducationLevelMenu(FeeType feeType, SchoolPeriod period)
    {
        String title = "\nSeleccione un cobro para mostrar el listado de " +
            "alumnos que realizaron ese pago o elija una acción a realizar";

        // Bucle para repetir el menú de selección de nivel educativo
        do
        {
            // Cobro seleccionado
            Fee selectedFee = null;
            // Solicita un nivel educativo
            EducationLevel level = helper.selectEducationLevel();
            
            // Verifica si no se seleccionó un nivel educativo
            if (level == null)
            {
                // De ser así, sale del bucle del menú de nivel educativo
                break;
            }

            do
            {
                // Procesa el tipo de pago requerido
                switch (feeType)
                {
                    // Mensualidades
                    case Monthly:
                        selectedFee = helper.selectMonthlyFee(
                            period, 
                            level, 
                            title);

                        break;
                    // Uniformes
                    case Uniform:
                        selectedFee = helper.selectUniformFee(
                            period, 
                            level, 
                            title);

                        break;
                    // Papelería
                    case Stationery:
                        selectedFee = helper.selectStationeryFee(
                            period, 
                            level, 
                            title);

                        break;
                    default:
                        break;
                }

                // Verifica si se seleccionó un cobro
                if (selectedFee != null)
                {
                    showStudentsWithPaidFee(selectedFee);
                }

            // Repite el bucle mientras el pago seleccionado no sea nulo
            } while (selectedFee != null);

        // El bucle se ejecuta infinitamente
        } while (true);
    }

    /**
     * Muestra el listado de alumnos que ha realizado el pago de un cobro 
     * específico.
     * @param fee Cobro
     */
    private void showStudentsWithPaidFee(Fee fee)
    {
        // Declara un arreglo para almacenar los alumnos obtenidos
        StudentPayment[] students;

        try
        {
            // Intenta obtener los alumnos con que han realizado el pago
            students = dbContext.getStudentsWhoHaveAFee(fee.code);
        }
        catch (Exception e)
        {
            System.out.println(
                "Error al obtener a los alumnos que han realizado el pago " +
                "del cobro indicado");

            // Termina la función
            return;
        }

        if (students.length > 0)
        {
            // Define una cabecera para la tabla
            String header = "Folio|Fecha de pago|Matricula|Alumno|Grupo|" +
                "Nivel educativo|Pagador por";
            
            // Crea un arreglo para contener la información de cada alumno
            String[] lines = new String[students.length];
            // Bucle que recorre el listado de alumnos
            for (int i = 0; i < students.length; i++)
            {
                var item = students[i];

                lines[i] = String.format(
                    "#%d|%s|%s|%s|%d-%s|%s|%s",
                    item.paymentFolio,
                    item.paymentDate,
                    item.studentId,
                    item.studentName,
                    item.grade,
                    item.letter,
                    item.level.description,
                    item.tutorName);
            }

            // Imprime la tabla de alumnos
            System.out.println("\nListado de alumnos");
            console.printAsTable(header, lines);
        }
        else
        {
            System.out.println(
                "\nNingún alumno ha realizado el pago por el cobro indicado");
        }

        console.pause("Presione ENTER para continuar...");
    }
}
