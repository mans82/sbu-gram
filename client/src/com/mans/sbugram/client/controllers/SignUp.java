package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.SignUpRequest;
import com.mans.sbugram.models.responses.SignUpResponse;
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

public class SignUp {

    public HBox rootHBox;
    public VBox titleVBox;
    public Label titleLabel;
    public Label statusLabel;
    public VBox inputVBox;
    public TextField usernameTextField;
    public PasswordField passwordPasswordField;
    public TextField nameTextField;
    public TextField cityTextField;
    public TextField bioTextField;
    public Button signUpButton;
    public Button switchLoginButton;

    public void onSignUpButtonAction() {
        String username = usernameTextField.getText();
        String name = nameTextField.getText();
        String password = passwordPasswordField.getText();
        String city = cityTextField.getText();
        String bio = cityTextField.getText();

        Socket serverConnection;
        try {
            serverConnection = new Socket("localhost", 8228);
        } catch (IOException e) {
            statusLabel.setText("Connection to server failed.");
            return;
        }

        RequestSendTask requestSendTask = new RequestSendTask(
                new SignUpRequest(new User(username, name, password, city, bio)),
                serverConnection
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            SignUpResponse response = (SignUpResponse) requestSendTask.getValue();

            if (response.successful) {
                statusLabel.setText("Signed Up successfully! Now you can login using your credentials.");
            } else {
                statusLabel.setText("Sign up failed: " + response.message);
            }

            signUpButton.setText("Sign Up");
            signUpButton.setDisable(false);
        });

        signUpButton.setText("Signing up...");
        signUpButton.setDisable(true);
        new Thread(requestSendTask).start();
    }

    public void onSwitchLoginButtonAction(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        HBox rootOfScene = new FXMLLoader(getClass().getResource("../views/Login.fxml")).load();

        currentStage.setScene(new Scene(rootOfScene));
    }
}
