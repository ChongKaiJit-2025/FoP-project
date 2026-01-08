package com.app.main;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarSystem {
    private List<Event> events;
    private List<RecurrentEvent> recEvents;

    public CalendarSystem() {
        this.events = FileManager.loadEvents();
        this.recEvents = FileManager.loadRecurrent();
    }

    // Get all events including generated recurrent ones
    public List<Event> getAllEventsCombined() {
        List<Event> all = new ArrayList<>(events);
        for(RecurrentEvent re : recEvents) {
            all.addAll(re.generateOccurrences());
        }
        all.sort(Comparator.comparing(Event::getStartDateTime));
        return all;
    }

    // Conflict detection
    public boolean checkConflict(LocalDateTime start, LocalDateTime end) {
        for(Event e : getAllEventsCombined()) {
            if(start.isBefore(e.getEndDateTime()) && end.isAfter(e.getStartDateTime())) return true;
        }
        return false;
    }

    public void addEvent(String t, String d, LocalDateTime s, LocalDateTime e) {
        int id = events.stream().mapToInt(Event::getEventId).max().orElse(0) + 1;
        events.add(new Event(id, t, d, s, e));
        FileManager.saveEvents(events);
    }

    public void addRecurrent(String t, String d, LocalDateTime s, LocalDateTime e, String f, int c) {
        int id = recEvents.size() + 1;
        recEvents.add(new RecurrentEvent(id, t, d, s, e, f, c));
        FileManager.saveRecurrent(recEvents);
    }

    public void updateEvent(int id, String t, String d, LocalDateTime s, LocalDateTime e) {
        for(Event ev : events) {
            if(ev.getEventId() == id) {
                ev.setTitle(t); ev.setDescription(d); ev.setStartDateTime(s); ev.setEndDateTime(e);
                FileManager.saveEvents(events);
                return;
            }
        }
    }

    public boolean deleteEvent(int id) {
        boolean res = events.removeIf(e -> e.getEventId() == id);
        if(res) FileManager.saveEvents(events);
        return res;
    }

    public List<Event> search(String k) {
        return getAllEventsCombined().stream()
               .filter(e -> e.getTitle().toLowerCase().contains(k.toLowerCase()))
               .collect(Collectors.toList());
    }

    public List<Event> getByMonth(int y, int m) {
        return getAllEventsCombined().stream()
               .filter(e -> e.getStartDateTime().getYear() == y && e.getStartDateTime().getMonthValue() == m)
               .collect(Collectors.toList());
    }
    
    public boolean backup(java.io.File f) { return FileManager.backup(f); }
    public boolean restore(java.io.File f) { 
        boolean res = FileManager.restore(f);
        if(res) {
            events = FileManager.loadEvents();
            recEvents = FileManager.loadRecurrent();
        }
        return res;
    }
}