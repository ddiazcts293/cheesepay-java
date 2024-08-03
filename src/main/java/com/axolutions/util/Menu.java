package com.axolutions.util;

import java.util.ArrayList;
import java.util.Scanner;

public class Menu
{
    private class MenuItem
    {
        private String key;
        private String text;

        public MenuItem(String key, String text)
        {
            this.key = key;
            this.text = text;
        }

        public String getKey()
        {
            return key;
        }

        public String getText()
        {
            return text;
        }
    }

    private Scanner scanner;
    private ArrayList<MenuItem> items;
    private String title;
    private int itemCount;

    public Menu(Scanner scanner)
    {
        this.scanner = scanner;
        this.items = new ArrayList<>();
        this.title = "";
        this.itemCount = 0;
    }

    public Menu(Scanner scanner, String title)
    {
        this(scanner);
        this.title = title;
    }

    public Menu addItem(String key, String text)
    {
        items.add(new MenuItem(key, text));
        return this;
    }

    public <T> Menu addItems(T[] items)
    {
        for (var item : items)
        {
            addItem(Integer.toString(itemCount++), item.toString());
        }

        return this;
    }

    public Menu addBlankLine()
    {
        items.add(new MenuItem("", ""));
        return this;
    }

    public void clearItems()
    {
        items.clear();
    }

    public Menu setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public String show()
    {
        return show("");
    }

    public String show(String prompt)
    {
        String key = "";
        boolean exitFromLoop = false;

        if (title != null && title.length() > 0)
        {
            System.out.println(title);
        }

        for (var item : items)
        {
            if (!item.key.isBlank())
            {
                System.out.printf("[%s] - %s\n\033[s",
                    item.getKey(),
                    item.getText());
            }
            else
            {
                System.out.println();
            }
        }

        do
        {
            System.out.printf("\033[u\033[0K%s: ", prompt);

            try
            {
                key = scanner.nextLine();
            }
            catch (Exception ex)
            {
                continue;
            }

            for (var item : items)
            {
                if (!item.getKey().isBlank() && item.getKey().equalsIgnoreCase(key))
                {
                    exitFromLoop = true;
                }
            }
        }
        while (!exitFromLoop);

        System.out.println();
        return key;
    }
}
