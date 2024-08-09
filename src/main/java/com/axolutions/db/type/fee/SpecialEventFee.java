package com.axolutions.db.type.fee;

import java.time.LocalDate;

/**
 * Representa un cobro por un evento especial.
 */
public class SpecialEventFee extends Fee 
{
    public String specialEventCode;

    /**
     * Concepto.
     */
    public String concept;

    /**
     * Fecha programada del evento.
     */
    public LocalDate scheduledDate;
}
