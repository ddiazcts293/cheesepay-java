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
        String option = createMenu()
            .setTitle("Bienvenido/a a CheesePay, ¿qué le gustaría hacer?")
            .addItem("1", "Registrar a un nuevo alumno")
            .addItem("2", "Ver información de alumno")
            .addItem("3", "Buscar a un alumno/tutor")
            .addItem("4", "Consultar grupos")
            .addItem("5", "Consultar costos de cobros")
            .addItem("6", "Ir a panel de control")
            .addItem("7", "Cambiar de cuenta")
            .addItem("8", "Salir")
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
                return setLocation(Location.ControlPanel);
            case "7":
                return setLocation(Location.LoginPanel);
            default:
                System.out.println("¡Gracias por utilizar CheesePay!");
                return setLocation(Location.Exit);
        }
    }
}
