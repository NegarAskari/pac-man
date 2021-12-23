module Pacman {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.media;


    opens view to javafx.fxml;
    opens model to com.google.gson;
    exports view;
    exports model;
}