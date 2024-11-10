package org.example.views;

import com.toedter.calendar.JDateChooser;
import org.example.controllers.EventController;
import org.example.interfaces.SaveClickListener;
import org.example.models.Colors;
import org.example.models.Event;
import org.example.models.RepeatType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class AddEventFrame extends JFrame implements PropertyChangeListener {

    private final EventController eventController;
    private final SaveClickListener saveClickListener;
    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField locationField;
    private JComboBox<Colors> colorComboBox;
    private JComboBox<RepeatType> repeatTypeComboBox;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JDateChooser happenDateChooser;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JCheckBox repeatedCheckBox;
    private JLabel selectedStartDateLabel;
    private JLabel selectedEndDateLabel;
    private JLabel selectedHappenDateLabel;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate happenDate;
    private Event createdEvent;

    public AddEventFrame(EventController eventController, SaveClickListener saveClickListener) {
        super("Add New Event");
        this.eventController = eventController;
        this.saveClickListener = saveClickListener;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 500);

        // Title label
        JLabel titleLabel = new JLabel("Add New Event");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        setInitialVisibility();

        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = createGridBagConstraints();

        addTitleField(mainPanel, gbc);
        addDescriptionField(mainPanel, gbc);
        addLocationField(mainPanel, gbc);
        addColorComboBox(mainPanel, gbc);
        addRepeatedCheckBox(mainPanel, gbc);
        addRepeatTypeComboBox(mainPanel, gbc);
        addHappenDateChooser(mainPanel, gbc);
        addStartDateChooser(mainPanel, gbc);
        addEndDateChooser(mainPanel, gbc);
        addStartTimeField(mainPanel, gbc);
        addEndTimeField(mainPanel, gbc);

        return mainPanel;
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        return gbc;
    }

    private void addTitleField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx++;
        titleField = new JTextField();
        panel.add(titleField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addLocationField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx++;
        locationField = new JTextField();
        panel.add(locationField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addDescriptionField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx++;
        descriptionField = new JTextField();
        panel.add(descriptionField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addColorComboBox(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Color:"), gbc);
        gbc.gridx++;
        colorComboBox = new JComboBox<>(Colors.values());
        panel.add(colorComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addRepeatedCheckBox(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Is Repeated?"), gbc);
        gbc.gridx++;
        repeatedCheckBox = new JCheckBox();
        repeatedCheckBox.addItemListener(e -> toggleDateFields(e.getStateChange() == ItemEvent.SELECTED));
        panel.add(repeatedCheckBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addRepeatTypeComboBox(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Repeat Type:"), gbc);
        gbc.gridx++;
        repeatTypeComboBox = new JComboBox<>(RepeatType.values());
        panel.add(repeatTypeComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addHappenDateChooser(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Happen Date:"), gbc);
        gbc.gridx++;
        happenDateChooser = new JDateChooser();
        happenDateChooser.setDateFormatString("yyyy-MM-dd");
        happenDateChooser.getDateEditor().addPropertyChangeListener("date", this);
        selectedHappenDateLabel = new JLabel("Selected Happen Date: None");
        panel.add(happenDateChooser, gbc);
        gbc.gridy++;
        gbc.gridx = 1;
        panel.add(selectedHappenDateLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addStartDateChooser(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx++;
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        startDateChooser.getDateEditor().addPropertyChangeListener("date", this);
        selectedStartDateLabel = new JLabel("Selected Start Date: None");
        panel.add(startDateChooser, gbc);
        gbc.gridy++;
        gbc.gridx = 1;
        panel.add(selectedStartDateLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addEndDateChooser(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("End Date:"), gbc);
        gbc.gridx++;
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        endDateChooser.getDateEditor().addPropertyChangeListener("date", this);
        selectedEndDateLabel = new JLabel("Selected End Date: None");
        panel.add(endDateChooser, gbc);
        gbc.gridy++;
        gbc.gridx = 1;
        panel.add(selectedEndDateLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }


    private void addStartTimeField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Start Time (HH:MM):"), gbc);
        gbc.gridx++;
        startTimeField = new JTextField();
        panel.add(startTimeField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addEndTimeField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("End Time (HH:MM):"), gbc);
        gbc.gridx++;
        endTimeField = new JTextField();
        panel.add(endTimeField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveEvent());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void setInitialVisibility() {
        toggleDateFields(false);
    }

    private void toggleDateFields(boolean isRepeated) {
        startDateChooser.setVisible(isRepeated);
        endDateChooser.setVisible(isRepeated);
        selectedStartDateLabel.setVisible(isRepeated);
        selectedEndDateLabel.setVisible(isRepeated);
        happenDateChooser.setVisible(!isRepeated);
        selectedHappenDateLabel.setVisible(!isRepeated);
        repeatTypeComboBox.setVisible(isRepeated);
        revalidate();
        repaint();
    }

    private void saveEvent() {
        try {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String location = locationField.getText();
            Colors color = (Colors) colorComboBox.getSelectedItem();
            boolean isRepeated = repeatedCheckBox.isSelected();
            RepeatType repeatType = isRepeated ? (RepeatType) repeatTypeComboBox.getSelectedItem() : null;

            LocalTime startTime = LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            if (startTime.isAfter(endTime)) {
                throw new Exception("Start time cannot be after end time.");
            }
            if (isRepeated) {
                if (startDateChooser.getDate() == null || endDateChooser.getDate() == null) {
                    throw new Exception("Start or end date not selected.");
                }

                if (startDate.isAfter(endDate)) {
                    throw new Exception("Start date cannot be after end date.");
                }
                if (repeatType == RepeatType.NONE) {
                    throw new Exception("Repeat type not selected.");
                }
                startDate = startDateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                endDate = endDateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                createdEvent = new Event(0, title, description, location, color, isRepeated, repeatType, startDate, endDate, startTime, endTime);
            } else {
                if (happenDateChooser.getDate() == null) {
                    throw new Exception("Happen date not selected.");
                }
                happenDate = happenDateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                createdEvent = new Event(0, title, description, location, color, isRepeated, RepeatType.NONE, happenDate, happenDate, startTime, endTime);
            }

            for (Event existingEvent : eventController.getAllEvents()) {
                if (isEventConflict(existingEvent, createdEvent)) {
                    JOptionPane.showMessageDialog(this, "There is a conflict with an existing event. Please choose a different time or date.", "Conflict", JOptionPane.WARNING_MESSAGE);
                    return; // Stop the saving process if a conflict is found
                }
            }

            eventController.addNewEvent(createdEvent);
            saveClickListener.reload();
            setVisible(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to check if there's a conflict between two events
    private boolean isEventConflict(Event existingEvent, Event newEvent) {
        // Check if the events have overlapping dates
        LocalDate existingStartDate = existingEvent.getStartDate();
        LocalDate existingEndDate = existingEvent.getEndDate();
        LocalTime existingStartTime = existingEvent.getStartTime();
        LocalTime existingEndTime = existingEvent.getEndTime();

        if (newEvent.isRepeated()) {
            LocalDate newStartDate = newEvent.getStartDate();
            LocalDate newEndDate = newEvent.getEndDate();

            LocalDate currentOccurrence = newStartDate;

            while (!currentOccurrence.isAfter(newEndDate)) {
                if (existingEvent.isRepeated()) {
                    // Compare each occurrence of the new event with the existing event occurrences
                    LocalDate existingOccurrence = existingStartDate;

                    while (!existingOccurrence.isAfter(existingEndDate)) {
                        if (isDatesAndTimesOverlap(existingOccurrence, existingStartTime, existingEndTime,
                                currentOccurrence, newEvent.getStartTime(), newEvent.getEndTime())) {
                            return true; // Conflict found
                        }
                        existingOccurrence = getNextOccurrence(existingEvent, existingOccurrence);
                    }
                } else {
                    if (isDatesAndTimesOverlap(existingStartDate, existingStartTime, existingEndTime,
                            currentOccurrence, newEvent.getStartTime(), newEvent.getEndTime())) {
                        return true; // Conflict found
                    }
                }

                currentOccurrence = getNextOccurrence(newEvent, currentOccurrence);
            }
        } else {
            if (existingEvent.isRepeated()) {
                LocalDate existingOccurrence = existingStartDate;

                while (!existingOccurrence.isAfter(existingEndDate)) {
                    if (isDatesAndTimesOverlap(existingOccurrence, existingStartTime, existingEndTime,
                            newEvent.getStartDate(), newEvent.getStartTime(), newEvent.getEndTime())) {
                        return true; // Conflict found
                    }
                    existingOccurrence = getNextOccurrence(existingEvent, existingOccurrence);
                }
            } else {
                return isDatesAndTimesOverlap(existingStartDate, existingStartTime, existingEndTime,
                        newEvent.getStartDate(), newEvent.getStartTime(), newEvent.getEndTime()); // Conflict found
            }
        }

        return false; // No conflict found
    }

    private boolean isDatesAndTimesOverlap(LocalDate date1, LocalTime startTime1, LocalTime endTime1,
                                           LocalDate date2, LocalTime startTime2, LocalTime endTime2) {
        if (date1.equals(date2)) {
            return !(endTime1.isBefore(startTime2) || startTime1.isAfter(endTime2));
        }
        return false;
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


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("date".equals(propertyName) && evt.getNewValue() instanceof Date) {
            Date selectedDate = (Date) evt.getNewValue();
            LocalDate localDate = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (evt.getSource() == startDateChooser.getDateEditor()) {
                selectedStartDateLabel.setText("Selected Start Date: " + formatter.format(localDate));
                startDate = localDate;
            } else if (evt.getSource() == endDateChooser.getDateEditor()) {
                selectedEndDateLabel.setText("Selected End Date: " + formatter.format(localDate));
                endDate = localDate;
            } else if (evt.getSource() == happenDateChooser.getDateEditor()) {
                selectedHappenDateLabel.setText("Selected Happen Date: " + formatter.format(localDate));
                happenDate = localDate;
            }
        }
    }

}
