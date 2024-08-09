package com.axolutions.db.query.payment;

import com.axolutions.db.type.EducationLevel;

public class StudentPayment extends PaidFee
{
    public int grade;
    public String letter;
    public EducationLevel level = new EducationLevel();
}
