import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

// Inherit JFrame to let Main class run as a window application
public class Main extends JFrame {

    // Core logic object (Connects to your CalendarSystem.java)
    private CalendarSystem system = new CalendarSystem();
    // Unified time format
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // UI Component: The large white board in the middle for displaying calendar and lists
    private JTextArea displayArea; 

    public Main() {
        // --- 1. Window Basic Settings ---
        setTitle("My Calendar Assignment (GUI)"); 
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());

        // --- 2. Top Title ---
        JLabel titleLabel = new JLabel("📅 2025 Personal Schedule System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- 3. Center Display Area ---
        displayArea = new JTextArea();
        // Key: Use Monospaced font to ensure calendar grid alignment
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 15)); 
        displayArea.setEditable(false); // Disable manual editing
        // Add scroll bar to prevent content from being cut off
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // --- 4. Bottom Button Area ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        JButton btnView = createButton("📅 View Calendar");
        JButton btnList = createButton("📋 All Events");
        JButton btnAdd  = createButton("➕ Add Event");
        JButton btnDel  = createButton("🗑️ Delete Event");
        JButton btnSearch = createButton("🔍 Search");

        buttonPanel.add(btnView);
        buttonPanel.add(btnList);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDel);
        buttonPanel.add(btnSearch);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- 5. Bind Button Functions ---
        btnView.addActionListener(e -> showCalendarPopup());
        btnList.addActionListener(e -> showList());
        btnAdd.addActionListener(e -> addEventDialog());
        btnDel.addActionListener(e -> deleteEventDialog());
        btnSearch.addActionListener(e -> searchEventDialog());

        // Initial welcome message
        displayArea.setText("Welcome!\nPlease click the buttons below to start.\n\nAll data will be automatically saved to event.csv.");
    }

    // Helper method: Quickly create buttons with unified style
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(150, 40)); // Increased width slightly for English text
        return btn;
    }

    // ================= Functional Logic Implementation =================

    // 1. Show Calendar
    private void showCalendarPopup() {
        try {
            String yearStr = JOptionPane.showInputDialog(this, "Enter Year (e.g., 2025):");
            if (yearStr == null) return;
            String monthStr = JOptionPane.showInputDialog(this, "Enter Month (1-12):");
            if (monthStr == null) return;

            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);

            StringBuilder sb = new StringBuilder();
            YearMonth ym = YearMonth.of(year, month);
            int daysInMonth = ym.lengthOfMonth();
            // Calculate which day of the week the first day is, and adjust offset (Sunday first)
            int dayOfWeek = ym.atDay(1).getDayOfWeek().getValue(); // 1=Mon...7=Sun
            int offset = (dayOfWeek == 7) ? 0 : dayOfWeek;

            // Get events for the month to mark on calendar
            List<Event> events = system.getEventsByMonth(year, month);
            Set<Integer> eventDays = new HashSet<>();
            for (Event e : events) eventDays.add(e.getStartDateTime().getDayOfMonth());

            sb.append("      📅  ").append(year).append(" - ").append(ym.getMonth()).append("\n");
            sb.append(" Sun Mon Tue Wed Thu Fri Sat\n");
            sb.append(" ---------------------------\n");

            // Print blank placeholders
            for(int i=0; i<offset; i++) sb.append("    ");

            // Print dates
            for(int d=1; d<=daysInMonth; d++) {
                if(eventDays.contains(d)) sb.append(String.format("[%2d]", d)); // Use [] for days with events
                else sb.append(String.format(" %2d ", d));
                
                // New line
                if((d + offset) % 7 == 0) sb.append("\n");
            }
            
            sb.append("\n\n [Events this Month]:\n");
            if(events.isEmpty()) sb.append(" (No events this month)");
            for(Event e : events) sb.append(" • ").append(e.toString()).append("\n");

            displayArea.setText(sb.toString());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input Error: Please enter a valid number!");
        }
    }

    // 2. Show List
    private void showList() {
        List<Event> events = system.getAllEvents();
        StringBuilder sb = new StringBuilder();
        // Print Header
        sb.append(String.format("%-5s %-15s %-25s %s\n", "ID", "Title", "Time Slot", "Description"));
        sb.append("--------------------------------------------------------------------------------\n");
        
        for (Event e : events) {
            sb.append(String.format("%-5d %-15s %s - %s %s\n",
                e.getEventId(), 
                truncate(e.getTitle(), 12),
                e.getStartDateTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                e.getEndDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), // End time shows only HH:mm
                e.getDescription()));
        }
        displayArea.setText(sb.toString());
    }

    // 3. Add Event (Popup Form)
    private void addEventDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField tF = new JTextField();
        JTextField dF = new JTextField();
        JTextField sF = new JTextField("2025-01-01 10:00");
        JTextField eF = new JTextField("2025-01-01 12:00");

        panel.add(new JLabel("Title:")); panel.add(tF);
        panel.add(new JLabel("Description:")); panel.add(dF);
        panel.add(new JLabel("Start (yyyy-MM-dd HH:mm):")); panel.add(sF);
        panel.add(new JLabel("End (yyyy-MM-dd HH:mm):")); panel.add(eF);

        int res = JOptionPane.showConfirmDialog(null, panel, "Add New Event", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                LocalDateTime start = LocalDateTime.parse(sF.getText(), FMT);
                LocalDateTime end = LocalDateTime.parse(eF.getText(), FMT);
                
                if (end.isBefore(start)) {
                    JOptionPane.showMessageDialog(this, "Error: End time cannot be earlier than start time!");
                    return;
                }

                system.addEvent(tF.getText(), dF.getText(), start, end);
                JOptionPane.showMessageDialog(this, "✅ Added Successfully!");
                showList(); // Auto refresh list
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Format Error! Please use yyyy-MM-dd HH:mm");
            }
        }
    }

    // 4. Delete Event
    private void deleteEventDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Event ID to delete:");
        if (idStr != null) {
            try {
                system.deleteEvent(Integer.parseInt(idStr));
                JOptionPane.showMessageDialog(this, "Operation complete (Deleted if ID existed)");
                showList();
            } catch (Exception e) { 
                JOptionPane.showMessageDialog(this, "ID must be a number!"); 
            }
        }
    }

    // 5. Search Event
    private void searchEventDialog() {
        String key = JOptionPane.showInputDialog(this, "Enter search keyword:");
        if (key != null) {
            List<Event> res = system.searchEvents(key);
            StringBuilder sb = new StringBuilder("🔍 Search Results:\n\n");
            for(Event e : res) sb.append(e.toString()).append("\n");
            displayArea.setText(sb.toString());
        }
    }
    
    // Helper: Truncate string (prevent table misalignment)
    private String truncate(String s, int len) {
        if(s.length() > len) return s.substring(0, len-2)+"..";
        return s;
    }

    public static void main(String[] args) {
        // Standard Swing startup
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}