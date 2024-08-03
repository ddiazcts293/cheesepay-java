package com.axolutions.db.type;

import java.util.ArrayList;
public class Tutor 
{
    public int number;
    public String name;
    public String firstSurname;
    public String lastSurname;
    public String rfc;
    public String kinship; 
    public String email;

    public ArrayList<TutorPhone> phones = new ArrayList<>();

    @Override
    public String toString() 
    {
        return String.format(
            "%s|%s %s %s|%s", 
            kinship,
            name,
            firstSurname,
            lastSurname,
            email,
            rfc);
    }
}
