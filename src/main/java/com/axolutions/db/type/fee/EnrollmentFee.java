package com.axolutions.db.type.fee;

import com.axolutions.db.type.EducationLevel;

/**
 * Representa una couta de inscripción.
 */
public class EnrollmentFee extends Fee
{
    public EnrollmentFee() 
    {
        super(FeeType.Enrollment);
    }

    /**
     * Código de inscripción
     */
    public String enrollmentCode;

    /**
     * Nivel educativo
     */
    public EducationLevel level = new EducationLevel();
}
