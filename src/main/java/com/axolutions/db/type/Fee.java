package com.axolutions.db.type;

import com.axolutions.db.type.fee.*;

/**
 * Representa una clase que engloba todos los cobros o tarifas.
 */
public class Fee 
{
    /**
     * Código del cobro.
     */
    public String code;

    /**
     * Cobro por un evento especial.
     */
    public SpecialEventFee specialEvent;

    /**
     * Cuota de manteinimiento.
     */
    public MaintenanceFee maintenance;

    /**
     * Cuota por gastos de papelería.
     */
    public StationeryFee stationery;

    /**
     * Cuota de mensualidad.
     */
    public MonthlyFee monthly;

    /**
     * Cuota de inscripción
     */
    public EnrollmentFee enrollment;

    /**
     * Cobro por uniforme
     */
    public UniformFee uniform;

    /**
     * Periodo escolar.
     */
    public ScholarPeriod period = new ScholarPeriod();
}
