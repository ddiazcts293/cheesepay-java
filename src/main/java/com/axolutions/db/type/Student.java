package com.axolutions.db.type;

import java.time.LocalDate;

public class Student 
{
    public String studentId;
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
    public String ssn;

    public SchoolPeriod period = new SchoolPeriod();
    public EducationLevel level = new EducationLevel();
    
    @Override
    public String toString() 
    {
        return String.format(
            "%s|%s %s %s|%s|%s",
            studentId,
            name,
            firstSurname,
            lastSurname,
            gender,
            curp);
    }
}
