package com.axolutions.db.type.fee;

import com.axolutions.db.type.EducationLevel;

/**
 * Representa una couta por gastos de papelería.
 */
public class StationeryFee extends Fee
{
    public StationeryFee()
    {
        super(FeeType.Stationery);
    }
    
    /**
     * Código de papeleria.
     */
    public String starioneryCode;

    /**
     * Concepto.
     */
    public String concept;
    
    /**
     * Grado.
     */
    public int grade;

    /**
     * Nivel educativo.
     */
    public EducationLevel level = new EducationLevel();
}
