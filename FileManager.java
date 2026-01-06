import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    private static final String FILE_NAME = "event.csv";

    // 读取所有事件
    public static List<Event> loadEvents() {
        List<Event> events = new ArrayList<>();
        File file = new File(FILE_NAME);
        
        if (!file.exists()) return events; // 如果文件不存在，返回空列表

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // 跳过标题行
            while ((line = br.readLine()) != null) {
                Event e = Event.fromCsv(line);
                if (e != null) events.add(e);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return events;
    }

    // 保存所有事件
    public static void saveEvents(List<Event> events) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            pw.println("eventId,title,description,startDateTime,endDateTime"); // Header
            for (Event e : events) {
                pw.println(e.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
    
    // 备份功能 (Bonus Mark)
    public static void backupData(String backupPath) {
        try {
            Files.copy(Paths.get(FILE_NAME), Paths.get(backupPath + "/event_backup.csv"), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Backup successful to " + backupPath);
        } catch (IOException e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }
}