package com.axolutions.util;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Representa un menú interactivo en el que un usuario puede seleccionar una
 * opción.
 */
public class Menu
{
    /**
     * Representa un elemento del menú
     */
    private class MenuItem
    {
        private String key;
        private String text;
        private boolean isRow;

        public MenuItem(String key, String text, boolean isRow)
        {
            this.key = key;
            this.text = text;
            this.isRow = isRow;
        }

        public String getKey()
        {
            return key;
        }

        public String getText()
        {
            return text;
        }
        
        public boolean isRow() 
        {
            return isRow;
        }
    }

    private Scanner scanner;
    private Console console;
    private ArrayList<MenuItem> items;
    private String title;
    private String header;
    private int itemCount;

    /**
     * Crea un nuevo objeto Menú
     * @param scanner Instancia del objeto Scanner
     */
    public Menu(Scanner scanner, Console console, String title)
    {
        this.scanner = scanner;
        this.console = console;
        this.items = new ArrayList<>();
        this.title = title;
        this.header = null;
        this.itemCount = 0;
    }

    /**
     * Agrega una opción al menú
     * @param key Clave de la opción
     * @param text Texto de la opción
     * @return Referencia al propio menú
     */
    public Menu addItem(String key, String text)
    {
        items.add(new MenuItem(key.toUpperCase(), text, false));
        return this;
    }

    /**
     * Agrega una lista de valores como opciones en el menú
     * @param <T> Tipo de valor genérico
     * @param items Arreglo de valores
     * @return Referencia al propio menú
     */
    public <T> Menu addItems(T[] items)
    {
        for (var item : items)
        {
            this.items.add(new MenuItem(
                Integer.toString(itemCount++), 
                item.toString(), 
                true));
        }

        return this;
    }

    /**
     * Agrega una línea en blanco para separar visualmente las opciones del menú
     * @return Referencia al propio menú
     */
    public Menu addBlankLine()
    {
        items.add(new MenuItem("", "", false));
        return this;
    }

    /**
     * Borra todos las opciones del menú
     */
    public void clearItems()
    {
        items.clear();
    }

    /**
     * Establece un título para el menú
     * @param title Título
     * @return Referencia al propio menú
     */
    public Menu setTitle(String title)
    {
        this.title = title;
        return this;
    }

    /**
     * Establece una cabecera para las opciones del menú
     * @param header Cabecera
     * @return Referencia al propio menú
     */
    public Menu setHeader(String header)
    {
        this.header = header;
        return this;
    }

    /**
     * Muestra el menú y espera a que el usuario haga una selección
     * @return Cadena de texto que representa la clave de la opción elegida
     */
    public String show()
    {
        return show("");
    }

    /**
     * Muestra el menú y espera a que el usuario haga una selección
     * @param prompt Indicación que es mostrada al usuario
     * @return Cadena de texto que representa la clave de la opción elegida
     */
    public String show(String prompt)
    {
        // Declara la variable que almacena la clave de la opción ingresada
        String key = "";
        // Declara la variable para indicar cuando terminar el bucle
        boolean endLoop = false;

        // Imprime una línea en blanco
        System.out.println();

        // Verifica si se estableció un título para el menú
        if (title != null && title.length() > 0)
        {
            // De ser así, muestra el título seguido de una línea en blanco
            System.out.println(title + "\n");
        }

        // Crea un arreglo para almacenar las líneas del menú
        ArrayList<String> linesToPrintAsRows = new ArrayList<>();
        ArrayList<String> linesToPrintNormally = new ArrayList<>();

        // Bucle que recorre la lista de opciones
        for (int i = 0; i < items.size(); i++) 
        {
            var item = items.get(i);
            String option = String.format("[%s] - %s",
                item.getKey(),
                item.getText());

            // Verifica si el texto de la opción contiene caracteres de 
            // separación
            if (item.isRow() && header != null)
            {
                // De ser así, agrega la opción para imprimir como filas
                linesToPrintAsRows.add(option);
            }
            // Verifica si la clave de la opción no esta en blanco
            else if (!item.key.isBlank())
            {
                linesToPrintNormally.add(option);
            }
        }

        // Verifica si se establecio una cabecera
        if (header != null)
        {
            // Convierte la lista de opciones a arreglo
            String[] array = new String[linesToPrintAsRows.size()];
            linesToPrintAsRows.toArray(array);
            // Imprime la lista como tabla
            console.printAsTable(header, array);
        }
        
        // Imprime el resto de las filas
        for (String string : linesToPrintNormally) 
        {
            System.out.println(string);
        }
    
        // Imprime un salto de línea y establece un punto de referencia para 
        // mostrar la indicación al usuario
        System.out.println("\n\033[s");

        // Bucle que se repite mientras no se elija una opción válida del menú
        do
        {
            // Imprime la indicación al comienzo de la línea
            System.out.printf("\033[u\033[0K%s: ", prompt);

            // Bloque para intentar leer la entrada de usuario
            try
            {
                key = scanner.nextLine().toUpperCase();
            }
            catch (Exception ex)
            {
                // De fallar, vuelve a repetir el ciclo
                continue;
            }

            // Bucle que recorre la lista de opciones hasta encontrar aquella
            // cuya clave coincide con la ingresada por el usuario
            for (var item : items)
            {
                // Verifica si coinciden las claves. No distingue entre
                // mayúsculas/minúsculas
                if (!item.getKey().isBlank() &&
                    item.getKey().equalsIgnoreCase(key))
                {
                    // De ser así, indica que el bucle debe terminar
                    endLoop = true;
                }
            }
        }

        // Se ejecuta hasta que endLoop deje de ser falso
        while (!endLoop);
        // Imprime una línea en blanco
        System.out.println();
        // Retorna la clave de la opción elegida
        return key;
    }
}
