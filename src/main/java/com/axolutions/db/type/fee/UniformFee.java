package com.axolutions.db.type.fee;

import com.axolutions.db.type.EducationLevel;

/**
 * Representa un cobro por uniforme
 */
public class UniformFee extends Fee
{
    public UniformFee()
    {
        super(FeeType.Uniform);
    }

    /**
     * Código de uniforme.
     */
    public String uniformCode;
    
    /**
     * Concepto.
     */
    public String concept;

    /**
     * Talla del uniforme.
     */
    public String size;

    /**
     * Tipo de uniforme
     */
    public UniformType type = new UniformType();

    /**
     * Nivel educativo.
     */
    public EducationLevel level = new EducationLevel();
}
