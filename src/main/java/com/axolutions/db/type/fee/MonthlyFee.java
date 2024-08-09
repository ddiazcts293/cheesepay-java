package com.axolutions.db.type.fee;

import java.time.LocalDate;
import com.axolutions.db.type.EducationLevel;

/**
 * Representa una cuota de mensualidad.
 */
public class MonthlyFee extends Fee
{
    public String monthlyCode;

    /**
     * Fecha límite de pago.
     */
    public LocalDate dueDate;

    /**
     * Indica si la cuota corresponde a un periodo vacacional.
     */
    public boolean isVacationMonth;

    /**
     * Indica el nivel educativo.
     */
    public EducationLevel level = new EducationLevel();
}
