package com.axolutions.db.query.fee;

import com.axolutions.db.type.EducationLevel;

public class PaidStationery extends PaidFee
{
    public String concept;
    public EducationLevel level = new EducationLevel();
}
