package model;

import java.util.ArrayList;

public class User {
    private static ArrayList<User> allUsers = new ArrayList<>();
    private String username;
    private String password;
    private int highScore = 0;
    private boolean hasUnfinishedGame = false;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        allUsers.add(this);
    }

    public static User getUserByUsername(String username) {
        for (User user : allUsers)
            if (user.username.equals(username)) return user;
        return null;
    }

    public static ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public void setHasUnfinishedGame(boolean hasUnfinishedGame) {
        this.hasUnfinishedGame = hasUnfinishedGame;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public boolean hasUnfinishedGame() {
        return hasUnfinishedGame;
    }

    public boolean doesPasswordMatch(String password) {
        return this.password.equals(password);
    }

    public void deleteAccount() {
        allUsers.remove(this);
    }
}
