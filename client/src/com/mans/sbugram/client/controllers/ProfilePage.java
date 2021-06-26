package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.controllers.listcells.TimelineListCell;
import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.FileDownloadRequest;
import com.mans.sbugram.models.requests.SetFollowingRequest;
import com.mans.sbugram.models.requests.UserInfoRequest;
import com.mans.sbugram.models.requests.UserPostsRequest;
import com.mans.sbugram.models.responses.FileDownloadResponse;
import com.mans.sbugram.models.responses.SetFollowingResponse;
import com.mans.sbugram.models.responses.UserInfoResponse;
import com.mans.sbugram.models.responses.UserPostsResponse;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

public class ProfilePage implements Initializable {
    public VBox rootVBox;
    public HBox profileHeaderHBox;
    public ImageView profilePhotoImageView;
    public VBox userTextInfoVBox;
    public HBox nameHBox;
    public Label nameLabel;
    public Button followButton;
    public Label usernameLabel;
    public ImageView cityIconImageView;
    public Label cityLabel;
    public ListView<Post> postsListView;
    public Label bioLabel;

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

        Socket serverConnectionSocket;
        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot load user information");
            errorAlert.setContentText("Server connection failed");
            errorAlert.showAndWait();
            return;
        }

        RequestSendTask requestOwnerUserInfoSendTask = new RequestSendTask(
                new UserInfoRequest(this.profileOwnerUsername.getValue()),
                serverConnectionSocket
        );

        requestOwnerUserInfoSendTask.setOnSucceeded(workerStateEvent -> {
            UserInfoResponse response = (UserInfoResponse) requestOwnerUserInfoSendTask.getValue();

            this.onUserLoaded(response.successful ? response.user : null);
        });

        Thread requestSendThread = new Thread(requestOwnerUserInfoSendTask);
        requestSendThread.setDaemon(true);
        requestSendThread.start();



        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot load user information");
            errorAlert.setContentText("Server connection failed");
            errorAlert.showAndWait();
            return;
        }

        RequestSendTask requestLoggedInUserInfoSendTask = new RequestSendTask(
                new UserInfoRequest(this.username.getValue()),
                serverConnectionSocket
        );

        requestLoggedInUserInfoSendTask.setOnSucceeded(workerStateEvent -> {
            UserInfoResponse response = (UserInfoResponse) requestLoggedInUserInfoSendTask.getValue();

            this.onLoggedInUserLoaded(response.successful ? response.user : null);
        });

        Thread loggedInUserRequestSendThread = new Thread(requestLoggedInUserInfoSendTask);
        loggedInUserRequestSendThread.setDaemon(true);
        loggedInUserRequestSendThread.start();
    }

    private void onUserLoaded(User user) {
        if (user == null) {
            nameLabel.setText("This user does not exist");
            usernameLabel.setText("Make sure your spelling is correct.");
            userTextInfoVBox.getChildren().remove(cityLabel);
            userTextInfoVBox.getChildren().remove(bioLabel);
            rootVBox.getChildren().remove(postsListView);
            nameHBox.getChildren().remove(followButton);
            return;
        }

        postsListView.setCellFactory(postListView -> new TimelineListCell(postListView, username.getValue(), password.getValue()));

        nameLabel.setText(user.name);
        usernameLabel.setText("@" + user.username);

        if (this.profileOwnerUsername.getValue().equals(this.username.getValue())) {
            nameHBox.getChildren().remove(followButton);
        }

        if (!user.city.isEmpty()) {
            cityLabel.setText(user.city);
        } else {
            userTextInfoVBox.getChildren().remove(cityLabel);
        }

        if (!user.bio.isEmpty()) {
            bioLabel.setText(user.bio);
        } else {
            userTextInfoVBox.getChildren().remove(bioLabel);
        }

        Socket serverConnectionSocket;

        if (user.profilePhotoFilename.isEmpty()) {
            this.profilePhotoImageView.setImage(new Image("/icons/account.png"));
        } else {
            try {
                serverConnectionSocket = new Socket("localhost", 8228);
            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot load posts");
                errorAlert.setContentText("Server connection failed.");
                errorAlert.showAndWait();
                return;
            }

            RequestSendTask userPhotoDownloadRequestSendTask = new RequestSendTask(
                    new FileDownloadRequest(user.profilePhotoFilename),
                    serverConnectionSocket
            );

            userPhotoDownloadRequestSendTask.setOnSucceeded(workerStateEvent -> {
                FileDownloadResponse response = (FileDownloadResponse) userPhotoDownloadRequestSendTask.getValue();

                Image downloadedImage = new Image(new ByteArrayInputStream(Base64.getDecoder().decode(response.blob)));

                this.profilePhotoImageView.setImage(downloadedImage);
            });

            Thread userPhotoDownloadThread = new Thread(userPhotoDownloadRequestSendTask);
            userPhotoDownloadThread.setDaemon(true);
            userPhotoDownloadThread.start();
        }


        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot load posts");
            errorAlert.setContentText("Server connection failed.");
            errorAlert.showAndWait();
            return;
        }

        RequestSendTask userPostsRequestSendTask = new RequestSendTask(
                new UserPostsRequest(profileOwnerUsername.getValue()),
                serverConnectionSocket
        );

        userPostsRequestSendTask.setOnSucceeded(workerStateEvent -> {
            UserPostsResponse response = (UserPostsResponse) userPostsRequestSendTask.getValue();

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

        Thread requestSendThread = new Thread(userPostsRequestSendTask);
        requestSendThread.setDaemon(true);
        requestSendThread.start();
    }

    public void setCredentials(String profileOwnerUsername, String username, String password) {
        this.username.setValue(username);
        this.password.setValue(password);
        this.profileOwnerUsername.setValue(profileOwnerUsername);
    }

    private void onLoggedInUserLoaded(User loggedInUser) {
        if (loggedInUser == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot load logged in user info");
            errorAlert.showAndWait();
            return;
        }

        boolean isFollowing = loggedInUser.followingUsersUsernames.contains(this.profileOwnerUsername.getValue());
        if (isFollowing) {
            followButton.setText("Unfollow");
        } else {
            followButton.setText("Follow");
        }

        followButton.setUserData(isFollowing);
    }


    public void onFollowButtonAction() {
        boolean following = (Boolean) followButton.getUserData();

        Socket serverConnectionSocket;

        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Cannot load logged in user info");
            errorAlert.showAndWait();
            return;
        }

        RequestSendTask followRequestTask = new RequestSendTask(
                new SetFollowingRequest(this.username.getValue(), this.password.getValue(), this.profileOwnerUsername.getValue(), !following),
                serverConnectionSocket
        );

        followRequestTask.setOnSucceeded(workerStateEvent -> {
            SetFollowingResponse response = (SetFollowingResponse) followRequestTask.getValue();

            if (response.successful) {
                followButton.setText(response.following ? "Unfollow" : "Follow");
                followButton.setUserData(response.following);
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot change following state");
                errorAlert.setContentText(response.message);
                errorAlert.showAndWait();
            }
        });

        Thread followRequestSendThread = new Thread(followRequestTask);
        followRequestSendThread.setDaemon(true);
        followRequestSendThread.start();
    }
}
