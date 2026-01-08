import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    private static final String EVENT_FILE = "event.csv";
    private static final String REC_FILE = "recurrent.csv";

    // 读写普通事件
    public static List<Event> loadEvents() {
        List<Event> list = new ArrayList<>();
        File f = new File(EVENT_FILE);
        if(!f.exists()) return list;
        try(BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; br.readLine(); 
            while((line=br.readLine())!=null) {
                Event e = Event.fromCsv(line);
                if(e!=null) list.add(e);
            }
        } catch(Exception e) { e.printStackTrace(); }
        return list;
    }

    public static void saveEvents(List<Event> list) {
        try(PrintWriter pw = new PrintWriter(new FileWriter(EVENT_FILE))) {
            pw.println("id,title,desc,start,end");
            for(Event e : list) pw.println(e.toCsv());
        } catch(Exception e) { e.printStackTrace(); }
    }

    // 读写重复事件
    public static List<RecurrentEvent> loadRecurrent() {
        List<RecurrentEvent> list = new ArrayList<>();
        File f = new File(REC_FILE);
        if(!f.exists()) return list;
        try(BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; br.readLine();
            while((line=br.readLine())!=null) {
                RecurrentEvent re = RecurrentEvent.fromCsv(line);
                if(re!=null) list.add(re);
            }
        } catch(Exception e) { e.printStackTrace(); }
        return list;
    }

    public static void saveRecurrent(List<RecurrentEvent> list) {
        try(PrintWriter pw = new PrintWriter(new FileWriter(REC_FILE))) {
            pw.println("id,title,desc,start,end,freq,count");
            for(RecurrentEvent re : list) pw.println(re.toCsv());
        } catch(Exception e) { e.printStackTrace(); }
    }
    
    // 备份
    public static boolean backup(File dir) {
        try {
            if(new File(EVENT_FILE).exists()) Files.copy(Paths.get(EVENT_FILE), new File(dir, "backup_event.csv").toPath(), StandardCopyOption.REPLACE_EXISTING);
            if(new File(REC_FILE).exists()) Files.copy(Paths.get(REC_FILE), new File(dir, "backup_recurrent.csv").toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch(Exception e) { return false; }
    }

    // 还原
    public static boolean restore(File dir) {
        try {
            File ev = new File(dir, "backup_event.csv");
            File re = new File(dir, "backup_recurrent.csv");
            if(ev.exists()) Files.copy(ev.toPath(), Paths.get(EVENT_FILE), StandardCopyOption.REPLACE_EXISTING);
            if(re.exists()) Files.copy(re.toPath(), Paths.get(REC_FILE), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch(Exception e) { return false; }
    }
}