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

    public Menu(Scanner scanner)
    {
        this.scanner = scanner;
        this.items = new ArrayList<>();
    }

    public Menu AddItem(String key, String text)
    {
        items.add(new MenuItem(key, text));
        return this;
    }

    public void clear()
    {
        items.clear();
    }

    public String show()
    {
        return show("");
    }

    public String show(String prompt)
    {
        String key = "";
        boolean exitFromLoop = false;

        System.out.println();
        items.forEach((item) ->
            System.out.printf("[%s] - %s\n\033[s",
                item.getKey(),
                item.getText()));

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
                if (item.getKey().compareTo(key) == 0)
                {
                    exitFromLoop = true;
                }
            }
        }
        while (!exitFromLoop);

        return key;
    }
}
