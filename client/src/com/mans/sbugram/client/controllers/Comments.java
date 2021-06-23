package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.controllers.listcells.CommentListCell;
import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.Comment;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.requests.AddCommentRequest;
import com.mans.sbugram.models.responses.AddCommentResponse;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Comments implements Initializable {
    public Pane rootPane;
    public ListView<Comment> commentsListView;
    public TextField commentTextField;
    public Button sendButton;

    private final Property<Post> currentPost = new SimpleObjectProperty<>();
    private final Property<String> currentUsername = new SimpleStringProperty();
    private final Property<String> currentPassword = new SimpleStringProperty();
    private final ObservableList<Comment> comments = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        commentsListView.setItems(comments);
        commentsListView.setCellFactory(CommentListCell::new);
    }

    public void initializeData(Post post, String username, String password) {
        comments.addAll(post.comments);
        currentUsername.setValue(username);
        currentPassword.setValue(password);
        currentPost.setValue(post);
    }

    public void onSendButtonAction(ActionEvent actionEvent) {
        Socket serverConnectionSocket;
        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("An error occurred");
            errorAlert.setContentText("Server connection error");
            errorAlert.showAndWait();
            return;
        }

        Comment commentToSend = new Comment(currentUsername.getValue(), commentTextField.getText());

        RequestSendTask requestSendTask = new RequestSendTask(
                new AddCommentRequest(
                        currentUsername.getValue(),
                        currentPassword.getValue(),
                        currentPost.getValue().id,
                        commentToSend.text
                ),
                serverConnectionSocket
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            AddCommentResponse response = (AddCommentResponse) requestSendTask.getValue();

            if (response.successful) {
                this.comments.add(commentToSend);
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot send comment");
                errorAlert.setContentText(response.message);
                errorAlert.showAndWait();
            }

            commentTextField.setDisable(false);
            sendButton.setDisable(false);
        });

        Thread requestSendThread = new Thread(requestSendTask);
        requestSendThread.setDaemon(true);
        requestSendThread.start();

        commentTextField.setDisable(true);
        sendButton.setDisable(true);
    }
}
