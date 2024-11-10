package org.example.databases;

import org.example.models.Colors;
import org.example.models.Event;
import org.example.models.RepeatType;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventDao {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Stebenc0912@))@"; // Replace with your DB password
    private static final String DB_NAME = "events_scheduler";
    private Connection connection;

    public EventDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewEvent(Event event) {
        try {
            String query = "INSERT INTO Event (title, description, location, color, isRepeated, repeatType,startDate, endDate, startTime, endTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, event.getTitle());
                ps.setString(2, event.getDescription());
                ps.setString(3, event.getLocation());
                ps.setString(4, event.getColor().name());
                ps.setBoolean(5, event.isRepeated());
                ps.setString(6, event.getRepeatType().name());
                ps.setDate(7, Date.valueOf(event.getStartDate()));
                ps.setDate(8, Date.valueOf(event.getEndDate()));
                ps.setTime(9, Time.valueOf(event.getStartTime()));
                ps.setTime(10, Time.valueOf(event.getEndTime()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event) {
        try {
            String query = "UPDATE Event SET title = ?, description = ?, location = ?, color = ?, isRepeated = ?, repeatType = ?,startDate = ?, endDate = ?, startTime = ?, endTime = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, event.getTitle());
                ps.setString(2, event.getDescription());
                ps.setString(3, event.getLocation());
                ps.setString(4, event.getColor().name());
                ps.setBoolean(5, event.isRepeated());
                ps.setString(6, event.getRepeatType().name());
                ps.setDate(7, Date.valueOf(event.getStartDate()));
                ps.setDate(8, Date.valueOf(event.getEndDate()));
                ps.setTime(9, Time.valueOf(event.getStartTime()));
                ps.setTime(10, Time.valueOf(event.getEndTime()));
                ps.setInt(11, event.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(int id) {
        try {
            String query = "DELETE FROM Event WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Event> getAllEventsForWeek(int currentWeek) {
        List<Event> eventList = new ArrayList<>();

        try {
            // Calculate the start and end date of the week
            LocalDate today = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            LocalDate startOfWeek = today.with(weekFields.weekOfYear(), currentWeek)
                    .with(weekFields.dayOfWeek(), 1);
            LocalDate endOfWeek = startOfWeek.plusDays(7);
            System.out.println(startOfWeek);
            String query = "SELECT * FROM Event WHERE (startDate >= ? AND endDate <= ?) OR isRepeated = TRUE";

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setDate(1, Date.valueOf(startOfWeek));
                ps.setDate(2, Date.valueOf(endOfWeek));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Event event = new Event(
                                rs.getInt("id"),
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getString("location"),
                                Colors.valueOf(rs.getString("color")),
                                rs.getBoolean("isRepeated"),
                                RepeatType.valueOf(rs.getString("repeatType")),
                                rs.getDate("startDate").toLocalDate(),
                                rs.getDate("endDate").toLocalDate(),
                                rs.getTime("startTime").toLocalTime(),
                                rs.getTime("endTime").toLocalTime()
                        );
                        eventList.add(event);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventList;
    }

    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        try {
            String query = "SELECT * FROM Event";
            try (PreparedStatement ps = connection.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("location"),
                            Colors.valueOf(rs.getString("color")),
                            rs.getBoolean("isRepeated"),
                            RepeatType.valueOf(rs.getString("repeatType")),
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("endDate").toLocalDate(),
                            rs.getTime("startTime").toLocalTime(),
                            rs.getTime("endTime").toLocalTime()
                    );
                    eventList.add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventList;
    }
}
