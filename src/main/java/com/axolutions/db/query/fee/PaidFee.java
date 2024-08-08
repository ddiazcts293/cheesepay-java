package com.axolutions.db.query.fee;

import java.time.LocalDate;
import com.axolutions.db.type.ScholarPeriod;

public abstract class PaidFee 
{
    public int paymentFolio;
    public String code;
    public LocalDate paymentDate;
    public float cost;
    public ScholarPeriod period = new ScholarPeriod();

    public String studentId;
    public String studentName;
    public int tutorNumber;
    public String tutorName;
}
