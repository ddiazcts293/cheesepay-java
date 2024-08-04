package com.axolutions.db.type;

import java.time.LocalDate;

public class Student 
{
    public String enrollment;
    public String name;
    public String firstSurname;
    public String lastSurname;
    public String gender;
    public LocalDate dateOfBirth;
    public int age;
    public String addressStreet;
    public String addressNumber;
    public String addressDistrict;
    public String addressPostalCode;
    public String curp;
    public String nss;

    public ScholarPeriod period = new ScholarPeriod();
    public EducationLevel level = new EducationLevel();
    
    @Override
    public String toString() 
    {
        return String.format(
            "%s|%s %s %s|%s|%s",
            enrollment,
            name,
            firstSurname,
            lastSurname,
            gender,
            curp);
    }
}
