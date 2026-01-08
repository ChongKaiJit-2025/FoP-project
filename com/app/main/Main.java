package com.app.main;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame {
    private CalendarSystem sys = new CalendarSystem();
    private JTextArea area;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Main() {
        setTitle("Nagoci V1.0");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("Personal Calendar and Schedule System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // 显示区域
        area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        // 按钮面板
        JPanel btnPanel = new JPanel(new GridLayout(2, 1));
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        addButton(row1, "View Calendar", e -> showCal());
        addButton(row1, "List All", e -> showList());
        addButton(row1, "Search", e -> search());
        addButton(row1, "Backup", e -> backup());
        addButton(row1, "Restore", e -> restore());

        addButton(row2, "Add Normal", e -> addNormal());
        addButton(row2, "Add Recurring", e -> addRecur());
        addButton(row2, "Edit Event", e -> edit());
        addButton(row2, "Delete Event", e -> del());
        addButton(row2, "Main Menu", e -> showMainMenu());

        btnPanel.add(row1);
        btnPanel.add(row2);
        add(btnPanel, BorderLayout.SOUTH);
        
        area.setText(mainMenuText);
    }

    private final String mainMenuText = 
        "Welcome to NaGoCi Calendar and Scheduler App by Nasi Goreng Cina!\n" +
        "Use the buttons below to manage your days ahead.\n\n" +
        " View Calendar: Show month view with events marked.\n" +
        " List All: List all events in detail.\n" +
        " Add Normal: Add a one-time event.\n" +
        " Add Recurring: Add a repeating event.\n" +
        " Edit Event: Modify an existing event by ID.\n" +
        " Delete Event: Remove an event by ID.\n" +
        " Search: Find events by keyword.\n" +
        " Backup: Save data to a chosen directory.\n" +
        " Restore: Load data from a chosen directory.";

    private void addButton(JPanel p, String t, java.awt.event.ActionListener l) {
        JButton b = new JButton(t);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setPreferredSize(new Dimension(120, 35));
        b.addActionListener(l);
        p.add(b);
    }
    private void showMainMenu() {
        area.setText(mainMenuText);
    }

    // --- Realising Functionality ---
    private void showCal() {
        try {
            String y = JOptionPane.showInputDialog("Year (e.g. 2025):"); if(y==null) return;
            String m = JOptionPane.showInputDialog("Month (1-12):"); if(m==null) return;
            int year = Integer.parseInt(y); int month = Integer.parseInt(m);

            StringBuilder sb = new StringBuilder();
            YearMonth ym = YearMonth.of(year, month);
            int off = (ym.atDay(1).getDayOfWeek().getValue() == 7) ? 0 : ym.atDay(1).getDayOfWeek().getValue();
            
            List<Event> evs = sys.getByMonth(year, month);
            Set<Integer> days = new HashSet<>();
            for(Event e : evs) days.add(e.getStartDateTime().getDayOfMonth());

            sb.append("      📅 Calendar: ").append(year).append("-").append(month).append("\n");
            sb.append(" Sun Mon Tue Wed Thu Fri Sat\n ---------------------------\n");
            for(int i=0; i<off; i++) sb.append("    ");
            for(int d=1; d<=ym.lengthOfMonth(); d++) {
                sb.append(days.contains(d) ? String.format("[%2d]", d) : String.format(" %2d ", d));
                if((d+off)%7==0) sb.append("\n");
            }
            sb.append("\n\nEvents:\n");
            for(Event e : evs) sb.append(e).append("\n");
            area.setText(sb.toString());
        } catch(Exception e) { JOptionPane.showMessageDialog(this, "Input Error!"); }
    }

    private void showList() {
        StringBuilder sb = new StringBuilder("=== All Events ===\n");
        sb.append(String.format("%-5s %-15s %-32s %s\n", "ID", "Title", "Time", "Desc"));
        sb.append("----------------------------------------------------------------------\n");
        for(Event e : sys.getAllEventsCombined()) {
            sb.append(String.format("%-5d %-15s %s -> %s   %s\n",
                e.getEventId(), 
                (e.getTitle().length()>12 ? e.getTitle().substring(0,10)+".." : e.getTitle()),
                e.getStartDateTime().format(FMT),
                e.getEndDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                e.getDescription()));
        }
        area.setText(sb.toString());
    }

    private void addNormal() {
        JPanel p = new JPanel(new GridLayout(4, 2));
        JTextField t=new JTextField(), d=new JTextField(), s=new JTextField("2025-01-01 10:00"), e=new JTextField("2025-01-01 12:00");
        p.add(new JLabel("Title:"));p.add(t); p.add(new JLabel("Desc:"));p.add(d);
        p.add(new JLabel("Start:"));p.add(s); p.add(new JLabel("End:"));p.add(e);
        
        if(JOptionPane.showConfirmDialog(null, p, "Add Normal", 2)==0) {
            try {
                LocalDateTime st = LocalDateTime.parse(s.getText(), FMT);
                LocalDateTime en = LocalDateTime.parse(e.getText(), FMT);
                if(sys.checkConflict(st, en)) JOptionPane.showMessageDialog(this, "⚠️ Warning: Time Conflict!");
                sys.addEvent(t.getText(), d.getText(), st, en);
                JOptionPane.showMessageDialog(this, "Added!"); showList();
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Format Error (yyyy-MM-dd HH:mm)"); }
        }
    }

    private void addRecur() {
        JPanel p = new JPanel(new GridLayout(6, 2));
        JTextField t=new JTextField(), d=new JTextField(), s=new JTextField("2025-01-01 10:00"), e=new JTextField("2025-01-01 12:00"), c=new JTextField("5");
        JComboBox<String> f = new JComboBox<>(new String[]{"DAILY","WEEKLY","MONTHLY"});
        p.add(new JLabel("Title:"));p.add(t); p.add(new JLabel("Desc:"));p.add(d);
        p.add(new JLabel("Start:"));p.add(s); p.add(new JLabel("End:"));p.add(e);
        p.add(new JLabel("Freq:"));p.add(f); p.add(new JLabel("Count:"));p.add(c);

        if(JOptionPane.showConfirmDialog(null, p, "Add Recurring", 2)==0) {
            try {
                sys.addRecurrent(t.getText(), d.getText(), LocalDateTime.parse(s.getText(), FMT), LocalDateTime.parse(e.getText(), FMT), (String)f.getSelectedItem(), Integer.parseInt(c.getText()));
                JOptionPane.showMessageDialog(this, "Recurring Added!"); showList();
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error!"); }
        }
    }

    private void edit() {
        String idStr = JOptionPane.showInputDialog("Enter ID to edit (Normal events only):");
        if(idStr==null) return;
        try {
            int id = Integer.parseInt(idStr);
            JPanel p = new JPanel(new GridLayout(4, 2));
            JTextField t=new JTextField(), d=new JTextField(), s=new JTextField(), e=new JTextField();
            p.add(new JLabel("New Title:"));p.add(t); p.add(new JLabel("New Desc:"));p.add(d);
            p.add(new JLabel("New Start (yyyy-MM-dd HH:mm):"));p.add(s); p.add(new JLabel("New End:"));p.add(e);
            if(JOptionPane.showConfirmDialog(null, p, "Edit Event", 2)==0) {
                sys.updateEvent(id, t.getText(), d.getText(), LocalDateTime.parse(s.getText(), FMT), LocalDateTime.parse(e.getText(), FMT));
                JOptionPane.showMessageDialog(this, "Updated!"); showList();
            }
        } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error or ID not found!"); }
    }

    private void del() {
        String id = JOptionPane.showInputDialog("Enter ID to delete:");
        if(id!=null) {
            if(sys.deleteEvent(Integer.parseInt(id))) { JOptionPane.showMessageDialog(this, "Deleted!"); showList(); }
            else JOptionPane.showMessageDialog(this, "ID not found (Cannot delete generated recurring instances)");
        }
    }

    private void search() {
        String k = JOptionPane.showInputDialog("Enter Keyword:");
        if(k!=null) {
            StringBuilder sb = new StringBuilder("Search Results:\n");
            for(Event e : sys.search(k)) sb.append(e).append("\n");
            area.setText(sb.toString());
        }
    }

    private void backup() {
        JFileChooser fc = new JFileChooser(); fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
            if(sys.backup(fc.getSelectedFile())) JOptionPane.showMessageDialog(this, "Backup Success!");
            else JOptionPane.showMessageDialog(this, "Backup Failed!");
        }
    }

    private void restore() {
        JFileChooser fc = new JFileChooser(); fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            if(sys.restore(fc.getSelectedFile())) { JOptionPane.showMessageDialog(this, "Restore Success!"); showList(); }
            else JOptionPane.showMessageDialog(this, "Restore Failed!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}