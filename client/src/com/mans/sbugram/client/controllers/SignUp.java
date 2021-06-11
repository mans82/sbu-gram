package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.FileUploadRequest;
import com.mans.sbugram.models.requests.SignUpRequest;
import com.mans.sbugram.models.responses.FileUploadResponse;
import com.mans.sbugram.models.responses.SignUpResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;

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
    public HBox profilePhotoHBox;
    public ImageView profilePhotoImageView;
    public Button profilePhotoButton;

    public void onSignUpButtonAction(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String name = nameTextField.getText();
        String password = passwordPasswordField.getText();
        String city = cityTextField.getText();
        String bio = cityTextField.getText();
        String profilePhotoFilename = (String) profilePhotoImageView.getUserData();
        if (profilePhotoFilename == null) {
            profilePhotoFilename = "";
        }
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Socket serverConnection;
        try {
            serverConnection = new Socket("localhost", 8228);
        } catch (IOException e) {
            statusLabel.setText("Connection to server failed.");
            return;
        }

        RequestSendTask requestSendTask = new RequestSendTask(
                new SignUpRequest(new User(username, name, password, city, bio, profilePhotoFilename)),
                serverConnection
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            signUpButton.setText("Sign Up");
            signUpButton.setDisable(false);

            SignUpResponse response = (SignUpResponse) requestSendTask.getValue();

            if (response.successful) {
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setHeaderText("Signed up successfully!");
                infoAlert.setContentText("Now you can login using your credentials.");
                infoAlert.initOwner(currentStage);
                infoAlert.initModality(Modality.WINDOW_MODAL);
                infoAlert.show();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Sign up failed");
                errorAlert.setContentText(response.message);
                errorAlert.initOwner(currentStage);
                errorAlert.initModality(Modality.WINDOW_MODAL);
                errorAlert.show();
            }
        });

        signUpButton.setText("Signing up...");
        signUpButton.setDisable(true);
        new Thread(requestSendTask).start();
    }

    public void onSwitchLoginButtonAction(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        HBox rootOfScene = new FXMLLoader(getClass().getResource("../../../../../../resources/views/Login.fxml")).load();

        currentStage.setScene(new Scene(rootOfScene));
    }

    public void onProfilePhotoButtonAction(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a profile picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(currentStage);

        if (selectedFile != null) {
            Image loadedImage = new Image(selectedFile.toURI().toString());
            if (loadedImage.isError()) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot load image file");
                errorAlert.initModality(Modality.WINDOW_MODAL);
                errorAlert.initOwner(currentStage);
                errorAlert.show();
                return;
            }

            Socket serverConnection;
            try {
                serverConnection = new Socket("localhost", 8228);
            } catch (IOException e) {
                Alert errorAlert =  new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Server connection failed");
                errorAlert.initModality(Modality.WINDOW_MODAL);
                errorAlert.initOwner(currentStage);
                errorAlert.show();
                return;
            }

            RequestSendTask requestSendTask = new RequestSendTask(
                    new FileUploadRequest(Base64.getEncoder().encodeToString(Files.readAllBytes(selectedFile.toPath()))),
                    serverConnection
            );
            Thread requestSendTaskThread = new Thread(requestSendTask);

            requestSendTask.setOnSucceeded(workerStateEvent -> {
                this.signUpButton.setDisable(false);
                this.profilePhotoButton.setText("Choose...");

                FileUploadResponse response = (FileUploadResponse) requestSendTask.getValue();

                if (!response.successful) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Uploading profile failed");
                    errorAlert.setContentText(response.message);
                    errorAlert.initOwner(currentStage);
                    errorAlert.initModality(Modality.WINDOW_MODAL);
                    errorAlert.show();
                } else {
                    this.profilePhotoImageView.setImage(loadedImage);
                    this.profilePhotoImageView.setUserData(response.filename);
                }
            });

            requestSendTaskThread.start();
            this.signUpButton.setDisable(true);
            this.profilePhotoButton.setText("Uploading...");
        }


    }
}
