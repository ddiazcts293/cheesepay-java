package com.axolutions.panel;

import com.axolutions.AppContext;

public class MainMenuPanel extends BasePanel
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

    public MainMenuPanel(AppContext appContext)
    {
        super(appContext);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        var menu = appContext.createMenu();
        menu.setTitle("Bienvenido, ¿qué le gustaría hacer?");
        menu.addItem("1", "Registrar a un alumno nuevo");
        menu.addItem("2", "Registrar pago");
        menu.addItem("3", "Buscar a tutores/alumnos");
        menu.addItem("4", "Ver información de alumno");
        menu.addItem("5", "Consultar grupos");
        menu.addItem("6", "Consultar cobros");
        menu.addItem("7", "Ir a panel de control");
        menu.addItem("8", "Cambiar de cuenta");
        menu.addItem("9", "Salir");

        switch (menu.show())
        {
            case "1":
                return nextDestination(Location.StudentRegistrationPanel);
            case "2":
                return nextDestination(Location.PaymentRegistrationPanel);
            case "3":
                return nextDestination(Location.SearchPanel);
            case "4":
                return nextDestination(Location.StudentInformationPanel);
            case "5":
                return nextDestination(Location.GroupQueryPanel);
            case "6":
                return nextDestination(Location.PaymentQueryPanel);
            case "7":
                return nextDestination(Location.ControlPanel);
            case "8":
                return nextDestination(Location.LoginPanel);
            default:
                return nextDestination(Location.Exit);
        }
    }
}
