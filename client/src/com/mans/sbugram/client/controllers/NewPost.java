package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.requests.FileUploadRequest;
import com.mans.sbugram.models.requests.SendPostRequest;
import com.mans.sbugram.models.responses.FileUploadResponse;
import com.mans.sbugram.models.responses.SendPostResponse;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

public class NewPost {
    public VBox rootVBox;
    public TextArea postContentTextArea;
    public HBox photoHBox;
    public Button choosePhotoButton;
    public HBox footerButtonsHBox;
    public Button sendButton;
    public Button cancelButton;
    public TextField titleTextField;
    public ImageView postPhotoImageView;

    private Stage currentStage;
    private String username;
    private String password;
    private String uploadedPhotoFilename = "";

    public void onCancelButtonAction() {
        currentStage.close();
    }

    public void onSendPostButtonAction(ActionEvent actionEvent) {
        // Check fields
        if (this.titleTextField.getText().isEmpty() || this.postContentTextArea.getText().isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot send post");
            errorAlert.setContentText("Title and content should not be empty.");
            errorAlert.showAndWait();
            return;
        }

        Post postToSend = new Post(
                0,
                Instant.now().getEpochSecond(),
                titleTextField.getText(),
                postContentTextArea.getText(),
                uploadedPhotoFilename,
                username,
                Collections.emptySet(),
                Collections.emptySet()
        );

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setHeaderText("Do you want to send this post?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        }

        Socket serverConnectionSocket;
        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot send post");
            errorAlert.setContentText("Server connection failed.");
            errorAlert.showAndWait();
            return;
        }

        RequestSendTask requestSendTask = new RequestSendTask(
                new SendPostRequest(username, password, postToSend),
                serverConnectionSocket
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            SendPostResponse response = (SendPostResponse) requestSendTask.getValue();

            if (response.successful) {
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setHeaderText("Post has been sent.");
                infoAlert.show();
                currentStage.close();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot send post");
                errorAlert.setContentText(response.message);
                errorAlert.showAndWait();
                sendButton.setDisable(false);
                cancelButton.setDisable(false);
            }
        });

        Thread sendRequestThread = new Thread(requestSendTask);
        sendRequestThread.setDaemon(true);
        sendRequestThread.start();

        sendButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void onChoosePhotoButtonAction(ActionEvent actionEvent) {
        FileChooser photoFileChooser = new FileChooser();
        photoFileChooser.setTitle("Choose post photo");
        photoFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

        File chosenFile = photoFileChooser.showOpenDialog(currentStage);

        if (chosenFile == null) {
            return;
        }

        FileInputStream chosenFileIS;
        String imageBlob;
        try {
            chosenFileIS = new FileInputStream(chosenFile);
            imageBlob = Base64.getEncoder().encodeToString(Files.readAllBytes(chosenFile.toPath()));
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot load photo");
            errorAlert.setContentText("Failed to open photo file.");
            errorAlert.showAndWait();
            return;
        }

        postPhotoImageView.setImage(new Image(chosenFileIS));

        Socket serverConnectionSocket;
        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot load photo");
            errorAlert.setContentText("Server connection failed");
            errorAlert.showAndWait();
            return;
        }

        choosePhotoButton.setDisable(true);

        RequestSendTask requestSendTask = new RequestSendTask(
                new FileUploadRequest(imageBlob),
                serverConnectionSocket
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            FileUploadResponse response = (FileUploadResponse) requestSendTask.getValue();

            if (response.successful) {
                this.uploadedPhotoFilename = response.filename;
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot upload photo");
                errorAlert.setContentText(response.message);
                errorAlert.showAndWait();
            }

            choosePhotoButton.setDisable(false);
        });

        Thread sendRequestThread = new Thread(requestSendTask);
        sendRequestThread.setDaemon(true);
        sendRequestThread.start();
    }
}
