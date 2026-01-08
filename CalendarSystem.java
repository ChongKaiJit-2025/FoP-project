import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarSystem {
    private List<Event> events;

    public CalendarSystem() {
        this.events = FileManager.loadEvents();
    }

    public List<Event> getAllEvents() {
        return events;
    }

    // 添加事件
    public void addEvent(String title, String desc, LocalDateTime start, LocalDateTime end) {
        int newId = generateNextId();
        Event newEvent = new Event(newId, title, desc, start, end);
        events.add(newEvent);
        saveChanges();
    }

    // --- 修改事件 (Update) ---
    public boolean updateEvent(int id, String newTitle, String newDesc, LocalDateTime newStart, LocalDateTime newEnd) {
        for (Event e : events) {
            if (e.getEventId() == id) {
                e.setTitle(newTitle);
                e.setDescription(newDesc);
                e.setStartDateTime(newStart);
                e.setEndDateTime(newEnd);
                saveChanges();
                return true;
            }
        }
        return false;
    }

    // 删除事件
    public boolean deleteEvent(int id) {
        boolean removed = events.removeIf(e -> e.getEventId() == id);
        if (removed) saveChanges();
        return removed;
    }
    
    // 按关键词搜索
    public List<Event> searchEvents(String keyword) {
        return events.stream()
                .filter(e -> e.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    // --- 按日期范围搜索 (Range Search) ---
    public List<Event> searchEventsByDateRange(LocalDate start, LocalDate end) {
        return events.stream()
                .filter(e -> {
                    LocalDate eventDate = e.getStartDateTime().toLocalDate();
                    return !eventDate.isBefore(start) && !eventDate.isAfter(end);
                })
                .sorted(Comparator.comparing(Event::getStartDateTime))
                .collect(Collectors.toList());
    }
    
    // 获取特定月份事件 (用于日历视图)
    public List<Event> getEventsByMonth(int year, int month) {
        return events.stream()
                .filter(e -> e.getStartDateTime().getYear() == year && e.getStartDateTime().getMonthValue() == month)
                .sorted(Comparator.comparing(Event::getStartDateTime))
                .collect(Collectors.toList());
    }

    // 重新加载数据 (用于还原后)
    public void reloadData() {
        this.events = FileManager.loadEvents();
    }

    public boolean backupData(java.io.File dest) {
        return FileManager.backupFile(dest);
    }

    public boolean restoreData(java.io.File src) {
        boolean success = FileManager.restoreFile(src);
        if (success) reloadData();
        return success;
    }

    private void saveChanges() {
        FileManager.saveEvents(events);
    }

    private int generateNextId() {
        return events.stream().mapToInt(Event::getEventId).max().orElse(0) + 1;
    }
}
