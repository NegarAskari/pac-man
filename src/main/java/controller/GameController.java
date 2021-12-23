package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import model.*;
import view.GameView;

import javafx.scene.media.Media;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class GameController implements EventHandler<KeyEvent> {
    private static Game game;
    private static boolean isPaused;
    private static boolean isMute;
    private static boolean isHardMode;
    private static Random random = new Random();
    private static boolean canGhostsMove;
    private static int initialLives;
    private static Timeline[] timelines = new Timeline[2];
    private static MediaPlayer mainMusic;
    private static MediaPlayer getPoint;
    private static MediaPlayer eatEnergyBomb;
    private static MediaPlayer eatGhost;
    private static MediaPlayer eatPacman;
    private static MediaPlayer gameOver;
    private static MediaPlayer resetMap;


    public static void setInitialLives(int initialLives) {
        GameController.initialLives = initialLives;
    }

    public static int getInitialLives() {
        return initialLives;
    }

    public static void setIsPaused(boolean isPaused) {
        GameController.isPaused = isPaused;
    }

    public static void setIsMute(boolean isMute) {
        GameController.isMute = isMute;
        if (isMute) mainMusic.pause();
        else mainMusic.play();
    }

    public static void setIsHardMode(boolean isHardMode) {
        GameController.isHardMode = isHardMode;
    }

    public static void newGame(CellValue[][] cellValues, Board board) {
        board.update(cellValues, false);
        playMainMusic();
        AccountController.setUnfinished(true);
        game = new Game(cellValues, board, initialLives, isHardMode);
        setTimeline();
    }

    public static void resumeGame(Board board) {
        playMainMusic();
        readSavedGame(board);
        game.setLastDirection(Direction.NONE);
        game.setNextDirection(Direction.NONE);
        setTimeline();
        if (game.isOnEnergyMode()) setEnergyModeTimeLine();
        initialLives = game.getLives();
        GameView.updateScore(game.getScore());
    }

    private static void readSavedGame(Board board) {
        try {
            FileReader fileReader = new FileReader("src/main/resources/users/" + AccountController.getLoggedInName() + "/unfinished.JSON");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            game = gson.fromJson(bufferedReader, Game.class);
            game.setBoard(board);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BoardController.copyFile("src/main/resources/users/" + AccountController.getLoggedInName() + "/chosen.txt",
                "src/main/resources/view/mazes/chosen.txt");
    }

    private static void setTimeline() {
        Timeline timeline = new Timeline();
        timelines[0] = timeline;
        KeyFrame move = new KeyFrame(Duration.seconds(0.15), actionEvent -> {
            if (isPaused) return;
            if (game.getNextDirection() == Direction.NONE) {
                if (game.getLastDirection() == Direction.NONE) return;
                if (canMove(game.getLastDirection())) movePacman(game.getLastDirection());
            } else {
                if (canMove(game.getNextDirection())) {
                    game.setLastDirection(game.getNextDirection());
                    game.setNextDirection(Direction.NONE);
                    movePacman(game.getLastDirection());
                } else {
                    if (game.getLastDirection() == Direction.NONE) return;
                    if (canMove(game.getLastDirection())) movePacman(game.getLastDirection());
                }
            }
            if (canGhostsMove) moveGhosts();
            canGhostsMove = !canGhostsMove;
        });
        timeline.getKeyFrames().add(move);
        timeline.setCycleCount(Animation.INDEFINITE);
        isPaused = false;
        timeline.play();
    }

    private static void moveGhosts() {
        if (game.isHardMode()) moveGhostsHardMode();
        else moveGhostsRandomly();
    }

    private static void playGameOver() {
        if (isMute) return;
        Media over = new Media(GameController.class.getResource("music/over.mp3").toExternalForm());
        gameOver = new MediaPlayer(over);
        gameOver.play();
    }

    private static void playEatEnergyBomb() {
        if (isMute) return;
        Media energy = new Media(GameController.class.getResource("music/energy.mp3").toExternalForm());
        eatEnergyBomb = new MediaPlayer(energy);
        eatEnergyBomb.play();
    }

    private static void playGetPoint() {
        if (isMute) return;
        Media point = new Media(GameController.class.getResource("music/point.mp3").toExternalForm());
        getPoint = new MediaPlayer(point);
        getPoint.play();
    }

    public static void playEatGhost() {
        if (isMute) return;
        Media ghost = new Media(GameController.class.getResource("music/ghost.mp3").toExternalForm());
        eatGhost = new MediaPlayer(ghost);
        eatGhost.play();
    }

    public static void playEatPacman() {
        if (isMute) return;
        Media pacman = new Media(GameController.class.getResource("music/pacman.mp3").toExternalForm());
        eatPacman = new MediaPlayer(pacman);
        eatPacman.play();
    }

    public static void playResetMap() {
        if (isMute) return;
        Media reset = new Media(GameController.class.getResource("music/reset.mp3").toExternalForm());
        resetMap = new MediaPlayer(reset);
        resetMap.play();
    }

    private static void playMainMusic() {
        Media main = new Media(GameController.class.getResource("music/main.mp3").toExternalForm());
        mainMusic = new MediaPlayer(main);
        mainMusic.setCycleCount(MediaPlayer.INDEFINITE);
        mainMusic.play();
    }

    public static void updateLives() {
        if (game.getLives() == 0) gameOver();
        else GameView.updateLives(game.getLives());
    }

    private static void gameOver() {
        finishGame();
        AccountController.setUnfinished(false);
        playGameOver();
        GameView.gameOver();
        game = null;
    }

    public static void finishGame() {
        timelines[0].stop();
        mainMusic.dispose();
        if (timelines[1] != null) timelines[1].stop();
        timelines[1] = null;
    }

    public static void updateScores() {
        int score = game.getScore();
        if (AccountController.isLoggedIn() && AccountController.getHighScore() < score)
            GameView.updateHighScore(score);
        GameView.updateScore(score);
    }

    private static void collision(CellValue ghost) {
        if (game.isOnEnergyMode())
            game.eatGhost(ghost);
        else
            game.eatPacman(ghost);
    }

    private static void moveGhostsRandomly() {
        for (int ghostNum = 1; ghostNum <= 4; ghostNum++) {
            if (game == null) return;
            moveGhostRandomly(ghostNum);
        }
    }

    private static void moveGhostRandomly(int ghostNum) {
        int ghostY = game.getGhostY(ghostNum);
        int ghostX = game.getGhostX(ghostNum);
        int possibleMoves = 0;
        Direction[] moves = new Direction[4];
        CellValue[] togo = new CellValue[4];
        if (canGhostEnter(game.getCell(ghostY - 1, ghostX))) {
            moves[possibleMoves] = Direction.UP;
            togo[possibleMoves] = game.getCell(ghostY - 1, ghostX);
            possibleMoves++;
        }
        if (canGhostEnter(game.getCell(ghostY + 1, ghostX))) {
            moves[possibleMoves] = Direction.DOWN;
            togo[possibleMoves] = game.getCell(ghostY + 1, ghostX);
            possibleMoves++;
        }
        if (canGhostEnter(game.getCell(ghostY, ghostX + 1))) {
            moves[possibleMoves] = Direction.RIGHT;
            togo[possibleMoves] = game.getCell(ghostY, ghostX + 1);
            possibleMoves++;
        }
        if (canGhostEnter(game.getCell(ghostY, ghostX - 1))) {
            moves[possibleMoves] = Direction.LEFT;
            togo[possibleMoves] = game.getCell(ghostY, ghostX - 1);
            possibleMoves++;
        }
        if (possibleMoves == 0) return;
        int rand = random.nextInt(possibleMoves);
        moveGhost(ghostNum, moves[rand], togo[rand]);
    }


    private static void moveGhost(int ghostNum, Direction direction, CellValue togo) {
        if (togo == CellValue.PACMAN_DOWN || togo == CellValue.PACMAN_UP ||
                togo == CellValue.PACMAN_RIGHT || togo == CellValue.PACMAN_LEFT)
            collision(game.getGhost(ghostNum));
        else
            game.moveGhost(ghostNum, direction);
    }

    private static void moveGhostsHardMode() {
        boolean energyMode = game.isOnEnergyMode();
        for (int ghostNum = 1; ghostNum <= 4; ghostNum++) {
            if (game == null) return;
            int ghostY = game.getGhostY(ghostNum);
            int ghostX = game.getGhostX(ghostNum);
            int pacmanX = game.getPacmanX();
            int pacmanY = game.getPacmanY();
            CellValue rightCell = game.getCell(ghostY, ghostX + 1);
            if (((!energyMode && pacmanX > ghostX) || (energyMode && pacmanX < ghostX))
                    && canGhostEnter(rightCell)) {
                moveGhost(ghostNum, Direction.RIGHT, rightCell);
                continue;
            }
            CellValue upCell = game.getCell(ghostY - 1, ghostX);
            if (((!energyMode && pacmanY < ghostY) || (energyMode && pacmanY > ghostY))
                    && canGhostEnter(upCell)) {
                moveGhost(ghostNum, Direction.UP, upCell);
                continue;
            }
            CellValue leftCell = game.getCell(ghostY, ghostX - 1);
            if (((!energyMode && pacmanX < ghostX) || (energyMode && pacmanX > ghostX))
                    && canGhostEnter(leftCell)) {
                moveGhost(ghostNum, Direction.LEFT, leftCell);
                continue;
            }
            CellValue downCell = game.getCell(ghostY + 1, ghostX);
            if (((!energyMode && pacmanY > ghostY) || (energyMode && pacmanY < ghostY))
                    && canGhostEnter(downCell)) {
                moveGhost(ghostNum, Direction.DOWN, downCell);
                continue;
            }
            moveGhostRandomly(ghostNum);
        }
    }


    private static boolean canGhostEnter(CellValue cellValue) {
        return cellValue == CellValue.EMPTY ||
                cellValue == CellValue.ENERGY_BOMB ||
                cellValue == CellValue.POINT ||
                cellValue == CellValue.PACMAN_RIGHT ||
                cellValue == CellValue.PACMAN_LEFT ||
                cellValue == CellValue.PACMAN_UP ||
                cellValue == CellValue.PACMAN_DOWN;
    }


    private static void movePacman(Direction direction) {
        int x = game.getPacmanX();
        int y = game.getPacmanY();
        switch (direction) {
            case DOWN:
                y++;
                break;
            case UP:
                y--;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
        }
        handlePacmanMovement(direction, game.getCell(y, x));
    }

    private static void handlePacmanMovement(Direction direction, CellValue togo) {
        switch (togo) {
            case POINT:
                playGetPoint();
                game.addScore(5);
                updateScores();
                game.movePacman(direction);
                game.decreaseToEat();
                break;
            case EMPTY:
                game.movePacman(direction);
                break;
            case ENERGY_BOMB:
                playEatEnergyBomb();
                game.movePacman(direction);
                energyMode();
                break;
            case GHOST_1:
            case GHOST_2:
            case GHOST_3:
            case GHOST_4:
                collision(togo);
        }
    }


    private static void energyMode() {
        if (game.isOnEnergyMode()) {
            game.resetCounter();
            return;
        }
        game.setOnEnergyMode();
        setEnergyModeTimeLine();
    }


    private static void setEnergyModeTimeLine() {
        Timeline timeline = new Timeline();
        timelines[1] = timeline;
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), actionEvent -> {
            if (isPaused) return;
            if (game.getLastDirection() == Direction.NONE && game.getNextDirection() == Direction.NONE) return;
            game.decreaseEnergyModeCounter();
            GameView.updateTimer(game.getEnergyModeTimeCounter());
            if (game.getEnergyModeTimeCounter() == -1) {
                timelines[1] = null;
                timeline.stop();
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    private static boolean canMove(Direction direction) {
        int x = game.getPacmanX();
        int y = game.getPacmanY();
        switch (direction) {
            case UP:
                return game.getCell(y - 1, x) != CellValue.WALL;
            case DOWN:
                return game.getCell(y + 1, x) != CellValue.WALL;
            case RIGHT:
                return game.getCell(y, x + 1) != CellValue.WALL;
            case LEFT:
                return game.getCell(y, x - 1) != CellValue.WALL;
        }
        return false;
    }


    public static void saveGame() {
        if (game == null || !AccountController.isLoggedIn()) return;
        BoardController.writeToJson("src/main/resources/users/" + AccountController.getLoggedInName() + "/unfinished.JSON", game);
        BoardController.copyFile("src/main/resources/view/mazes/chosen.txt",
                "src/main/resources/users/" + AccountController.getLoggedInName() + "/chosen.txt");
    }


    @Override
    public void handle(KeyEvent keyEvent) {
        Direction lastDirection = game.getLastDirection();
        switch (keyEvent.getCode()) {
            case LEFT:
                if (lastDirection != Direction.LEFT)
                    game.setNextDirection(Direction.LEFT);
                break;
            case RIGHT:
                if (lastDirection != Direction.RIGHT)
                    game.setNextDirection(Direction.RIGHT);
                break;
            case UP:
                if (lastDirection != Direction.UP)
                    game.setNextDirection(Direction.UP);
                break;
            case DOWN:
                if (lastDirection != Direction.DOWN)
                    game.setNextDirection(Direction.DOWN);
                break;
        }
    }

}
