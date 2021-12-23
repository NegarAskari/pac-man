package view;

import controller.AccountController;
import controller.BoardController;
import controller.GameController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Board;
import model.CellValue;

public class GameSettings extends Application {
    private static Stage stage;
    private static Scene scene;
    private static int currentMaze;
    private static int numOfMazes;
    private static String directory;
    private static int lives;

    @Override
    public void start(Stage stage) throws Exception {
        currentMaze = 1;
        numOfMazes = 4;
        directory = "src/main/resources/view/mazes/";
        lives = 5;
        GameSettings.stage = stage;
        newScene("Game Settings", "GameSettings.fxml");
    }

    private void newScene(String title, String address) throws java.io.IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(address));
        Scene scene = new Scene(fxmlLoader.load());
        GameSettings.scene = scene;
        loadBoard();
        initial(title.equals("Game Settings"));
        stage.setScene(scene);
        stage.setTitle(title);
    }

    private void initial(boolean hasLives) {
        GameController.setIsHardMode(false);
        if (!AccountController.isLoggedIn()) {
            scene.lookup("#save").setDisable(true);
            scene.lookup("#show").setDisable(true);
        } else if (AccountController.getNumOfMyMazes() == 0) scene.lookup("#show").setDisable(true);
        if (currentMaze == 1) scene.lookup("#back").setDisable(true);
        if (!hasLives && AccountController.getNumOfMyMazes() == 1) scene.lookup("#next").setDisable(true);
        if (hasLives) {
            ((Label) scene.lookup("#life")).setText(lives + "");
            if (lives == 2) scene.lookup("#lifeDecrease").setDisable(true);
            if (lives == 5) scene.lookup("#lifeIncrease").setDisable(true);
        }
    }


    private void loadBoard() {
        Label label = (Label) scene.lookup("#name");
        label.setText("Maze num." + currentMaze);
        Board board = (Board) scene.lookup("#board");
        CellValue[][] cellValues = BoardController.readMaze(directory + currentMaze + ".txt");
        board.update(cellValues, false);
    }

    public void back() {
        scene.lookup("#next").setDisable(false);
        currentMaze--;
        if (currentMaze == 1) scene.lookup("#back").setDisable(true);
        loadBoard();
    }

    public void next() {
        scene.lookup("#back").setDisable(false);
        currentMaze++;
        if (currentMaze == numOfMazes) scene.lookup("#next").setDisable(true);
        loadBoard();
    }

    public void regenerate() {
        BoardController.regenerateAll();
        loadBoard();
    }

    public void saveMaze() {
        AccountController.addToMyMazes(currentMaze);
        scene.lookup("#show").setDisable(false);
    }

    public void showMyMazes() throws Exception {
        currentMaze = 1;
        directory = "src/main/resources/users/" + AccountController.getLoggedInName() + "/";
        numOfMazes = AccountController.getNumOfMyMazes();
        newScene("My Mazes", "MyMazes.fxml");
        loadBoard();
    }

    public void backToGameSettings() throws Exception {
        currentMaze = 1;
        directory = "src/main/resources/view/mazes/";
        numOfMazes = 4;
        newScene("Game Settings", "GameSettings.fxml");
        loadBoard();
    }

    public void exitSettings() throws Exception {
        if (AccountController.isLoggedIn())
            new MainPage().start(stage);
        else
            new FirstPage().start(stage);
    }

    public void decreaseLife() {
        scene.lookup("#lifeIncrease").setDisable(false);
        lives--;
        ((Label) scene.lookup("#life")).setText(lives + "");
        if (lives == 2) scene.lookup("#lifeDecrease").setDisable(true);
    }

    public void increaseLife() {
        scene.lookup("#lifeDecrease").setDisable(false);
        lives++;
        ((Label) scene.lookup("#life")).setText(lives + "");
        if (lives == 5) scene.lookup("#lifeIncrease").setDisable(true);
    }

    public void startGame() throws Exception {
        GameView.setIsResumed(false);
        GameController.setInitialLives(lives);
        BoardController.setChosen(directory + currentMaze + ".txt");
        new GameView().start(stage);
    }

    public void hardMode() {
        ToggleButton button = ((ToggleButton) scene.lookup("#gameMode"));
        boolean isHardMode = button.isSelected();
        GameController.setIsHardMode(isHardMode);
        if (isHardMode) button.setText("Hard Mode");
        else button.setText("Easy Mode");
    }
}
