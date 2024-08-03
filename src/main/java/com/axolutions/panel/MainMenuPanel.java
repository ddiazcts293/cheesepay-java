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

    /**
     * Crea un nuevo objeto MainMenuPanel.
     * @param appContext Instancia del objeto AppContext
     */
    public MainMenuPanel(AppContext appContext)
    {
        super(appContext, Location.MainMenu);
    }

    @Override
    public PanelTransitionArgs show(PanelTransitionArgs args)
    {
        var menu = appContext.createMenu();
        menu.setTitle("Bienvenido, ¿qué le gustaría hacer?");
        menu.addItem("1", "Registrar a un nuevo alumno");
        menu.addItem("2", "Ver información de alumno");
        menu.addItem("3", "Buscar a un alumno");
        menu.addItem("4", "Consultar grupos");
        menu.addItem("5", "Consultar costos de cobros");
        menu.addItem("6", "Ir a panel de control");
        menu.addItem("7", "Cambiar de cuenta");
        menu.addItem("8", "Salir");

        switch (menu.show())
        {
            case "1":
                return setLocation(Location.StudentRegistrationPanel);
            case "2":
                return setLocation(Location.StudentInformationPanel);
            case "3":
                return setLocation(Location.SearchPanel);
            case "4":
                return setLocation(Location.GroupQueryPanel);
            case "5":
                return setLocation(Location.PaymentQueryPanel);
            case "6":
                return setLocation(Location.ControlPanel);
            case "7":
                return setLocation(Location.LoginPanel);
            default:
                return setLocation(Location.Exit);
        }
    }
}
