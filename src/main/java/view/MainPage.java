package view;

import controller.AccountController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class MainPage extends Application {
    private static Stage stage;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        MainPage.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainPage.scene = scene;
        initial();
        stage.setTitle("Main Page");
        stage.setScene(scene);
    }


    private void initial() {
        String text = "welcome " + AccountController.getLoggedInName() + "!";
        ((Label) scene.lookup("#welcome")).setText(text);
        if (!AccountController.isGameUnfinished()) scene.lookup("#resume").setDisable(true);
    }



    public void changePassword() {
        Popup popup = new Popup();
        GridPane gridPane = new GridPane();
        Label label = new Label();
        TextField textField = new TextField();
        HBox hBox = new HBox();
        Button okButton = new Button();
        Button cancelButton = new Button();
        configurePasswordPopup(popup, gridPane, label, textField, hBox, okButton, cancelButton);
        addButtonFunctionsForPassword(popup, textField, okButton, cancelButton);
        popup.show(stage);
    }



    private void addButtonFunctionsForPassword(Popup popup, TextField textField, Button okButton, Button cancelButton) {
        okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String newPassword = textField.getText();
                if (!newPassword.isBlank()) {
                    AccountController.changePassword(newPassword);
                    popup.hide();
                }
            }
        });

        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                popup.hide();
            }
        });
    }



    private void configurePasswordPopup(Popup popup, GridPane gridPane, Label label, TextField textField,
                                        HBox hBox, Button okButton, Button cancelButton) {
        popup.getContent().add(gridPane);
        gridPane.add(label, 0, 0);
        gridPane.add(textField, 1, 0);
        gridPane.add(hBox, 1, 1);
        hBox.getChildren().add(cancelButton);
        hBox.getChildren().add(okButton);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        gridPane.setAlignment(Pos.CENTER);
        label.setText("new password:");
        okButton.setText("OK");
        cancelButton.setText("Cancel");
        gridPane.setStyle("-fx-background-color: #0000fe; -fx-padding: 20px;");
        label.setStyle("-fx-text-fill: #ffffff; -fx-padding: 10px; -fx-font-size: 14px; -fx-font-weight: bold;");
        okButton.setStyle("-fx-background-color: #fdfd01;");
        cancelButton.setStyle("-fx-background-color: #fdfd01; -fx-opacity: 50;");
    }


    public void deleteAccount() {
        GridPane gridPane = new GridPane();
        HBox hBox = new HBox();
        Popup popup = new Popup();
        Label label = new Label();
        Button yesButton = new Button();
        Button noButton = new Button();
        configureDeletePopup(gridPane, hBox, popup, label, yesButton, noButton);
        addButtonFunctionsForDelete(popup, yesButton, noButton);
        popup.show(stage);
    }


    private void addButtonFunctionsForDelete(Popup popup, Button yesButton, Button noButton) {
        yesButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                AccountController.deleteAccount();
                popup.hide();
                try {
                    new FirstPage().start(stage);
                } catch (Exception ignored) {
                }
            }
        });
        noButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                popup.hide();
            }
        });
    }


    private void configureDeletePopup(GridPane gridPane, HBox hBox, Popup popup, Label label, Button yesButton, Button noButton) {
        hBox.getChildren().add(noButton);
        hBox.getChildren().add(yesButton);
        gridPane.add(label, 0, 0);
        gridPane.add(hBox, 0, 1);
        popup.getContent().add(gridPane);
        label.setText("Are you sure that you want to delete your account?");
        yesButton.setText("Yes");
        noButton.setText("No");
        gridPane.setAlignment(Pos.CENTER);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        gridPane.setStyle("-fx-background-color: #0000fe; -fx-padding: 20px;");
        label.setStyle("-fx-text-fill: #ffffff; -fx-padding: 10px; -fx-font-size: 14px; -fx-font-weight: bold;");
        noButton.setStyle("-fx-background-color: #fdfd01;");
        yesButton.setStyle("-fx-background-color: #fdfd01;");
    }


    public void logout() throws Exception {
        AccountController.logout();
        new FirstPage().start(stage);
    }


    public void newGame() throws Exception {
        new GameSettings().start(stage);
    }


    public void showScoreboard() throws Exception {
        new Scoreboard().start(stage);
    }


    public void resumeGame() throws Exception{
        GameView.setIsResumed(true);
        new GameView().start(stage);
    }

}
