package org.example.main;

import org.example.databases.DatabaseInitializer;
import org.example.views.HourlyCalendarUI;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DatabaseInitializer databaseInitializer = new DatabaseInitializer();
        databaseInitializer.initializeDatabase();
        new HourlyCalendarUI();
    }
}