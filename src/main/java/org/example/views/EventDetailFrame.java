package org.example.views;

import org.example.controllers.EventController;
import org.example.interfaces.SaveClickListener;
import org.example.models.Colors;
import org.example.models.Event;
import org.example.models.RepeatType;
import com.toedter.calendar.JDateChooser;

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

public class EventDetailFrame extends JFrame implements PropertyChangeListener {

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
    private JButton saveButton;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate happenDate;
    private final Event event;
    private final EventController eventController;
    private final SaveClickListener saveClickListener;

    public EventDetailFrame(Event event, EventController eventController, SaveClickListener saveClickListener) {
        super("Event Details");
        this.event = event;
        this.eventController = eventController;
        this.saveClickListener = saveClickListener;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 500);

        JLabel titleLabel = new JLabel("Event Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        setInitialVisibility();
        populateFields();

        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = createGridBagConstraints();

        addTitleField(mainPanel, gbc);
        addDescriptionField(mainPanel, gbc);
        addLocationField(mainPanel,gbc);
        addColorComboBox(mainPanel, gbc);
        addRepeatedCheckBox(mainPanel, gbc);
        addRepeatTypeComboBox(mainPanel, gbc);
        addHappenDateChooser(mainPanel, gbc);
        addStartDateChooser(mainPanel, gbc);
        addEndDateChooser(mainPanel, gbc);
        addStartTimeField(mainPanel, gbc);
        addEndTimeField(mainPanel, gbc);

        happenDateChooser.getDateEditor().addPropertyChangeListener("date", this);
        startDateChooser.getDateEditor().addPropertyChangeListener("date", this);
        endDateChooser.getDateEditor().addPropertyChangeListener("date", this);

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
        titleField.setEditable(false);
        panel.add(titleField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addDescriptionField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx++;
        descriptionField = new JTextField();
        descriptionField.setEditable(false);
        panel.add(descriptionField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addLocationField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx++;
        locationField = new JTextField();
        locationField.setEditable(false);
        panel.add(locationField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addColorComboBox(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Color:"), gbc);
        gbc.gridx++;
        colorComboBox = new JComboBox<>(Colors.values());
        colorComboBox.setEnabled(false);
        panel.add(colorComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addRepeatedCheckBox(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Is Repeated?"), gbc);
        gbc.gridx++;
        repeatedCheckBox = new JCheckBox();
        repeatedCheckBox.addItemListener(e -> toggleDateFields(e.getStateChange() == ItemEvent.SELECTED));
        repeatedCheckBox.setEnabled(false);
        panel.add(repeatedCheckBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addRepeatTypeComboBox(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Repeat Type:"), gbc);
        gbc.gridx++;
        repeatTypeComboBox = new JComboBox<>(RepeatType.values());
        repeatTypeComboBox.setEnabled(false);
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
        happenDateChooser.setEnabled(false);
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
        startDateChooser.setEnabled(false);
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
        endDateChooser.setEnabled(false);
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
        startTimeField.setEditable(false);
        panel.add(startTimeField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addEndTimeField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("End Time (HH:MM):"), gbc);
        gbc.gridx++;
        endTimeField = new JTextField();
        endTimeField.setEditable(false);
        panel.add(endTimeField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        saveButton = new JButton("Save");
        saveButton.setEnabled(false); // Initially disabled
        saveButton.addActionListener(e -> saveEvent());
        buttonPanel.add(saveButton);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> enableEditing());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteEvent());
        buttonPanel.add(deleteButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private void saveEvent() {
        try {
            // Validate and parse start and end times
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parsedStartTime = LocalTime.parse(startTimeField.getText(), timeFormatter);
            LocalTime parsedEndTime = LocalTime.parse(endTimeField.getText(), timeFormatter);

            // Validate that start and end times are correct
            if (parsedStartTime.isAfter(parsedEndTime)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time.", "Invalid Time", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if repeated event and set dates accordingly
            if (repeatedCheckBox.isSelected()) {
                if (startDate == null || endDate == null) {
                    JOptionPane.showMessageDialog(this, "Start and End Dates must be selected for repeated events.", "Missing Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (startDate.isAfter(endDate)) {
                    JOptionPane.showMessageDialog(this, "End date must be after start date.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                if (happenDate == null) {
                    JOptionPane.showMessageDialog(this, "Happen Date must be selected for non-repeated events.", "Missing Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Set event fields
            event.setTitle(titleField.getText().trim());
            event.setDescription(descriptionField.getText().trim());
            event.setLocation(locationField.getText().trim());
            event.setColor((Colors) colorComboBox.getSelectedItem());
            event.setRepeated(repeatedCheckBox.isSelected());
            event.setRepeatType((RepeatType) repeatTypeComboBox.getSelectedItem());
            if (repeatedCheckBox.isSelected()) {
                if (startDateChooser.getDate() == null || endDateChooser.getDate() == null) {
                    throw new Exception("Start or end date not selected.");
                }
                if ((RepeatType) repeatTypeComboBox.getSelectedItem() == RepeatType.NONE) {
                    throw new Exception("Repeat type not selected.");
                }
                event.setStartDate(startDate);
                event.setEndDate(endDate);
            } else {
                happenDate = happenDateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                event.setStartDate(happenDate);
                event.setEndDate(happenDate);
            }
            event.setStartTime(parsedStartTime);
            event.setEndTime(parsedEndTime);
            for (Event existingEvent : eventController.getAllEvents()) {
                if (isEventConflict(existingEvent, event)) {
                    JOptionPane.showMessageDialog(this, "There is a conflict with an existing event. Please choose a different time or date.", "Conflict", JOptionPane.WARNING_MESSAGE);
                    return; // Stop the saving process if a conflict is found
                }
            }
            // Save or update the event
            eventController.updateEvent(event);
            saveClickListener.reload();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while saving the event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Method to check if there's a conflict between two events
    private boolean isEventConflict(Event existingEvent, Event newEvent) {
        if (existingEvent.getId() == newEvent.getId()) {
            return false;
        }

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
                if (isDatesAndTimesOverlap(existingStartDate, existingStartTime, existingEndTime,
                        newEvent.getStartDate(), newEvent.getStartTime(), newEvent.getEndTime())) {
                    return true; // Conflict found
                }
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


    private void setInitialVisibility() {
        toggleDateFields(event.isRepeated());
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

    private void populateFields() {
        titleField.setText(event.getTitle());
        descriptionField.setText(event.getDescription());
        locationField.setText(event.getLocation());
        colorComboBox.setSelectedItem(event.getColor());
        repeatedCheckBox.setSelected(event.isRepeated());
        repeatTypeComboBox.setSelectedItem(event.getRepeatType());
        startDate = event.getStartDate();
        endDate = event.getEndDate();
        happenDate = event.getStartDate();
        if (startDate != null) startDateChooser.setDate(java.sql.Date.valueOf(startDate));
        if (endDate != null) endDateChooser.setDate(java.sql.Date.valueOf(endDate));
        if (happenDate != null) happenDateChooser.setDate(java.sql.Date.valueOf(happenDate));
        startTimeField.setText(event.getStartTime().toString());
        endTimeField.setText(event.getEndTime().toString());
    }

    private void enableEditing() {
        titleField.setEditable(true);
        descriptionField.setEditable(true);
        locationField.setEditable(true);
        colorComboBox.setEnabled(true);
        repeatTypeComboBox.setEnabled(true);
        repeatedCheckBox.setEnabled(true);
        happenDateChooser.setEnabled(true);
        startDateChooser.setEnabled(true);
        endDateChooser.setEnabled(true);
        startTimeField.setEditable(true);
        endTimeField.setEditable(true);
        saveButton.setEnabled(true);
    }

    private void deleteEvent() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this event?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            eventController.deleteEvent(event.getId());
            saveClickListener.reload();
            dispose();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Date selectedDate = (Date) evt.getNewValue();
        if (selectedDate != null) {
            if (evt.getSource() == startDateChooser.getDateEditor()) {
                startDate = convertToLocalDate(selectedDate);
                selectedStartDateLabel.setText("Selected Start Date: " + startDate);
            } else if (evt.getSource() == endDateChooser.getDateEditor()) {
                endDate = convertToLocalDate(selectedDate);
                selectedEndDateLabel.setText("Selected End Date: " + endDate);
            } else if (evt.getSource() == happenDateChooser.getDateEditor()) {
                happenDate = convertToLocalDate(selectedDate);
                selectedHappenDateLabel.setText("Selected Happen Date: " + happenDate);
            }
        }
    }


    private LocalDate convertToLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }
}
