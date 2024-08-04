package com.axolutions.db.type.fee;

import com.axolutions.db.type.EducationLevel;

/**
 * Representa una couta por gastos de papelería.
 */
public class StationeryFee extends BaseFee
{
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
