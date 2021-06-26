package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.requests.LoginRequest;
import com.mans.sbugram.models.responses.LoginResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class Login {
    public Label titleLabel;
    public TextField usernameTextField;
    public PasswordField passwordPasswordField;
    public Button loginButton;
    public Label statusLabel;
    public HBox rootHBox;
    public VBox titleVBox;
    public VBox inputVBox;
    public Button switchSignUpButton;


    public void onLoginButtonAction(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Socket serverConnectionSocket;
        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert =  new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Server connection failed");
            errorAlert.initModality(Modality.WINDOW_MODAL);
            errorAlert.initOwner(currentStage);
            errorAlert.show();
            return;
        }

        RequestSendTask requestSendTask = new RequestSendTask(
                new LoginRequest(username, password),
                serverConnectionSocket
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            loginButton.setText("Login");
            loginButton.setDisable(false);
            LoginResponse response = (LoginResponse) requestSendTask.getValue();

            if (response.successful) {
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setHeaderText("Login successful!");
                infoAlert.initOwner(currentStage);
                infoAlert.initModality(Modality.WINDOW_MODAL);
                infoAlert.showAndWait();

                currentStage.close();

                Stage timelineStage = new Stage();
                FXMLLoader timelineLoader = new FXMLLoader(getClass().getResource("/views/Timeline.fxml"));
                Parent timelineRoot;
                try {
                    timelineRoot = timelineLoader.load();
                } catch (IOException e) {
                    return;
                }

                Timeline timelineController = timelineLoader.getController();

                timelineController.setCredentials(username, password);

                timelineStage.setScene(new Scene(timelineRoot));
                timelineStage.setTitle(username + " - SBUGram");
                timelineStage.initModality(Modality.APPLICATION_MODAL);
                timelineStage.setResizable(false);

                timelineStage.show();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Login failed");
                errorAlert.setContentText(response.message);
                errorAlert.initOwner(currentStage);
                errorAlert.initModality(Modality.WINDOW_MODAL);
                errorAlert.show();
            }
        });

        new Thread(requestSendTask).start();
        loginButton.setDisable(true);
        loginButton.setText("Logging in...");
    }


    public void onSwitchSignUpButton(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        HBox rootOfScene = new FXMLLoader(getClass().getResource("/views/SignUp.fxml")).load();

        currentStage.setScene(new Scene(rootOfScene));
    }
}
