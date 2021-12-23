package view;

import controller.AccountController;
import controller.BoardController;
import controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;
import model.Board;
import model.CellValue;

public class GameView extends Application {
    private static Stage stage;
    private static Scene scene;
    private static boolean isResumed;
    private static boolean highScoreStyleChanged;

    public static void setIsResumed(boolean isResumed) {
        GameView.isResumed = isResumed;
    }

    public static void updateScore(int score) {
        ((Label) scene.lookup("#score")).setText(score + "");
    }

    public static void updateHighScore(int highScore) {
        Label label = ((Label) scene.lookup("#highScore"));
        if (!highScoreStyleChanged) {
            label.setStyle("-fx-text-fill: #b4ecfe; -fx-font-weight: bold; -fx-font-size: 28;");
            highScoreStyleChanged = true;
        }
        label.setText(highScore + "");
        AccountController.newHighScore(highScore);
    }

    public static void updateTimer(int time) {
        if (time == -1) ((Label) scene.lookup("#time")).setText("");
        else ((Label) scene.lookup("#time")).setText("" + time);
    }

    public static void updateLives(int lives) {
        ((Label) scene.lookup("#life")).setText(lives + "");
    }

    public static void gameOver() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GameView.class.getResource("GameOver.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Game Over!");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage stage) throws Exception {
        GameView.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Game.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        GameView.scene = scene;
        root.requestFocus();
        stage.setTitle("Pacman");
        initial();
        stage.setScene(scene);
        scene.setOnKeyPressed(new GameController());
        highScoreStyleChanged = false;
    }


    private void initial() {
        Board board = (Board) scene.lookup("#board");
        if (isResumed)
            GameController.resumeGame(board);
        else {
            CellValue[][] cellValues = BoardController.readMaze("src/main/resources/view/mazes/chosen.txt");
            GameController.newGame(cellValues, board);
            updateScore(0);
        }
        ((Label) scene.lookup("#highScore")).setText(AccountController.getHighScore() + "");
        ((Label) scene.lookup("#life")).setText(GameController.getInitialLives() + "");
    }


    public void pause(MouseEvent mouseEvent) {
        GameController.setIsPaused(((ToggleButton) mouseEvent.getTarget()).isSelected());
    }


    public void mute(MouseEvent mouseEvent) {
        GameController.setIsMute(((ToggleButton) mouseEvent.getTarget()).isSelected());
    }


    public void restart() throws Exception {
        GameView.setIsResumed(false);
        new GameView().start(stage);
    }


    public void exit() throws Exception {
        GameController.finishGame();
        if (AccountController.isLoggedIn())
            new MainPage().start(stage);
        else
            new FirstPage().start(stage);
        GameController.saveGame();
    }

}
