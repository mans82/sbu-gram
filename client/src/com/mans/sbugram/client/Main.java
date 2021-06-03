package com.mans.sbugram.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Paths.get("views", "SignUp.fxml").toString()));

        Pane rootPane = fxmlLoader.load();

        stage.setScene(new Scene(rootPane));
        stage.setTitle("SBUgram");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
