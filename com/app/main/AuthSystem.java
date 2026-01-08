package com.app.main;

import java.util.*;

public class AuthSystem {
    private List<User> users;

    public AuthSystem() {
        users = UserFileManager.loadUsers();
    }

    public boolean register(String username, String password) {
        for (User u : users)
            if (u.getUsername().equalsIgnoreCase(username)) return false;

        users.add(new User(username, password));
        UserFileManager.saveUsers(users);
        return true;
    }

    public boolean login(String username, String password) {
        for (User u : users)
            if (u.getUsername().equals(username) && u.getPassword().equals(password))
                return true;
        return false;
    }
}