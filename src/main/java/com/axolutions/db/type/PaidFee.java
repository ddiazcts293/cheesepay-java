package com.axolutions.db.type;

import com.axolutions.db.type.fee.FeeType;
import java.time.LocalDate;

/**
 * Representa un cobro o tarifa pagada.
 */
public class PaidFee 
{
    /**
     * Folio del pago asociado.
     */
    public int paymentFolio;
    
    /**
     * Tarifa pagada
     */
    public FeeType type;

    public String concept;

    public LocalDate date;

    /**
     * Costo pagado.
     */
    public float cost;

    public String studentId;

    public String studentName;

    public int tutorNumber;

    public String tutorName;

    public ScholarPeriod period = new ScholarPeriod();

    public EducationLevel level = new EducationLevel();
}
