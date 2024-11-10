package org.example.controllers;

import org.example.databases.EventDao;
import org.example.models.Event;

import java.util.List;

public class EventController {
    private final EventDao eventDao;

    public EventController() {
        eventDao = new EventDao();
    }

    public void addNewEvent(Event event) {
        eventDao.addNewEvent(event);
    }

    public void updateEvent(Event event) {
        eventDao.updateEvent(event);
    }

    public void deleteEvent(int eventId) {
        eventDao.deleteEvent(eventId);
    }

    public List<Event> getAllEventsForWeek(int weekNumber) {
        return eventDao.getAllEventsForWeek(weekNumber);
    }

    public List<Event> getAllEvents() {
        return eventDao.getAllEvents();
    }
}
