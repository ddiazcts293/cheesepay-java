package com.axolutions.db.type.fee;

import com.axolutions.db.type.EducationLevel;

/**
 * Representa una couta de inscripción.
 */
public class EnrollmentFee extends BaseFee
{
    /**
     * Nivel educativo
     */
    public EducationLevel level = new EducationLevel();
}
