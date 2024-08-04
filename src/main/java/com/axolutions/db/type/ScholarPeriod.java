package com.axolutions.db.type;

import java.time.LocalDate;

public class ScholarPeriod 
{
    public String code;
    public LocalDate startingDate;
    public LocalDate endingDate;
    
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
