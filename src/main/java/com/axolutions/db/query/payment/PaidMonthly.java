package com.axolutions.db.query.payment;

import java.time.LocalDate;
import com.axolutions.db.type.EducationLevel;

public class PaidMonthly extends PaidFee 
{
    public LocalDate paidMonth;
    public boolean isVacationMonth;
    public EducationLevel level = new EducationLevel();
}