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
        return readInt(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
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
                System.out.print(prompt + ": ");
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

    /**
     * Borra el contenido de la pantalla.
     */
    public void clearDisplay()
    {
        // using flag 2; implement another flags
        System.out.printf("\033[%dJ\033[H", 2);
    }

    /**
     * Detiene la ejecución del programa hasta que se pulse ENTER.
     * @param prompt Indicación que es mostrada al usuario
     */
    public void pause(String prompt)
    {
        System.out.print(prompt);
        scanner.nextLine();
    }

    /**
     * Imprime un arreglo de elementos como una tabla.
     * @param <T> Tipo de elemento
     * @param header Cabecera de la tabla, en la cual las columnas deberán estar
     * separadas por el carácter de barra vertical |.
     * @param items Arreglo de elementos.
     */
    public <T> void printAsTable(String header, T[] items)
    {
        // Patrón de división
        String regex = "[\\|]";
        // Divide la cabecera por partes utilizando '|' como separador
        String[] headerColumns = header.split(regex);
        // Crea un arreglo para almacenar las longitudes máximas de las columnas
        int[] maxColumnLengths = new int[headerColumns.length];

        // Paso 1: establecer las longitudes máximas iniciales de acuerdo a las
        // longitudes de las cabeceras de columnas
        for (int i = 0; i < headerColumns.length; i++) 
        {
            maxColumnLengths[i] = headerColumns[i].length() + 2;
        }

        // Paso 2: establecer longitudes de acuerdo a los máximos de cada 
        // columna para cada elemento
        for (var item : items) 
        {
            // Obtiene un arreglo con los campos del elemento
            String[] itemColumns = item.toString().split(regex, headerColumns.length);

            // Bucle que recorre la lista campos de un elemento para establecer 
            // las logitudes máximas de cada columna
            for (int i = 0; i < itemColumns.length; i++) 
            {
                // Obtiene la longitud del campo y la incrementa en 2 para que
                // los campos no queden muy juntos
                int fieldLength = itemColumns[i].length() + 2;

                // Verifica si la longitud del campo es mayor que el limite ant
                if (fieldLength > maxColumnLengths[i])
                {
                    // Establece la nueva longitud para la columna
                    maxColumnLengths[i] = fieldLength;
                }
            }
        }

        // Paso 3: imprime la cabecera de la tabla
        for (int i = 0; i < headerColumns.length; i++) 
        {
            // Obtiene el texto de la columna
            String text = headerColumns[i];
            // Obtiene la longitud máxima de la columna
            int maxColumnLength = maxColumnLengths[i];
            // Obtiene la longitud restante
            int remainingLength = maxColumnLength - text.length();

            // Imprime el texto de la columna y reposiciona el desplazamiento 
            // del cursor horizontalmente basándose en la longitud restante
            System.out.printf("%s\033[%dC", text, remainingLength);
        }

        // Salto de línea
        System.out.println();

        // Paso 2: imprime cada elemento de la tabla
        for (var item : items) 
        {
            // Obtiene un arreglo con los campos del elemento
            String[] itemColumns = item.toString().split(regex, headerColumns.length);

            // Bucle que recorre el arreglo de campos
            for (int i = 0; i < itemColumns.length; i++) 
            {
                // Obtiene el texto del campo
                String text = itemColumns[i];
                // Obtiene la longitud máxima de la columna
                int maxColumnLength = maxColumnLengths[i];
                int remainingLength = maxColumnLength - text.length();


                // Imprime el texto de la columna y reposiciona el desplazamiento 
                // del cursor horizontalmente basándose en la longitud restante
                System.out.printf("%s\033[%dC", text, remainingLength);
            }

            // Imprime un salto de línea después de terminar de agregar todas 
            // las columnas
            System.out.println();
        }

        // Imprime un salto de línea al final de la tabla
        System.out.println();
    }
}
