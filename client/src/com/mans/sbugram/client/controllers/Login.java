package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.requests.LoginRequest;
import com.mans.sbugram.models.responses.LoginResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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


    public void onLoginButtonAction() {
        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        Socket serverConnectionSocket;
        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            statusLabel.setText("Connection to server error");
            return;
        }

        RequestSendTask requestSendTask = new RequestSendTask(
                new LoginRequest(username, password),
                serverConnectionSocket
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            LoginResponse response = (LoginResponse) requestSendTask.getValue();
            if (response.successful) {
                statusLabel.setText("Logged in!");
            } else {
                statusLabel.setText("Login failed: " + response.message);
            }
        });

        new Thread(requestSendTask).start();
    }


    public void onSwitchSignUpButton(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        HBox rootOfScene = new FXMLLoader(getClass().getResource("../views/SignUp.fxml")).load();

        currentStage.setScene(new Scene(rootOfScene));
    }
}
