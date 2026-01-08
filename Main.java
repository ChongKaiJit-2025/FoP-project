import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class Main extends JFrame {

    private CalendarSystem system = new CalendarSystem();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private JTextArea displayArea; 

    public Main() {
        // --- 窗口基本设置 ---
        setTitle("Calendar App (Full Version)"); 
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 顶部标题 ---
        JLabel titleLabel = new JLabel("📅 2025 Calendar System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- 中间显示区域 ---
        displayArea = new JTextArea();
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // --- 底部按钮区 (两行布局) ---
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        
        // 第一行：视图与数据操作
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnView = createButton("📅 Calendar View");
        JButton btnList = createButton("📋 List All");
        JButton btnSearch = createButton("🔍 Search");
        JButton btnBackup = createButton("💾 Backup");
        JButton btnRestore = createButton("📂 Restore");
        
        row1.add(btnView); row1.add(btnList); row1.add(btnSearch); 
        row1.add(btnBackup); row1.add(btnRestore);

        // 第二行：事件增删改
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAdd  = createButton("➕ Add Event");
        JButton btnEdit = createButton("✏️ Edit Event");
        JButton btnDel  = createButton("🗑️ Delete Event");
        
        row2.add(btnAdd); row2.add(btnEdit); row2.add(btnDel);

        bottomPanel.add(row1);
        bottomPanel.add(row2);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 按钮事件绑定 ---
        btnView.addActionListener(e -> showCalendarPopup());
        btnList.addActionListener(e -> showList());
        btnSearch.addActionListener(e -> showSearchDialog());
        btnAdd.addActionListener(e -> addEventDialog());
        btnEdit.addActionListener(e -> editEventDialog());
        btnDel.addActionListener(e -> deleteEventDialog());
        btnBackup.addActionListener(e -> backupDialog());
        btnRestore.addActionListener(e -> restoreDialog());

        displayArea.setText("Welcome! All features are ready.\n\nFeatures available:\n- Add/Edit/Delete Events\n- Calendar/List Views\n- Search by Keyword or Date Range\n- Backup and Restore Data");
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(130, 35));
        return btn;
    }

    // --- 功能实现 ---

    // 1. 日历视图
    private void showCalendarPopup() {
        try {
            String yearStr = JOptionPane.showInputDialog(this, "Enter Year (e.g., 2025):");
            if (yearStr == null) return;
            String monthStr = JOptionPane.showInputDialog(this, "Enter Month (1-12):");
            if (monthStr == null) return;

            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            
            if (month < 1 || month > 12) throw new Exception("Invalid Month");

            StringBuilder sb = new StringBuilder();
            YearMonth ym = YearMonth.of(year, month);
            int daysInMonth = ym.lengthOfMonth();
            int offset = (ym.atDay(1).getDayOfWeek().getValue() == 7) ? 0 : ym.atDay(1).getDayOfWeek().getValue();

            List<Event> events = system.getEventsByMonth(year, month);
            Set<Integer> eventDays = new HashSet<>();
            for (Event e : events) eventDays.add(e.getStartDateTime().getDayOfMonth());

            sb.append("      📅  ").append(year).append(" - ").append(ym.getMonth()).append("\n");
            sb.append(" Sun Mon Tue Wed Thu Fri Sat\n");
            sb.append(" ---------------------------\n");
            for(int i=0; i<offset; i++) sb.append("    ");
            for(int d=1; d<=daysInMonth; d++) {
                if(eventDays.contains(d)) sb.append(String.format("[%2d]", d));
                else sb.append(String.format(" %2d ", d));
                if((d + offset) % 7 == 0) sb.append("\n");
            }
            sb.append("\n\n [Events this Month]:\n");
            if(events.isEmpty()) sb.append(" (None)");
            for(Event e : events) sb.append(" • ").append(e.toString()).append("\n");

            displayArea.setText(sb.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Input!");
        }
    }

    // 2. 列表视图
    private void showList() {
        displayEvents(system.getAllEvents(), "All Events");
    }

    private void displayEvents(List<Event> events, String title) {
        StringBuilder sb = new StringBuilder("=== " + title + " ===\n\n");
        sb.append(String.format("%-5s %-15s %-32s %s\n", "ID", "Title", "Time", "Desc"));
        sb.append("----------------------------------------------------------------------------------\n");
        if(events.isEmpty()) sb.append(" (No records found)\n");
        for (Event e : events) {
            sb.append(String.format("%-5d %-15s %s -> %s   %s\n",
                e.getEventId(), truncate(e.getTitle(), 12),
                e.getStartDateTime().format(FMT),
                e.getEndDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                e.getDescription()));
        }
        displayArea.setText(sb.toString());
    }

    // 3. 添加事件
    private void addEventDialog() {
        showEventForm(null); 
    }

    // 4. 编辑事件
    private void editEventDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Event ID to Edit:");
        if (idStr == null) return;
        try {
            int id = Integer.parseInt(idStr);
            Event target = system.getAllEvents().stream().filter(e -> e.getEventId() == id).findFirst().orElse(null);
            if (target == null) {
                JOptionPane.showMessageDialog(this, "Event ID not found!");
                return;
            }
            showEventForm(target); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ID must be a number!");
        }
    }

    // 通用表单 (用于Add和Edit)
    private void showEventForm(Event existingEvent) {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField tF = new JTextField(existingEvent != null ? existingEvent.getTitle() : "");
        JTextField dF = new JTextField(existingEvent != null ? existingEvent.getDescription() : "");
        JTextField sF = new JTextField(existingEvent != null ? existingEvent.getStartDateTime().format(FMT) : "2025-01-01 10:00");
        JTextField eF = new JTextField(existingEvent != null ? existingEvent.getEndDateTime().format(FMT) : "2025-01-01 12:00");

        panel.add(new JLabel("Title:")); panel.add(tF);
        panel.add(new JLabel("Desc:")); panel.add(dF);
        panel.add(new JLabel("Start (yyyy-MM-dd HH:mm):")); panel.add(sF);
        panel.add(new JLabel("End (yyyy-MM-dd HH:mm):")); panel.add(eF);

        String dialogTitle = (existingEvent == null) ? "Add Event" : "Edit Event (ID: " + existingEvent.getEventId() + ")";
        int res = JOptionPane.showConfirmDialog(null, panel, dialogTitle, JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            try {
                LocalDateTime start = LocalDateTime.parse(sF.getText(), FMT);
                LocalDateTime end = LocalDateTime.parse(eF.getText(), FMT);
                if(end.isBefore(start)) { JOptionPane.showMessageDialog(this, "End time cannot be before start!"); return; }

                if (existingEvent == null) {
                    system.addEvent(tF.getText(), dF.getText(), start, end);
                    JOptionPane.showMessageDialog(this, "Added!");
                } else {
                    system.updateEvent(existingEvent.getEventId(), tF.getText(), dF.getText(), start, end);
                    JOptionPane.showMessageDialog(this, "Updated!");
                }
                showList();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Format Error! Use yyyy-MM-dd HH:mm");
            }
        }
    }

    // 5. 删除事件
    private void deleteEventDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter ID to Delete:");
        if(idStr != null) {
            try {
                if(system.deleteEvent(Integer.parseInt(idStr))) {
                    JOptionPane.showMessageDialog(this, "Deleted.");
                    showList();
                } else {
                    JOptionPane.showMessageDialog(this, "ID not found.");
                }
            } catch(Exception e) { JOptionPane.showMessageDialog(this, "Invalid ID."); }
        }
    }

    // 6. 搜索功能 (支持关键词和日期范围)
    private void showSearchDialog() {
        String[] options = {"By Keyword", "By Date Range"};
        int choice = JOptionPane.showOptionDialog(this, "Choose search type:", "Search",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) { 
            String key = JOptionPane.showInputDialog(this, "Enter Keyword:");
            if (key != null) displayEvents(system.searchEvents(key), "Search: " + key);
        } else if (choice == 1) { 
            JPanel p = new JPanel(new GridLayout(2, 2));
            JTextField startF = new JTextField("2025-01-01");
            JTextField endF = new JTextField("2025-12-31");
            p.add(new JLabel("From (yyyy-MM-dd):")); p.add(startF);
            p.add(new JLabel("To (yyyy-MM-dd):")); p.add(endF);
            
            int res = JOptionPane.showConfirmDialog(null, p, "Date Range Search", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    LocalDate s = LocalDate.parse(startF.getText(), DATE_ONLY_FMT);
                    LocalDate e = LocalDate.parse(endF.getText(), DATE_ONLY_FMT);
                    displayEvents(system.searchEventsByDateRange(s, e), "Range: " + s + " to " + e);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Format Error (yyyy-MM-dd)");
                }
            }
        }
    }

    // 7. 备份数据
    private void backupDialog() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Backup File");
        fc.setSelectedFile(new File("backup_events.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = fc.getSelectedFile();
            if(system.backupData(dest)) JOptionPane.showMessageDialog(this, "Backup Successful!");
            else JOptionPane.showMessageDialog(this, "Backup Failed.");
        }
    }

    // 8. 还原数据
    private void restoreDialog() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select Backup File to Restore");
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File src = fc.getSelectedFile();
            int confirm = JOptionPane.showConfirmDialog(this, "This will OVERWRITE current data. Continue?", "Warning", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if(system.restoreData(src)) {
                    JOptionPane.showMessageDialog(this, "Restore Successful!");
                    showList();
                } else JOptionPane.showMessageDialog(this, "Restore Failed.");
            }
        }
    }
    
    private String truncate(String s, int len) {
        if(s == null) return "";
        if(s.length() > len) return s.substring(0, len-2)+"..";
        return s;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}