import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main { 
    private static CalendarSystem system = new CalendarSystem();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Calendar App 2025 ===");
        
        while (true) {
            System.out.println("\n1. View Calendar (Month)");
            System.out.println("2. List All Events");
            System.out.println("3. Add Event");
            System.out.println("4. Delete Event");
            System.out.println("5. Search Event");
            System.out.println("6. Backup Data");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1": viewCalendar(); break;
                case "2": listEvents(); break;
                case "3": addEventUI(); break;
                case "4": deleteEventUI(); break;
                case "5": searchEventUI(); break;
                case "6": backupUI(); break;
                case "0": 
                    System.out.println("Goodbye!"); 
                    return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    // 核心：日历月视图打印逻辑 
    private static void viewCalendar() {
        System.out.print("Enter Year (e.g. 2025): ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Month (1-12): ");
        int month = Integer.parseInt(scanner.nextLine());
        
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
        
        // 调整：使日历从周日开始 (Sun=0) 或保持 Mon=1，这里演示 Sun Mo Tu...
        // Java DayOfWeek: Mon=1 ... Sun=7. 
        // 转换 logic: Mon(1)->1, Sun(7)->0. 
        int startOffset = (dayOfWeekValue == 7) ? 0 : dayOfWeekValue; 

        System.out.println("\n   " + yearMonth.getMonth() + " " + year);
        System.out.println("Su Mo Tu We Th Fr Sa");

        // 打印空白前缀
        for (int i = 0; i < startOffset; i++) {
            System.out.print("   ");
        }

        List<Event> monthEvents = system.getEventsByMonth(year, month);
        Set<Integer> eventDays = new HashSet<>();
        for (Event e : monthEvents) eventDays.add(e.getStartDateTime().getDayOfMonth());

        for (int day = 1; day <= daysInMonth; day++) {
            // 如果当天有事件，加上 * 标记
            if (eventDays.contains(day)) {
                System.out.printf("%2d*", day);
            } else {
                System.out.printf("%2d ", day);
            }

            if ((day + startOffset) % 7 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n\nEvents in this month:");
        for (Event e : monthEvents) {
            System.out.println(e);
        }
    }

    private static void listEvents() {
        List<Event> list = system.getAllEvents();
        if (list.isEmpty()) System.out.println("No events found.");
        else list.forEach(System.out::println);
    }

    private static void addEventUI() {
        try {
            System.out.print("Title: ");
            String title = scanner.nextLine();
            System.out.print("Description: ");
            String desc = scanner.nextLine();
            
            System.out.print("Start Time (yyyy-MM-dd HH:mm): ");
            String startStr = scanner.nextLine();
            LocalDateTime start = LocalDateTime.parse(startStr, Event.FORMATTER);
            
            System.out.print("End Time (yyyy-MM-dd HH:mm): ");
            String endStr = scanner.nextLine();
            LocalDateTime end = LocalDateTime.parse(endStr, Event.FORMATTER);
            
            if (end.isBefore(start)) {
                System.out.println("Error: End time cannot be before start time.");
                return;
            }

            system.addEvent(title, desc, start, end);
        } catch (Exception e) {
            System.out.println("Invalid input format. Use yyyy-MM-dd HH:mm");
        }
    }

    private static void deleteEventUI() {
        System.out.print("Enter Event ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            system.deleteEvent(id);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }
    
    private static void searchEventUI() {
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();
        List<Event> results = system.searchEvents(keyword);
        if (results.isEmpty()) System.out.println("No matching events.");
        else results.forEach(System.out::println);
    }
    
    private static void backupUI() {
        System.out.print("Enter backup directory path (e.g. . or C:/Temp): ");
        String path = scanner.nextLine();
        FileManager.backupData(path);
    }
}