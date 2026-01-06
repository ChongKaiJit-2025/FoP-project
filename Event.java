import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    private int eventId;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    
    // 统一的时间格式: yyyy-MM-dd HH:mm
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    // 将对象转换为 CSV 格式字符串
    public String toCsv() {
        return eventId + "," + title + "," + description + "," + 
               startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," + 
               endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // 从 CSV 字符串解析出对象
    public static Event fromCsv(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 5) return null;
        int id = Integer.parseInt(parts[0]);
        String title = parts[1];
        String desc = parts[2];
        LocalDateTime start = LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new Event(id, title, desc, start, end);
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s (%s - %s): %s", 
            eventId, title, startDateTime.format(FORMATTER), endDateTime.format(FORMATTER), description);
    }
}