package com.axolutions.db.query.payment;

import com.axolutions.db.type.EducationLevel;

public class PaidUniform extends PaidFee
{
    public String concept;
    public String size;
    public String uniformType;
    public EducationLevel level = new EducationLevel();
}
