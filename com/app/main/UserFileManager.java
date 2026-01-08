package com.app.main;

import java.io.*;
import java.util.*;

public class UserFileManager {
    private static final String FILE_NAME = "data/userregister.csv";

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File f = new File(FILE_NAME);
        if (!f.exists()) return users;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                User u = User.fromCsv(line);
                if (u != null) users.add(u);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return users;
    }

    public static void saveUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (User u : users) pw.println(u.toCsv());
        } catch (IOException e) { e.printStackTrace(); }
    }
}