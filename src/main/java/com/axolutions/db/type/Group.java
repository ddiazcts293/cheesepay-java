package com.axolutions.db.type;

public class Group 
{
    public int number;
    public int grade;
    public String letter;

    public ScholarPeriod period = new ScholarPeriod();
    public EducationLevel level = new EducationLevel();

    public int studentCount;

    @Override
    public String toString() 
    {
        return String.format(
            "%s|%d-%s|%d-%d|%d",
            level.description,
            grade,
            letter,
            period.startingDate.getYear(),
            period.endingDate.getYear(),
            studentCount);
    }
}
