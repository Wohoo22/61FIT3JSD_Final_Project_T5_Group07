package org.example.views;

import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.controllers.EventController;
import org.example.interfaces.SaveClickListener;
import org.example.models.Event;
import org.example.utils.HolidayChecker;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HourlyCalendarUI extends JFrame implements SaveClickListener {

    private static final int TOTAL_HOURS = 24;
    private final EventController eventController;
    private List<String> weekList;
    private List<Event> events = new ArrayList<>();
    private DefaultTableModel model;
    private JComboBox<String> weekDropdown;
    private JTable table;
    private int currentYear = 2024;
    private JButton prevWeekButton;
    private JButton nextWeekButton;
    private List<Event> displayEvents = new ArrayList<>();

    public HourlyCalendarUI() {
        eventController = new EventController();
        setupFrame();
        setupTopPanel();
        setupTable();
        setupAddEventButton();
        setupMenuBar();
        selectCurrentWeek();
        pack();
        setVisible(true);
        prevWeekButton.doClick();
        nextWeekButton.doClick();
    }


    private void setupFrame() {
        setTitle("Hourly Calendar UI with Custom Event Placement");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 800));
        getContentPane().setBackground(new Color(245, 245, 245));
    }

    private void setupTopPanel() {
        JLabel label = new JLabel("Add, view, and manage your events");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize week list for the current year
        weekList = calculateWeeksForYear(2024);
        weekDropdown = new JComboBox<>(weekList.toArray(new String[0]));
        weekDropdown.addActionListener(e -> updateTableForWeek((String) weekDropdown.getSelectedItem()));
        weekDropdown.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add "Previous Year" and "Next Year" buttons
        prevWeekButton = new JButton("< Previous Week");
        nextWeekButton = new JButton("Next Week >");
        prevWeekButton.setBackground(new Color(70, 130, 180)); // Steel blue color for buttons
        nextWeekButton.setBackground(new Color(70, 130, 180));
        prevWeekButton.setForeground(Color.WHITE);
        nextWeekButton.setForeground(Color.WHITE);
        prevWeekButton.setFocusPainted(false);
        nextWeekButton.setFocusPainted(false);
        prevWeekButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextWeekButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Panel to hold the buttons and the dropdown
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));

        buttonPanel.add(prevWeekButton);
        buttonPanel.add(weekDropdown);
        buttonPanel.add(nextWeekButton);

        prevWeekButton.addActionListener(e -> changeWeek(-1));
        nextWeekButton.addActionListener(e -> changeWeek(1));

        // Set up the top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.add(label, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
    }

    private void changeWeek(int weekIncrement) {
        // Find the current week and total weeks in the current year
        int currentWeekIndex = weekDropdown.getSelectedIndex();
        int totalWeeksInYear = weekList.size(); // Total weeks for the current year

        // Increment or decrement the week
        currentWeekIndex += weekIncrement;

        // If the current week exceeds total weeks, move to the next year
        if (currentWeekIndex >= totalWeeksInYear) {
            currentYear++; // Move to the next year
            weekList = calculateWeeksForYear(currentYear); // Recalculate weeks for the new year
            currentWeekIndex = 0; // Set to the first week of the new year
        }

        // If the current week is less than 0, move to the previous year
        if (currentWeekIndex < 0) {
            currentYear--; // Move to the previous year
            weekList = calculateWeeksForYear(currentYear); // Recalculate weeks for the previous year
            currentWeekIndex = weekList.size() - 1; // Set to the last week of the previous year
        }

        // Update the week dropdown with the new list of weeks
        weekDropdown.setModel(new DefaultComboBoxModel<>(weekList.toArray(new String[0])));
        weekDropdown.setSelectedIndex(currentWeekIndex); // Set the selected week to the new current week

        // Refresh the events for the new week
        updateTableForWeek(weekList.get(currentWeekIndex));
    }


    private void setupTable() {
        String[] columnNames = {"Time", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        model = new DefaultTableModel(columnNames, TOTAL_HOURS);

        table = new JTable(model) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawAllEvents(g);
            }
        };

        table.setRowHeight(60);
        table.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font for table content
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setEnabled(false);
        table.setSelectionBackground(new Color(135, 206, 250)); // Light sky blue selection color
        table.setSelectionForeground(Color.BLACK);

        fillTimeSlots();
        table.setEnabled(false);
        setupEventClickHandler(table);
        table.getTableHeader().setBackground(new Color(176, 196, 222));
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(100, 149, 237)); // Cornflower blue for header background
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16)); // Set font for table header
        header.setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        table.setShowGrid(true);  // Ensures the grid is shown
        table.setGridColor(Color.DARK_GRAY);  // Set a darker color for better visibility
        table.setIntercellSpacing(new Dimension(2, 2));  // Makes the grid lines thicker
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Get the header background color
                Color headerColor = new Color(100, 149, 237); // Cornflower blue, same as the first row

                // Apply the header color to the first column and first row
                if (column == 0) {
                    cellComponent.setBackground(headerColor);
                    cellComponent.setForeground(Color.WHITE); // Set text color to white for contrast
                } else {
                    // Set the default background for other cells
                    cellComponent.setBackground(Color.WHITE); // Or any other default color
                    cellComponent.setForeground(Color.BLACK); // Set default text color
                }

                return cellComponent;
            }
        });

        add(scrollPane, BorderLayout.CENTER);
    }

    private void drawAllEvents(Graphics g) {
        displayEvents.clear();
        for (Event event : events) {
            drawEvent(g, event);
        }
    }

    // Custom method to draw events precisely between hours
    private void drawEvent(Graphics g, Event event) {
        LocalDate currentWeekStart = getCurrentWeekStartDate();
        LocalDate currentWeekEnd = currentWeekStart.plusDays(6);
        LocalDate nextOccurrence = event.getStartDate();

        if (event.isRepeated()) {
            while (!nextOccurrence.isAfter(currentWeekEnd)) {
                if (!nextOccurrence.isBefore(event.getStartDate()) && !nextOccurrence.isAfter(event.getEndDate()) &&
                        !nextOccurrence.isBefore(currentWeekStart) && !nextOccurrence.isAfter(currentWeekEnd)) {
                    drawSingleEventOccurrence(g, event, nextOccurrence);
                }
                nextOccurrence = getNextOccurrence(event, nextOccurrence);
            }
        } else if (isEventInCurrentWeek(event)) {
            if (!event.getStartDate().isBefore(currentWeekStart) && !event.getStartDate().isAfter(currentWeekEnd)) {
                drawSingleEventOccurrence(g, event, event.getStartDate());
            }
        }
    }

    private LocalDate getNextOccurrence(Event event, LocalDate currentDate) {
        switch (event.getRepeatType()) {
            case DAILY:
                return currentDate.plusDays(1);
            case WEEKLY:
                return currentDate.plusWeeks(1);
            case MONTHLY:
                return currentDate.plusMonths(1);
            default:
                return currentDate; // No repeat or unknown repeat type
        }
    }

    private void drawSingleEventOccurrence(Graphics g, Event event, LocalDate eventDate) {
        String eventName = event.getTitle();
        String location = event.getLocation();
        LocalTime startTime = event.getStartTime();
        LocalTime endTime = event.getEndTime();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeRange = String.format("(%s - %s)", startTime.format(timeFormatter), endTime.format(timeFormatter));
        String eventDisplayText = eventName + " " + timeRange + " - " + location;

        int dayColumn = eventDate.getDayOfWeek().getValue() - 1; // Adjust for 0-based index (0 = MON, ..., 6 = SUN)

        double startHourFraction = startTime.getHour() + startTime.getMinute() / 60.0;
        double endHourFraction = endTime.getHour() + endTime.getMinute() / 60.0;

        int startRow = (int) startHourFraction;
        int endRow;
        if (Math.ceil(endHourFraction) - endHourFraction == 0) {
            endRow = (int) Math.ceil(endHourFraction);

        } else {
            endRow = (int) Math.ceil(endHourFraction) - 1;

        }

        Rectangle startCell = table.getCellRect(startRow, dayColumn + 1, true);
        Rectangle endCell = table.getCellRect(endRow, dayColumn + 1, true);

        int startY = startCell.y + (int) ((startHourFraction % 1) * startCell.height);
        int endY = endCell.y + (int) ((endHourFraction % 1) * endCell.height);

        if (endY <= startY) {
            endY = startY + startCell.height;
        }

        // Check if the event date is a holiday
        if (HolidayChecker.isHoliday(eventDate)) {
            g.setColor(new Color(255, 102, 102)); // Use a distinct color for holidays (e.g., light red)
        } else {
            g.setColor(event.getColor().getAwtColor()); // Use the original event color
        }

        g.fillRect(startCell.x + 1, startY, startCell.width - 2, endY - startY);

        FontMetrics metrics = g.getFontMetrics();
        int columnWidth = startCell.width - 10;
        List<String> wrappedText = wrapTextToFit(eventDisplayText, metrics, columnWidth);

        Color contrastingColor = getContrastingColor(event.getColor().getAwtColor());
        g.setColor(contrastingColor);
        int lineHeight = metrics.getHeight();
        int textY = startY + 15;

        for (String line : wrappedText) {
            g.drawString(line, startCell.x + 5, textY);
            textY += lineHeight;
            if (textY >= endY) break;
        }
        if (!displayEvents.contains(event)) {
            displayEvents.add(event);
        }
    }


    private Color getContrastingColor(Color color) {
        // Calculate luminance of the color
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private LocalDate getCurrentWeekStartDate() {
        String selectedWeek = (String) weekDropdown.getSelectedItem();
        String[] weekRange = selectedWeek.split(": ")[1].split(" to ");
        return LocalDate.parse(weekRange[0], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }


    private boolean isEventInCurrentWeek(Event event) {
        String selectedWeek = (String) weekDropdown.getSelectedItem();
        String[] weekRange = selectedWeek.split(": ")[1].split(" to ");
        LocalDate startDate = LocalDate.parse(weekRange[0], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endDate = LocalDate.parse(weekRange[1], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return !event.getStartDate().isBefore(startDate) && !event.getStartDate().isAfter(endDate);
    }

    // Helper method to wrap text into multiple lines if necessary
    private List<String> wrapTextToFit(String text, FontMetrics metrics, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : text.split(" ")) {
            // If the current line plus the next word fits within the width, add it to the line
            if (metrics.stringWidth(currentLine + word) <= maxWidth) {
                currentLine.append(word).append(" ");
            } else {
                // If it doesn't fit, add the current line to the list and start a new line
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder(word).append(" ");
            }
        }

        // Add any remaining text as the last line
        if (!currentLine.toString().isEmpty()) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }

    private void fillTimeSlots() {
        for (int i = 0; i < TOTAL_HOURS; i++) {
            model.setValueAt(getTimeSlot(i), i, 0);
        }
    }

    private void setupEventClickHandler(JTable table) {
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                if (row != -1 && column > 0) { // Ensure clicking on a valid day column, not the "Time" column
                    // Adjust time calculation to 1-hour intervals if needed
                    LocalTime clickedTime = LocalTime.of(row, 0); // Use 1-hour intervals for better precision

                    // Adjust dayOfWeek if your Event objects use a specific day format
                    int dayOfWeek = column; // Assuming the column index correctly maps to day-of-week

                    // Call the event display method
                    checkAndDisplayEvent(dayOfWeek, clickedTime, row);
                }
            }
        });
    }


    private void checkAndDisplayEvent(int dayOfWeek, LocalTime clickedTime, int clickedRow) {
        for (Event event : events) {
            // Determine if the event occurs on the clicked day
            boolean eventOccursOnDay = false;

            if (event.isRepeated()) {
                LocalDate eventStartDate = event.getStartDate();
                LocalDate eventEndDate = event.getEndDate();
                LocalDate currentWeekStart = getCurrentWeekStartDate();
                LocalDate clickedDate = currentWeekStart.plusDays(dayOfWeek - 1); // Get the exact date within the current week based on the clicked day.

                switch (event.getRepeatType()) {
                    case DAILY:
                        // Daily event occurs if clickedDate is between startDate and endDate (inclusive)
                        if (!clickedDate.isBefore(eventStartDate) && !clickedDate.isAfter(eventEndDate)) {
                            eventOccursOnDay = true;
                        }
                        break;
                    case WEEKLY:
                        // A weekly event occurs on the same day of the week as the original start date, within start and end date range
                        if (event.getStartDate().getDayOfWeek().getValue() == dayOfWeek &&
                                !clickedDate.isBefore(eventStartDate) && !clickedDate.isAfter(eventEndDate)) {
                            eventOccursOnDay = true;
                        }
                        break;
                    case MONTHLY:
                        // A monthly event occurs if the day of the month matches, and within the start and end date range
                        if (event.getStartDate().getDayOfMonth() == clickedDate.getDayOfMonth() &&
                                !clickedDate.isBefore(eventStartDate) && !clickedDate.isAfter(eventEndDate)) {
                            eventOccursOnDay = true;
                        }
                        break;
                }
            } else {
                // If the event is not repeated, just check if it is on the same day as the clicked day
                if (event.getStartDate().getDayOfWeek().getValue() == dayOfWeek &&
                        !event.getStartDate().isBefore(getCurrentWeekStartDate()) &&
                        !event.getStartDate().isAfter(getCurrentWeekStartDate().plusDays(6))) {
                    eventOccursOnDay = true;
                }
            }

            if (eventOccursOnDay) {
                // Calculate the row range where the event is displayed
                double startHourFraction = event.getStartTime().getHour() + event.getStartTime().getMinute() / 60.0;
                double endHourFraction = event.getEndTime().getHour() + event.getEndTime().getMinute() / 60.0;

                int startRow = (int) (startHourFraction);
                int endRow = (int) Math.ceil(endHourFraction);

                // Check if the clicked row falls within the event's time range
                if (clickedRow >= startRow && clickedRow < endRow) {
                    // Display event details in a new frame
                    EventDetailFrame eventDetailFrame = new EventDetailFrame(event, eventController, this);
                    eventDetailFrame.setVisible(true);
                    System.out.println("Event found: " + event.getTitle());
                    break; // Stop after finding the first matching event
                }
            }
        }
    }


    private void setupAddEventButton() {
        JButton addEventButton = new JButton("+ Add Event");
        addEventButton.addActionListener(e -> {
            AddEventFrame addEventFrame = new AddEventFrame(eventController, this);
            addEventFrame.setVisible(true);
        });

        JButton exportToPngButton = new JButton("Export to PDF");
        exportToPngButton.addActionListener(e -> {
            exportEventsToPdf();
        });

        // Style the buttons
        addEventButton.setBackground(new Color(60, 179, 113)); // Medium sea green
        exportToPngButton.setBackground(new Color(255, 140, 0)); // Dark orange
        addEventButton.setForeground(Color.WHITE);
        exportToPngButton.setForeground(Color.WHITE);
        addEventButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportToPngButton.setFont(new Font("Arial", Font.BOLD, 14));
        addEventButton.setFocusPainted(false);
        exportToPngButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(exportToPngButton);
        buttonPanel.add(addEventButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newEventItem = new JMenuItem("New Event");
        JMenuItem exitItem = new JMenuItem("Exit");

        newEventItem.addActionListener(e -> new AddEventFrame(eventController, this).setVisible(true));
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newEventItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private String getTimeSlot(int index) {
        LocalTime time = LocalTime.of(index, 0);
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private List<String> calculateWeeksForYear(int year) {
        List<String> weeks = new ArrayList<>();
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        int weekNumber = 1;

        while (startOfYear.getYear() == year) {
            LocalDate endOfWeek = startOfYear.plusDays(6);
            String weekRange = String.format("Week %d: %s to %s",
                    weekNumber,
                    startOfYear.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    endOfWeek.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            weeks.add(weekRange);
            weekNumber++;
            startOfYear = startOfYear.plusWeeks(1);
        }
        return weeks;
    }

    private void selectCurrentWeek() {
        LocalDate currentDate = LocalDate.now();
        int currentWeekIndex = findWeekIndex(currentDate);
        weekDropdown.setSelectedIndex(currentWeekIndex);
    }

    private int findWeekIndex(LocalDate currentDate) {
        for (int i = 0; i < weekList.size(); i++) {
            String[] weekRange = weekList.get(i).split(": ")[1].split(" to ");
            LocalDate startDate = LocalDate.parse(weekRange[0], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate endDate = LocalDate.parse(weekRange[1], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            if (!currentDate.isBefore(startDate) && !currentDate.isAfter(endDate)) {
                return i; // Return the index of the current week
            }
        }
        return 0; // Default to the first week if no match is found
    }

    private void updateTableForWeek(String selectedWeek) {
        int weekNumber = extractWeekNumber(selectedWeek);
        events = eventController.getAllEventsForWeek(weekNumber); // Fetch the events for the selected week
        System.out.println("Events for week " + weekNumber + ": " + events);
        repaint(); // Redraw the table with new events
        scrollToEarliestEvent();
    }

    private int extractWeekNumber(String selectedWeek) {
        String[] parts = selectedWeek.split(" ");
        int weekIndex = Integer.parseInt(parts[1].replace(":", ""));
        return weekIndex; // Assuming format is "Week X: ..."
    }

    private void exportEventsToPdf() {
        String selectedWeek = (String) weekDropdown.getSelectedItem();
        String sanitizedWeekName = selectedWeek.replaceAll("[^a-zA-Z0-9]", "_"); // Sanitize to avoid invalid characters in file names
        String filePath = sanitizedWeekName + "_events.pdf";

        // Create a Document with landscape orientation
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add title
            document.add(new Paragraph("Events for " + selectedWeek, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(new Paragraph(" ")); // Add an empty line for spacing

            // Create a table with 9 columns
            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100); // Set table width to 100% of the page
            table.setSpacingBefore(10f); // Space before table
            table.setSpacingAfter(10f);  // Space after table

            // Set column widths (you can adjust these values as needed)
            float[] columnWidths = {2f, 3f, 2f, 1.5f, 2f, 2f, 2f, 1.5f, 1.5f};
            table.setWidths(columnWidths);

            // Add table headers
            table.addCell("Title");
            table.addCell("Description");
            table.addCell("Location");
            table.addCell("Is Repeated");
            table.addCell("Repeat Type");
            table.addCell("Start Date");
            table.addCell("End Date");
            table.addCell("Start Time");
            table.addCell("End Time");

            // Add event details
            for (Event event : events) {
                table.addCell(event.getTitle());
                table.addCell(event.getDescription() != null ? event.getDescription() : "");
                table.addCell(event.getLocation() != null ? event.getLocation() : "");
                table.addCell(event.isRepeated() ? "Yes" : "No");
                table.addCell(event.getRepeatType() != null ? event.getRepeatType().name() : "");
                table.addCell(event.getStartDate() != null ? event.getStartDate().toString() : "");
                table.addCell(event.getEndDate() != null ? event.getEndDate().toString() : "");
                table.addCell(event.getStartTime() != null ? event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                table.addCell(event.getEndTime() != null ? event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "");
            }

            document.add(table); // Add the table to the document
            document.close(); // Close the document

            JOptionPane.showMessageDialog(this, "Events exported to " + filePath + " successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting events: " + e.getMessage(), "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }


    @Override
    public void reload() {
        updateTableForWeek((String) weekDropdown.getSelectedItem());
    }

    private void scrollToEarliestEvent() {

        if (displayEvents.isEmpty()) return; // No events to scroll to

        // Find the event that starts the earliest in the day
        Event earliestEvent = displayEvents.get(0);
        for (Event event : displayEvents) {
            if (event.getStartTime().isBefore(earliestEvent.getStartTime())) {
                earliestEvent = event; // Update to the event with the earliest start time
            }
        }

        // Calculate the row index for the earliest event's start time based on the 24-hour format
        double startHourFraction = earliestEvent.getStartTime().getHour() + earliestEvent.getStartTime().getMinute() / 60.0;
        int earliestRow = (int) startHourFraction; // Directly use the hour as the row index

        // Ensure the row index is within the bounds of the table
        earliestRow = Math.max(earliestRow, 0);

        // Scroll to the earliest row
        Rectangle rect = table.getCellRect(earliestRow, 0, true);
        table.scrollRectToVisible(table.getCellRect(24, 0, true));
        table.scrollRectToVisible(rect);
    }

}