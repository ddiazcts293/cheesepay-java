package com.axolutions;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) 
    {
        // Lee el host de la base de datos
        String dbHost = readDbHost();
        // Crea la cadena de conexión con la BD
        String dbConnString = "jdbc:mysql://" + dbHost + "/SistemaEscolar";

        // Crea los objetos que se utilizarán a lo largo de la ejecución
        Scanner scanner = new Scanner(System.in);
        AppContext appContext = new AppContext(scanner, dbConnString);

        // Comienza la diversión
        appContext.run();

        // Cierra el objeto Scanner
        scanner.close();
    }

    // Función que lee el host de la base de datos indicado por el archivo
    // db_host.
    private static String readDbHost()
    {
        String host;

        // Bloque en el que se intenta abrir el archivo
        try 
        {
            // Crea una objeto File indicando la ruta de un archivo
            File file = new File("./db_host");

            // Verifica que el archivo exista, no sea un directorio y que se 
            // pueda leer
            if (!file.exists() || file.isDirectory() || !file.canRead())
            {
                throw new Exception();
            }

            // Crea un objeto Scanner para leer el archivo
            Scanner reader = new Scanner(file);
            host = reader.nextLine();
            reader.close();
        } 
        catch (Exception e) 
        {
            // Establece una dirección preterminada
            host = "localhost";
        }

        return host;
    }
}
