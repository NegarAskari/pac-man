package controller;

import java.util.Random;
import java.io.FileWriter;
import java.lang.StringBuilder;

public class GenerateMaze {

    public static void generate(String address) {
        Random rand = new Random();
        int height = 9;
        int width = 10;

        char[][] maze = new char[2 * height + 1][2 * width + 1];
        initializeMaze(maze, 2 * height + 1, 2 * width + 1);

        StringBuilder ans = new StringBuilder();
        boolean[][] visited = new boolean[height][width];
        visited[0][0] = true;
        generateMaze(maze, height, width, visited, rand, 0, 0);
        maze[7][5] = maze[7][15] = maze[13][5] = maze[13][15] = '2';
        translate(rand, height, width, maze, ans);
        write(ans, address);
    }

    private static void write(StringBuilder ans, String address) {
        try {
            FileWriter fileWriter = new FileWriter(address);
            fileWriter.write(ans.toString());
            fileWriter.close();
        } catch (Exception e) {
        }
    }

    private static void translate(Random rand, int height, int width, char[][] maze, StringBuilder ans) {
        for (int j = 0; j < height * 2 + 1; j++) {
            for (int k = 0; k < width * 2 + 1; k++) {
                if (j == 0 || j == height * 2 || k == 0 || k == width * 2)
                    ans.append("* ");
                else if (maze[j][k] == '1') {
                    if (rand.nextInt(6) == 4) ans.append(". ");
                    else ans.append("* ");
                } else if (maze[j][k] == '2') ans.append("@ ");
                else if (j == 1 && k == 1) ans.append("1 ");
                else if (j == 1 && k == width * 2 - 1) ans.append("2 ");
                else if (j == height * 2 - 1 && k == 1) ans.append("3 ");
                else if (j == height * 2 - 1 && k == width * 2 - 1) ans.append("4 ");
                else if (j == height && k == width + 1) ans.append("P ");
                else ans.append(". ");
            }
            ans.append("\n");
        }
    }

    private static void initializeMaze(char[][] maze, int height, int width) {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (i % 2 == 0 || j % 2 == 0) maze[i][j] = '1';
                else maze[i][j] = '*';
    }

    private static void generateMaze(char[][] maze, int height, int width, boolean[][] visited, Random rand, int currentX, int currentY) {
        char[] freeNeighbours = new char[4];
        int numOfFreeNeighbours = 0;
        if (currentX + 1 < width && !visited[currentY][currentX + 1]) {
            freeNeighbours[numOfFreeNeighbours] = 'R';
            numOfFreeNeighbours++;
        }
        if (currentX - 1 >= 0 && !visited[currentY][currentX - 1]) {
            freeNeighbours[numOfFreeNeighbours] = 'L';
            numOfFreeNeighbours++;
        }
        if (currentY + 1 < height && !visited[currentY + 1][currentX]) {
            freeNeighbours[numOfFreeNeighbours] = 'D';
            numOfFreeNeighbours++;
        }
        if (currentY - 1 >= 0 && !visited[currentY - 1][currentX]) {
            freeNeighbours[numOfFreeNeighbours] = 'U';
            numOfFreeNeighbours++;
        }
        if (numOfFreeNeighbours == 0) return;

        int direction = rand.nextInt(numOfFreeNeighbours);

        move(freeNeighbours[direction], maze, currentY, currentX, visited, height, width, rand);
        if (numOfFreeNeighbours == 1) return;
        generateMaze(maze, height, width, visited, rand, currentX, currentY);
    }

    private static void move(char direction, char[][] maze, int currentY, int currentX, boolean[][] visited, int height, int width, Random rand) {
        int newX = currentX, newY = currentY;
        switch (direction) {
            case 'R':
                maze[currentY * 2 + 1][currentX * 2 + 2] = '0';
                newX++;
                break;
            case 'L':
                maze[currentY * 2 + 1][currentX * 2] = '0';
                newX--;
                break;
            case 'D':
                maze[currentY * 2 + 2][currentX * 2 + 1] = '0';
                newY++;
                break;
            case 'U':
                maze[currentY * 2][currentX * 2 + 1] = '0';
                newY--;
                break;
        }
        visited[newY][newX] = true;
        generateMaze(maze, height, width, visited, rand, newX, newY);
    }
}
