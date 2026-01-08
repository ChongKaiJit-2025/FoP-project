import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    private static final String FILE_NAME = "event.csv";

    // 读取所有事件
    public static List<Event> loadEvents() {
        List<Event> events = new ArrayList<>();
        File file = new File(FILE_NAME);
        
        if (!file.exists()) return events; 

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // 跳过表头
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
            pw.println("eventId,title,description,startDateTime,endDateTime"); 
            for (Event e : events) {
                pw.println(e.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // --- 备份功能 ---
    public static boolean backupFile(File destination) {
        try {
            Files.copy(Paths.get(FILE_NAME), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 还原功能 ---
    public static boolean restoreFile(File source) {
        try {
            Files.copy(source.toPath(), Paths.get(FILE_NAME), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}