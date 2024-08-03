package com.axolutions.util;

import java.util.Scanner;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console
{
    private Scanner scanner;

    /**
     * Crea un objeto de tipo Console
     * 
     * @param scanner
     */
    public Console(Scanner scanner)
    {
        this.scanner = scanner;
    }

    /**
     * Lee una cadena de caracteres que es ingresada por el usuario.
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @return Cadena de caracteres
     */
    public String readString(String prompt)
    {
        return readString(prompt, 0, Integer.MAX_VALUE);
    }

    /**
     * Lee una cadena de caracteres de longitud fija que es ingresada por el
     * usuario.
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @param fixedLength Longitud fija
     * @return Cadena de caracteres
     */
    public String readString(String prompt, int fixedLength)
    {
        return readString(prompt, fixedLength, fixedLength);
    }

    /**
     * Lee una cadena de caracteres cuya longitud se encuentra dentro de un 
     * rango definido.
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @param minLength Longitud mínima requerida 
     * @param maxLength Longitud máxima requerida
     * @return Cadena de caracteres
     */
    public String readString(String prompt, int minLength, int maxLength)
    {
        String result;

        do
        {
            System.out.print(prompt + ": ");
            result = scanner.nextLine().trim();

        } while (result.length() < minLength || result.length() > maxLength);

        return result;
    }

    /**
     * Lee una contraseña que es ingresada por el usuario sin mostrarla en
     * pantalla.
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @return Cadena de caracteres
     */
    public String readPassword(String prompt)
    {
        return readPassword(prompt, 0, Integer.MAX_VALUE);
    }

    /**
     * Lee una contraseña que es ingresada por el usuario sin mostrarla en
     * pantalla.
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @param minLength Longitud mínima
     * @param maxLength Longitud máxima
     * @return Cadena de caracteres
     */
    public String readPassword(String prompt, int minLength, int maxLength)
    {
        char[] array;

        do
        {
            System.out.print(prompt + ": ");
            array = System.console().readPassword();

        } while (array.length < minLength || array.length > maxLength);

        return new String(array);
    }

    /**
     * Lee un valor entero que es ingresado por el usuario.
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @return Número entero
     */
    public int readInt(String prompt)
    {
        return readInt(prompt, Integer.MIN_VALUE - 1, Integer.MAX_VALUE);
    }

    /**
     * Lee un valor entero que es ingresado por el usuario.
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @param min Valor mínimo
     * @return Número entero
     */
    public int readInt(String prompt, int min)
    {
        return readInt(prompt, min, Integer.MAX_VALUE);
    }

    /**
     * Lee un valor entero que es ingresado por el usuario
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @param min Límite inferior
     * @param max Límite superior
     * @return Número entero
     */
    public int readInt(String prompt, int min, int max)
    {
        int result = min - 1;

        do
        {
            try
            {
                System.out.print(prompt + " :");
                result = scanner.nextInt();
                scanner.nextLine();
            }
            catch (Exception e)
            {
                continue;
            }

        } while (result < min || result > max);

        return result;
    }

    /**
     * Lee un valor de punto flotante que es ingresado por el usuario.
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @return Número float
     */
    public float readFloat(String prompt)
    {
        return readFloat(prompt, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    /**
     * Lee un valor de punto flotante que es ingresado por el usuario.
     * 
     * @param prompt Indicación que es mostrada al usuario
     * @param min Límite inferior
     * @param max Límite superior
     * @return Número float
     */
    public float readFloat(String prompt, float min, float max)
    {
        float result = Float.NaN;

        do
        {
            try
            {
                System.out.print(prompt + " : ");
                result = scanner.nextFloat();
                scanner.nextLine();
            }
            catch (Exception e)
            {
                continue;
            }

        } while (result == Float.NaN || result < min || result > max);

        return result;
    }

    /**
     * Lee una fecha que es ingresada por el usuario.
     * 
     * @param prompt Indicación que es mostrada al usuario.
     * @return Fecha en objeto LocalDate
     */
    public LocalDate readDate(String prompt)
    {
        return readDate(prompt, false);
    }

    /**
     * Lee una fecha que es ingresada por el usuario.
     * 
     * @param prompt Indicación que es mostrada al usuario.
     * @param canBeNull Indica si la fecha se puede dejar en null
     * @return Fecha en objeto LocalDate o null
     */
    public LocalDate readDate(String prompt, boolean canBeNull)
    {
        // Expresión que hace coincicir con el formato DD-MM-AAAA o AAAA-MM-DD
        String regex = "(\\d{2})[-/](\\d{1,2})[-/](\\d{4})|\\d{4}[-/](\\d{1,2})[-/](\\d{2})";

        do
        {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String enteredText = scanner.nextLine();

            if (enteredText.isBlank() && canBeNull)
            {
                return null;
            }

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(enteredText);

            if (!matcher.matches())
            {
                continue;
            }

            String[] parts = enteredText.split("[-/]");
            int[] values = new int[3];
            int year, month, day;

            try
            {
                for (int i = 0; i < values.length; i++)
                {
                    values[i] = Integer.parseInt(parts[i]);
                }
                
                month = values[1];
                if (month == 0 || month > 12)
                {
                    continue;
                }

                if (values[0] == 0 || values[0] > 31)
                {
                    year = values[0];
                    day = values[2];
                }
                else
                {
                    day = values[0];
                    year = values[2];
                }
                
                return LocalDate.of(year, month, day);
            }
            catch (Exception e)
            {
                continue;
            }
        } while (true);

        //return LocalDate.now();
    }

    public void clearDisplay()
    {
        // using flag 2; implement another flags
        System.out.printf("\033[%dJ\033[H", 2);
    }

    public void pause(String prompt)
    {
        System.out.println(prompt);
        scanner.nextLine();
    }
}
