package com.axolutions.db.type.fee;

/**
 * Representa una couta de mantenimiento.
 */
public class MaintenanceFee extends Fee
{
    public MaintenanceFee()
    {
        super(FeeType.Maintenance);
    }

    /**
     * Número.
     */
    public int maintenanceNumber;

    /**
     * Concepto.
     */
    public String concept;
}
