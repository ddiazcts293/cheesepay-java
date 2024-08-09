package com.axolutions.panel;

import com.axolutions.AppContext;

/**
 * Representa la clase del menú principal.
 */
public class MainMenuPanel extends BasePanel
{
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
        String option = helper.createMenu()
            .setTitle("Bienvenido/a a CheesePay, ¿qué le gustaría hacer?")
            .addItem("1", "Registrar a un nuevo alumno")
            .addItem("2", "Ver información de alumno")
            .addItem("3", "Buscar a un alumno/tutor")
            .addItem("4", "Consultar grupos")
            .addItem("5", "Consultar cobros y pagos")
            //.addItem("7", "Ir a panel de control")
            .addItem("6", "Cambiar de cuenta")
            .addItem("X", "Salir del programa")
            .show();

        switch (option)
        {
            case "1":
                return setLocation(Location.EnrollmentPanel);
            case "2":
                return setLocation(Location.InfoPanel);
            case "3":
                return setLocation(Location.SearchPanel);
            case "4":
                return setLocation(Location.GroupQueryPanel);
            case "5":
                return setLocation(Location.FeeQueryPanel);
            case "6":
                //return setLocation(Location.ControlPanel);
            case "7":
                return setLocation(Location.LoginPanel);
            default:
                System.out.println("\n¡Gracias por utilizar CheesePay!");
                return setLocation(Location.Exit);
        }
    }
}
