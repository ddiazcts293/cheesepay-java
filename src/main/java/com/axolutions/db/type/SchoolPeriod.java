package com.axolutions.db.type;

import java.time.LocalDate;

public class SchoolPeriod 
{
    public String code;
    public LocalDate startingDate;
    public LocalDate endingDate;
    
    public String getPeriodString()
    {
        return String.format("%d-%d",
            startingDate.getYear(),
            endingDate.getYear());
    }

    @Override
    public String toString() 
    {
        return String.format(
            "%d-%d|%s|%s",
            startingDate.getYear(),
            endingDate.getYear(),
            startingDate,
            endingDate);
    }
}
