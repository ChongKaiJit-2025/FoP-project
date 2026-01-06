import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarSystem {
    private List<Event> events;

    public CalendarSystem() {
        this.events = FileManager.loadEvents();
    }

    public void addEvent(String title, String desc, LocalDateTime start, LocalDateTime end) {
        // 冲突检测 (Additional Feature)
        if (isConflict(start, end)) {
            System.out.println("Warning: This event conflicts with an existing event!");
            // 这里你可以选择 return 阻止添加，或者仅仅是警告
        }
        
        int newId = generateNextId();
        Event newEvent = new Event(newId, title, desc, start, end);
        events.add(newEvent);
        saveChanges();
        System.out.println("Event added successfully.");
    }

    public void deleteEvent(int id) {
        boolean removed = events.removeIf(e -> e.getEventId() == id);
        if (removed) {
            saveChanges();
            System.out.println("Event deleted.");
        } else {
            System.out.println("Event ID not found.");
        }
    }
    
    // 搜索功能
    public List<Event> searchEvents(String keyword) {
        return events.stream()
                .filter(e -> e.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // 获取特定月份的事件 (用于日历视图)
    public List<Event> getEventsByMonth(int year, int month) {
        return events.stream()
                .filter(e -> e.getStartDateTime().getYear() == year && e.getStartDateTime().getMonthValue() == month)
                .sorted(Comparator.comparing(Event::getStartDateTime))
                .collect(Collectors.toList());
    }

    private void saveChanges() {
        FileManager.saveEvents(events);
    }

    private int generateNextId() {
        return events.stream().mapToInt(Event::getEventId).max().orElse(0) + 1;
    }
    
    // 冲突检测逻辑: (StartA < EndB) && (EndA > StartB)
    private boolean isConflict(LocalDateTime start, LocalDateTime end) {
        for (Event e : events) {
            if (start.isBefore(e.getEndDateTime()) && end.isAfter(e.getStartDateTime())) {
                return true;
            }
        }
        return false;
    }
    
    public List<Event> getAllEvents() {
        return events;
    }
}
