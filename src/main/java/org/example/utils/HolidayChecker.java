package org.example.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayChecker {

    // List of holidays (you can customize this list with your holiday dates)
    private static final List<LocalDate> holidays = new ArrayList<>();

    static {
        holidays.add(LocalDate.of(2024, 1, 1));  // New Year's Day
        holidays.add(LocalDate.of(2024, 4, 30)); // Reunification Day
        holidays.add(LocalDate.of(2024, 5, 1));  // International Labor Day
        holidays.add(LocalDate.of(2024, 9, 2));  // National Day
        holidays.add(LocalDate.of(2024, 11, 20)); // Vietnamese Teachers' Day
        holidays.add(LocalDate.of(2024, 6, 1));  // International Children's Day
    }

    // Function to check if a date is a holiday
    public static boolean isHoliday(LocalDate date) {
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // Check if the day and month match any holiday in the list
        for (LocalDate holiday : holidays) {
            if (holiday.getMonthValue() == month && holiday.getDayOfMonth() == day) {
                return true;
            }
        }
        return false;
    }
}
