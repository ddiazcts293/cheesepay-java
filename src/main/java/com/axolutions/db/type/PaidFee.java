package com.axolutions.db.type;

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
    public Fee fee;

    /**
     * Costo pagado.
     */
    public float cost;
}
