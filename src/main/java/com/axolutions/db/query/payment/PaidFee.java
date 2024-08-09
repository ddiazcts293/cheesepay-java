package com.axolutions.db.query.payment;

import java.time.LocalDate;
import com.axolutions.db.type.SchoolPeriod;

public class PaidFee 
{
    public int paymentFolio;
    public String code;
    public LocalDate paymentDate;
    public float cost;
    public SchoolPeriod period = new SchoolPeriod();

    public String studentId;
    public String studentName;
    public int tutorNumber;
    public String tutorName;
}
