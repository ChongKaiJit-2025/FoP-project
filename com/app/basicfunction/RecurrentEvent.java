package com.app.basicfunction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RecurrentEvent {
    private int id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String frequency; // "DAILY", "WEEKLY", "MONTHLY"
    private int repeatCount;

    public RecurrentEvent(int id, String title, String description, LocalDateTime start, LocalDateTime end, String frequency, int repeatCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = start;
        this.endDateTime = end;
        this.frequency = frequency;
        this.repeatCount = repeatCount;
    }

    public String toCsv() {
        return id + "," + title + "," + description + "," + 
               startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," + 
               endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," +
               frequency + "," + repeatCount;
    }

    public static RecurrentEvent fromCsv(String line) {
        try {
            String[] parts = line.split(",");
            return new RecurrentEvent(
                Integer.parseInt(parts[0]), parts[1], parts[2],
                LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                parts[5], Integer.parseInt(parts[6])
            );
        } catch (Exception e) { return null; }
    }

    // Generate occurrences based on frequency and count
    public List<Event> generateOccurrences() {
        List<Event> list = new ArrayList<>();
        LocalDateTime currStart = startDateTime;
        LocalDateTime currEnd = endDateTime;

        for (int i = 0; i < repeatCount; i++) {
            // ID = -1 means generated occurrence which doesn't have its own ID
            list.add(new Event(-1, title + " (" + (i + 1) + ")", description, currStart, currEnd));
            if (frequency.equals("DAILY")) { currStart = currStart.plusDays(1); currEnd = currEnd.plusDays(1); }
            else if (frequency.equals("WEEKLY")) { currStart = currStart.plusWeeks(1); currEnd = currEnd.plusWeeks(1); }
            else if (frequency.equals("MONTHLY")) { currStart = currStart.plusMonths(1); currEnd = currEnd.plusMonths(1); }
        }
        return list;
    }
}