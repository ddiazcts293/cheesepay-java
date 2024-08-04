package com.axolutions.db.type;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Representa un pago que puede contener múltiples cobros o tarifas pagadas en
 * una sola exhibición.
 */
public class Payment 
{
    /**
     * Folio.
     */
    public int folio;

    /**
     * Fecha.
     */
    public LocalDate date;

    /**
     * Monto total.
     */
    public float totalAmount;

    /**
     * Ciclo escolar.
     */
    public ScholarPeriod period = new ScholarPeriod();

    /**
     * Representa una lista de cobros pagados.
     */
    public ArrayList<PaidFee> paidFees = new ArrayList<>();
}
