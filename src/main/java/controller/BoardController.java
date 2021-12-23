package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.CellValue;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class BoardController {

    public static CellValue[][] readMaze(String address) {
        CellValue[][] cellValues = new CellValue[19][21];
        try {
            Scanner scanner = new Scanner(new File(address));
            for (int i = 0; i < 19; i++) {
                String line = scanner.nextLine();
                for (int j = 0; j < 21; j++) {
                    char cell = line.charAt(2 * j);
                    cellValues[i][j] = getCellValue(cell);
                }
            }
            scanner.close();
        } catch (Exception ignored){}
        return cellValues;
    }

    private static CellValue getCellValue(char cell) {
        switch (cell) {
            case '.': return CellValue.POINT;
            case '*': return CellValue.WALL;
            case '@': return CellValue.ENERGY_BOMB;
            case 'P': return CellValue.PACMAN_RIGHT;
            case '1': return CellValue.GHOST_1;
            case '2': return CellValue.GHOST_2;
            case '3': return CellValue.GHOST_3;
            case '4': return CellValue.GHOST_4;
        }
        return null;
    }

    public static void regenerateAll() {
        for (int i = 1; i <= 4; i++)
            GenerateMaze.generate("src/main/resources/view/mazes/" + i + ".txt");
    }

    public static void setChosen(String address) {
        copyFile(address, "src/main/resources/view/mazes/chosen.txt");
    }


    public static void copyFile(String source, String destination) {
        try {
            File sourceFile = new File(source);
            Scanner scanner = new Scanner(sourceFile);
            FileWriter fileWriter = new FileWriter(destination);
            while (scanner.hasNextLine())
                fileWriter.write(scanner.nextLine() + "\n");
            fileWriter.close();
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeToJson(String address, Object object) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            FileWriter fileWriter;
            fileWriter = new FileWriter(address);
            fileWriter.write(gson.toJson(object));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
