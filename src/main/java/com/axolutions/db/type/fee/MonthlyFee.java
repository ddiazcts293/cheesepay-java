package com.axolutions.db.type.fee;

import java.time.Month;
import com.axolutions.db.type.EducationLevel;

/**
 * Representa una cuota de mensualidad.
 */
public class MonthlyFee extends BaseFee
{
    /**
     * Mes en el que aplica.
     */
    public Month month;

    /**
     * Indica si la cuota corresponde a un periodo vacacional.
     */
    public boolean isVacationMonth;

    /**
     * Indica el nivel educativo.
     */
    public EducationLevel level = new EducationLevel();
}
