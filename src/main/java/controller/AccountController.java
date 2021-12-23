package controller;

import model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import com.google.gson.*;

public class AccountController {
    private static User loggedIn;

    public static int login(String username, String password) {
        if (username.isBlank()) return 1;
        if (password.isBlank()) return 2;
        User user = User.getUserByUsername(username);
        if (user == null) return 3;
        if (!user.doesPasswordMatch(password)) return 4;
        loggedIn = user;
        return 0;
    }

    public static int signup(String username, String password, String repeatPassword) {
        if (username.isBlank()) return 1;
        if (password.isBlank()) return 2;
        if (repeatPassword.isBlank()) return 3;
        if (User.getUserByUsername(username) != null) return 4;
        if (!password.equals(repeatPassword)) return 5;
        new User(username, password);
        return 0;
    }

    public static void logout() {
        loggedIn = null;
    }

    public static void deleteAccount() {
        loggedIn.deleteAccount();
        try {
            File folder = new File("src/main/resources/users/" + loggedIn.getUsername());
            for (File file : folder.listFiles())
                file.delete();
            folder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loggedIn = null;
    }

    public static String getLoggedInName() {
        return loggedIn.getUsername();
    }

    public static void changePassword(String newPassword) {
        loggedIn.setPassword(newPassword);
    }

    public static boolean isGameUnfinished() {
        return loggedIn.hasUnfinishedGame();
    }

    public static boolean isLoggedIn() {
        return loggedIn != null;
    }

    public static void writeAllUsers() {
        for (User user : User.getAllUsers()) {
            String username = user.getUsername();
            new File("src/main/resources/users/" + username).mkdir();
            String address = "src/main/resources/users/" + username + "/" + username + ".JSON";
            BoardController.writeToJson(address, user);
        }
    }


    public static void readAllUsers() {
        File folder = new File("src/main/resources/users");
        for (File userFolder : folder.listFiles()) {
            String username = userFolder.getName();
            User user = readUser("src/main/resources/users/" + username + "/" + username + ".JSON");
            User.getAllUsers().add(user);
        }
    }

    public static User readUser(String address) {
        try {
            FileReader fileReader = new FileReader(address);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, User.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void addToMyMazes(int mazeNum) {
        try {
            String username = loggedIn.getUsername();
            File source = new File("src/main/resources/view/mazes/" + mazeNum + ".txt");
            Scanner scanner = new Scanner(source);
            File folder = new File("src/main/resources/users/" + username);
            folder.mkdir();
            FileWriter destination = new FileWriter("src/main/resources/users/" + username + "/" + getNumOfMyMaze(folder) + ".txt");
            while (scanner.hasNextLine())
                destination.write(scanner.nextLine() + "\n");
            scanner.close();
            destination.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getNumOfMyMaze(File folder) {
        int counter = 1;
        for (File file : folder.listFiles())
            if (file.getName().matches("\\d+\\.txt")) counter++;
        return counter;
    }

    public static int getNumOfMyMazes() {
        File folder = new File("src/main/resources/users/" + loggedIn.getUsername());
        folder.mkdir();
        return getNumOfMyMaze(folder) - 1;
    }

    public static ArrayList<Map.Entry<String, Map.Entry<Integer, Integer>>> getRankings() {
        ArrayList<Map.Entry<String, Map.Entry<Integer, Integer>>> rankings = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>(User.getAllUsers());
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Integer.compare(o2.getHighScore(), o1.getHighScore());
            }
        });
        int lastScore = -1;
        int currentRank = 1;
        int counter = 0;
        for (User user : users) {
            counter++;
            if (user.getHighScore() != lastScore) currentRank = counter;
            lastScore = user.getHighScore();
            rankings.add(new AbstractMap.SimpleEntry<>(user.getUsername(), new AbstractMap.SimpleEntry<>(currentRank, lastScore)));
            if (counter == 10) break;
        }
        return rankings;
    }

    public static int getHighScore() {
        if (loggedIn == null) return 0;
        else return loggedIn.getHighScore();
    }

    public static void newHighScore(int score) {
        loggedIn.setHighScore(score);
    }

    public static void setUnfinished(boolean isUnfinished) {
        if (loggedIn == null) return;
        loggedIn.setHasUnfinishedGame(isUnfinished);
    }

}
