package com.axolutions.panel;

import com.axolutions.AppContext;

public class MainMenuPanel implements BasePanel
{
    /**
     * Menú principal
     * 
     * Agregar los siguientes apartados como opciones del menú:
     * - Registrar alumno
     * - Registrar pago
     * - Buscar tutor o alumno
     * - Ver información de alumno
     * - Consultar grupos
     * - Consultar cobros
     * - Panel de control
     */

    @Override
    public Destination show(AppContext appContext) 
    {
        System.out.println("Menú principal");

        var menu = appContext.createMenu();
        menu.AddItem("1", "Registrar alumno");
        menu.AddItem("2", "Registrar pago");
        menu.AddItem("3", "Buscar tutor o alumno");
        menu.AddItem("4", "Ver información de alumno");
        menu.AddItem("5", "Consultar grupos");
        menu.AddItem("6", "Consultar cobros");
        menu.AddItem("7", "Ir a panel de control");
        menu.AddItem("8", "Cambiar de cuenta");
        menu.AddItem("9", "Salir");

        String option = menu.show();
        switch (option) 
        {
            case "1":
                return Destination.StudentRegistrationPanel;
            case "2":
                return Destination.PaymentRegistrationPanel;
            case "3":
                return Destination.SearchPanel;
            case "4":
                return Destination.StudentInformationPanel;
            case "5":
                return Destination.GroupQueryPanel;
            case "6":
                return Destination.PaymentQueryPanel;
            case "7":
                return Destination.ControlPanel;
            case "8":
                return Destination.LoginPanel;
            default:
                return Destination.Exit;
        }
    }
}
