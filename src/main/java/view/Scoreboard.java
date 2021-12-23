package view;

import controller.AccountController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;

public class Scoreboard extends Application {
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Scoreboard.stage = stage;
        HBox hBox = makeBoard();
        hBox.setPrefHeight(500);
        hBox.setPrefWidth(400);
        hBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(getClass().getResource("stylesheets/scoreboard.css").toExternalForm());
        stage.setScene(scene);
    }


    private HBox makeBoard() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(40);
        hBox.setId("scoreboard");
        String[] styles = getStyles();
        Label[] labels = new Label[3];
        VBox[] vBoxes = new VBox[3];

        makeBoxes(hBox, styles[0], labels, vBoxes);
        addUsers(styles, labels, vBoxes);
        addButton(vBoxes[0]);
        return hBox;
    }

    private void makeBoxes(HBox hBox, String style, Label[] labels, VBox[] vBoxes) {
        for (int i = 0; i < 3; i++) {
            vBoxes[i] = new VBox();
            vBoxes[i].setSpacing(5);
            hBox.getChildren().add(vBoxes[i]);
        }
        labels[0] = new Label("RANK");
        labels[1] = new Label("SCORE");
        labels[2] = new Label("NAME");
        for (int i = 0; i < 3; i++) {
            labels[i].setStyle(style);
            vBoxes[i].getChildren().add(labels[i]);
        }
    }

    private void addButton(VBox vBox) {
        Button back = new Button();
        back.setText("Back");
        back.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    new MainPage().start(stage);
                } catch (Exception ignored) {
                }
            }
        });
        vBox.getChildren().add(back);
    }

    private void addUsers(String[] styles, Label[] labels, VBox[] vBoxes) {
        int counter = 0;
        ArrayList<Map.Entry<String, Map.Entry<Integer, Integer>>> rankings = AccountController.getRankings();
        for (Map.Entry<String, Map.Entry<Integer, Integer>> entry : rankings) {
            counter++;
            if (entry.getKey().equals(AccountController.getLoggedInName())) labels[2] = new Label("YOU");
            else labels[2] = new Label(entry.getKey());
            labels[0] = new Label(entry.getValue().getKey() + "");
            labels[1] = new Label(entry.getValue().getValue() + "");
            for (int i = 0; i < 3; i++) {
                labels[i].setStyle(styles[counter % 6]);
                vBoxes[i].getChildren().add(labels[i]);
            }
        }
    }

    private String[] getStyles() {
        String[] styles = new String[6];
        styles[0] = "-fx-text-fill: #fdb747";
        styles[1] = "-fx-text-fill: #fd0001";
        styles[2] = "-fx-text-fill: #00fddd";
        styles[3] = "-fx-text-fill: #0000FE";
        styles[4] = "-fx-text-fill: #fdb7dd";
        styles[5] = "-fx-text-fill: #ffffff";
        return styles;
    }
}
