package view;

import controller.AccountController;
import controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class FirstPage extends Application {
    private static Stage stage;
    private static Scene scene;

    public static void main(String[] args) {
        AccountController.readAllUsers();
        launch(args);
        AccountController.writeAllUsers();
        GameController.saveGame();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FirstPage.stage = stage;
        newScene("Login.fxml", "Log In");
        stage.show();
    }

    private void newScene(String fileName, String title) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fileName));
        Scene scene = new Scene(fxmlLoader.load());
        FirstPage.scene = scene;
        stage.setScene(scene);
        stage.setTitle(title);
    }

    public void login() throws Exception{
        TextField username = (TextField) scene.lookup("#username");
        PasswordField password = (PasswordField) scene.lookup("#password");
        Label warning = (Label) scene.lookup("#warning");
        warning.setText("");
        int message = AccountController.login(username.getText(), password.getText());
        switch (message) {
            case 0:
                new MainPage().start(stage);
                break;
            case 1:
                warning.setText("you have to enter a username to log in!");
                break;
            case 2:
                warning.setText("you have to enter your password to log in!");
                break;
            case 3:
                warning.setText("there is no user with this username!");
                username.clear();
                password.clear();
                break;
            case 4:
                warning.setText("your password is incorrect!");
                password.clear();
        }
    }

    public void signUp() {
        TextField username = (TextField) scene.lookup("#username");
        PasswordField password = (PasswordField) scene.lookup("#password");
        PasswordField repeatPassword = (PasswordField) scene.lookup("#repeatPassword");
        Label warning = (Label) scene.lookup("#warning");
        Label success = (Label) scene.lookup("#success");
        warning.setText("");
        success.setText("");
        int message = AccountController.signup(username.getText(), password.getText(), repeatPassword.getText());
        switch (message) {
            case 0:
                success.setText("sign up successful");
                username.clear();
                password.clear();
                repeatPassword.clear();
                break;
            case 1:
                warning.setText("you have to choose a username to sign up!");
                break;
            case 2:
                warning.setText("you have to choose a password to sign up!");
                break;
            case 3:
                warning.setText("please repeat your password");
                break;
            case 4:
                warning.setText("this username is already taken!");
                username.clear();
                break;
            case 5:
                warning.setText("passwords do not match!");
                repeatPassword.clear();
        }
    }

    public void enterLogin () throws Exception {
        newScene("Login.fxml", "Log In");
    }

    public void enterSignUp() throws Exception{
        newScene("Signup.fxml", "Sign Up");
    }

    public void newGame() throws Exception{
        new GameSettings().start(stage);
    }
}
