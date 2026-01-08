package com.app.main;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class MainCalendar extends JFrame {

    private CalendarSystem sys;
    private JTextArea area;
    private final String mainMenuText;
    private AuthSystem auth; // optional if you want logout

    public MainCalendar(AuthSystem auth) {
        this.auth = auth;
        this.sys = new CalendarSystem();
        this.mainMenuText =
            "Welcome to NaGoCi Calendar and Scheduler App!\n" +
            "Use the buttons below to manage your days ahead.\n\n" +
            " View Calendar: Show month view with events marked.\n" +
            " List All: List all events in detail.\n" +
            " Add Normal: Add a one-time event.\n" +
            " Add Recurring: Add a repeating event.\n" +
            " Edit Event: Modify an existing event by ID.\n" +
            " Delete Event: Remove an event by ID.\n" +
            " Search: Find events by keyword.\n" +
            " Backup: Save data to a chosen directory.\n" +
            " Restore: Load data from a chosen directory.\n" +
            " Logout: Return to login screen.";

        initUI();
    }

    private void initUI() {
        setTitle("NaGoCi Calendar V1.0");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Personal Calendar and Schedule System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Display area
        area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new GridLayout(3,1));
        JPanel row1 = new JPanel(new FlowLayout());
        JPanel row2 = new JPanel(new FlowLayout());
        JPanel row3 = new JPanel(new FlowLayout()); // for Logout

        // Row1 buttons
        addButton(row1, "View Calendar", e -> showCalendar());
        addButton(row1, "List All", e -> showList());
        addButton(row1, "Search", e -> search());
        addButton(row1, "Backup", e -> backup());
        addButton(row1, "Restore", e -> restore());

        // Row2 buttons
        addButton(row2, "Add Normal", e -> addNormal());
        addButton(row2, "Add Recurring", e -> addRecur());
        addButton(row2, "Edit Event", e -> edit());
        addButton(row2, "Delete Event", e -> deleteEvent());

        // Row3 buttons
        addButton(row3, "Main Menu", e -> showMainMenu());
        addButton(row3, "Logout", e -> logout());

        btnPanel.add(row1);
        btnPanel.add(row2);
        btnPanel.add(row3);
        add(btnPanel, BorderLayout.SOUTH);

        // Show main menu text
        showMainMenu();
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener listener){
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(120, 35));
        btn.addActionListener(listener);
        panel.add(btn);
    }

    // === Main menu / logout methods ===
    private void showMainMenu() {
        area.setText(mainMenuText);
    }

    private void logout() {
        dispose(); // close current calendar window
        SwingUtilities.invokeLater(() -> {
            AuthSystem newAuth = new AuthSystem(); // reload users
            new MainLauncher(); // show login again
        });
    }

    // === Placeholder methods ===
    private void showCalendar(){ area.setText("Showing calendar..."); }
    private void showList(){ area.setText("Listing all events..."); }
    private void search(){ area.setText("Searching events..."); }
    private void backup(){ area.setText("Backup..."); }
    private void restore(){ area.setText("Restore..."); }
    private void addNormal(){ area.setText("Add normal event..."); }
    private void addRecur(){ area.setText("Add recurring event..."); }
    private void edit(){ area.setText("Edit event..."); }
    private void deleteEvent(){ area.setText("Delete event..."); }
}
