package com.app.main;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    private int eventId;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    
    // 统一的时间格式
    public static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Event(int eventId, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    // Getters
    public int getEventId() { return eventId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    // CSV 转换
    public String toCsv() {
        return eventId + "," + title + "," + description + "," + 
               startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," + 
               endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static Event fromCsv(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length < 5) return null;
            int id = Integer.parseInt(parts[0]);
            String title = parts[1];
            String desc = parts[2];
            LocalDateTime start = LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return new Event(id, title, desc, start, end);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s (%s - %s)", 
            eventId, title, startDateTime.format(FMT), endDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}