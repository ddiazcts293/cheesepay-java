package com.axolutions.db.type.fee;

import com.axolutions.db.type.SchoolPeriod;

/**
 * Representa la clase base para los diferentes cobros o tarifas.
 */
public abstract class Fee 
{
    /**
     * Código o identificador del cobro.
     */
    public String code;

    /**
     * Costo.
     */
    public float cost;

    /**
     * Ciclo escolar.
     */
    public SchoolPeriod period = new SchoolPeriod();
}
