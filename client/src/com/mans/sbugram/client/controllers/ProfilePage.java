package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.controllers.listcells.TimelineListCell;
import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.client.utils.Utils;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.UserPostsRequest;
import com.mans.sbugram.models.responses.UserPostsResponse;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfilePage implements Initializable {
    public VBox rootVBox;
    public HBox profileHeaderHBox;
    public ImageView profilePhotoImageView;
    public VBox userTextInfoVBox;
    public Label nameLabel;
    public Label usernameLabel;
    public Label cityLabel;
    public ListView<Post> postsListView;

    private final Property<String> profileOwnerUsername = new SimpleStringProperty();
    private final Property<String> username = new SimpleStringProperty();
    private final Property<String> password = new SimpleStringProperty();

    private final ObservableList<Post> userPosts = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        profileOwnerUsername.addListener(this::updateElements);
        postsListView.setItems(userPosts);
    }

    private void updateElements(ObservableValue<? extends String> observableValue, String oldUsername, String newUsername) {
        if (newUsername == null) {
            return;
        }

        try {
            Utils.loadUser(newUsername, this::onUserLoaded);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot load user information");
            errorAlert.showAndWait();
        }
    }

    private void onUserLoaded(User user) {
        postsListView.setCellFactory(postListView -> new TimelineListCell(postListView, username.getValue(), password.getValue()));

        nameLabel.setText(user.name);
        usernameLabel.setText("@" + user.username);

        if (!user.city.isEmpty()) {
            cityLabel.setText(user.city);
        } else {
            userTextInfoVBox.getChildren().remove(cityLabel);
        }

        Socket serverConnectionSocket;
        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot load posts");
            errorAlert.setContentText("Server connection failed.");
            errorAlert.showAndWait();
            return;
        }

        RequestSendTask requestSendTask = new RequestSendTask(
                new UserPostsRequest(username.getValue()),
                serverConnectionSocket
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            UserPostsResponse response = (UserPostsResponse) requestSendTask.getValue();

            if (response.successful) {
                this.userPosts.clear();
                this.userPosts.addAll(response.posts);
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot load posts");
                errorAlert.setContentText(response.message);
                errorAlert.showAndWait();
            }
        });

        Thread requestSendThread = new Thread(requestSendTask);
        requestSendThread.setDaemon(true);
        requestSendThread.start();
    }

    public void setCredentials(String profileOwnerUsername, String username, String password) {
        this.username.setValue(username);
        this.password.setValue(password);
        this.profileOwnerUsername.setValue(profileOwnerUsername);
    }
}
