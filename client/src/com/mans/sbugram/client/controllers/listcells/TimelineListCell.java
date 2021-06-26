package com.mans.sbugram.client.controllers.listcells;

import com.mans.sbugram.client.controllers.Comments;
import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.client.utils.Utils;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.requests.RepostRequest;
import com.mans.sbugram.models.requests.SetLikeRequest;
import com.mans.sbugram.models.requests.UserInfoRequest;
import com.mans.sbugram.models.responses.RepostResponse;
import com.mans.sbugram.models.responses.SetLikeResponse;
import com.mans.sbugram.models.responses.UserInfoResponse;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TimelineListCell extends ListCell<Post> {

    private final String currentUsername;
    private final String currentPassword;

    public TimelineListCell(ListView<Post> postListView, String currentUsername, String currentPassword) {
        super();

        this.currentUsername = currentUsername;
        this.currentPassword = currentPassword;
        this.prefWidthProperty().bind(postListView.widthProperty().subtract(20));
        this.itemProperty().addListener(((observableValue, oldPost, newPost) -> {
            if (newPost != null) {
                this.updateCellNodes(newPost);
            }
        }));
    }

    private void updateCellNodes(Post newPost) {
        Pane root;
        try {
            root = FXMLLoader.load(getClass().getResource("/views/TimelineCell.fxml"));
        } catch (IOException e) {
            return;
        }

        Label nameLabel;
        Label usernameLabel;
        Label titleLabel;
        Label contentLabel;
        ImageView postPhotoImageView;
        VBox postTextVBox;
        Label repostedLabel;
        HBox userInfoHBox;
        ImageView profilePhotoImageView;
        HBox buttonsHBox;
        Button commentsButton;
        Button likeButton;
        Button repostButton;
        Label dateTimeLabel;

        try {
            postTextVBox = (VBox) root.lookup("#postTextVbox");
            userInfoHBox = (HBox) postTextVBox.lookup("#userInfoHBox");
            nameLabel = (Label) postTextVBox.lookup("#nameLabel");
            usernameLabel = (Label) postTextVBox.lookup("#usernameLabel");
            titleLabel = (Label) postTextVBox.lookup("#titleLabel");
            contentLabel = (Label) postTextVBox.lookup("#contentLabel");
            postPhotoImageView = (ImageView) root.lookup("#postPhotoImageView");
            profilePhotoImageView = (ImageView) userInfoHBox.lookup("#profilePhotoImageView");
            buttonsHBox = (HBox) postTextVBox.lookup("#buttonsHBox");
            commentsButton = (Button) buttonsHBox.lookup("#commentsButton");
            likeButton = (Button) buttonsHBox.lookup("#likeButton");
            repostButton = (Button) buttonsHBox.lookup("#repostButton");
            repostedLabel = (Label) postTextVBox.lookup("#repostedLabel");
            dateTimeLabel = (Label) postTextVBox.lookup("#dateTimeLabel");
        } catch (NullPointerException e) {
            return;
        }

        titleLabel.setText(newPost.title);
        contentLabel.setText(newPost.content);
        commentsButton.setText("Comments " + newPost.comments.size());

        boolean liked = newPost.likedUsersUsernames.contains(currentUsername);
        if (liked) {
            likeButton.setText("Liked " + newPost.likedUsersUsernames.size());
        } else {
            likeButton.setText("Like " + newPost.likedUsersUsernames.size());
        }

        if (newPost.isRepost) {
            repostedLabel.setText(newPost.posterUsername + " reposted");

            Socket serverSocketConnection;

            try {
                serverSocketConnection = new Socket("localhost", 8228);
            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot load original poster info");
                errorAlert.setContentText("Server connection failed");
                errorAlert.showAndWait();
                return;
            }

            RequestSendTask originalUserInfoRequestTask = new RequestSendTask(
                    new UserInfoRequest(newPost.originalPosterUsername),
                    serverSocketConnection
            );

            originalUserInfoRequestTask.setOnSucceeded(workerStateEvent -> {
                UserInfoResponse response = (UserInfoResponse) originalUserInfoRequestTask.getValue();

                if (response.successful) {
                    try {
                        Utils.loadImageFromUploadedFile(profilePhotoImageView, response.user.profilePhotoFilename);
                    } catch (IOException e) {
                        profilePhotoImageView.setImage(new Image("/icons/error_small.png"));
                    }

                    nameLabel.setText(response.user.name);
                    usernameLabel.setText("@" + response.user.username);
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Cannot load original poster info");
                    errorAlert.setContentText(response.message);
                    errorAlert.showAndWait();
                }
            });

            Thread userInfoRequestThread = new Thread(originalUserInfoRequestTask);
            userInfoRequestThread.setDaemon(true);
            userInfoRequestThread.start();
        } else {
            postTextVBox.getChildren().remove(repostedLabel);
            try {
                Utils.loadUser(newPost.posterUsername, user -> {
                    if (!user.profilePhotoFilename.isEmpty()) {
                        try {
                            Utils.loadImageFromUploadedFile(profilePhotoImageView, user.profilePhotoFilename);
                        } catch (IOException e) {
                            profilePhotoImageView.setImage(new Image("/icons/error_small.png"));
                        }
                    } else {
                        profilePhotoImageView.setImage(new Image("/icons/account.png"));
                    }

                    nameLabel.setText(user.name);
                    usernameLabel.setText("@" + user.username);
                });
            } catch (IOException e) {
                profilePhotoImageView.setImage(new Image("/icons/error_small.png"));
            }
        }

        commentsButton.setOnAction(actionEvent -> {
            Stage commentsStage = new Stage();

            FXMLLoader commentsLoader = new FXMLLoader(getClass().getResource("/views/Comments.fxml"));
            Scene commentsScene;
            try {
                commentsScene = new Scene(commentsLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            ((Comments) commentsLoader.getController()).initializeData(newPost, currentUsername, currentPassword);

            commentsStage.setScene(commentsScene);
            commentsStage.setTitle("Comments");

            commentsButton.setDisable(true);
            commentsStage.showAndWait();
            commentsButton.setDisable(false);
        });

        likeButton.setOnAction(actionEvent -> {
            Socket serverConnectionSocket;
            try {
                serverConnectionSocket = new Socket("localhost", 8228);
            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot like post");
                errorAlert.setContentText("Server connection failed");
                errorAlert.showAndWait();
                return;
            }

            RequestSendTask likeRequestSendTask = new RequestSendTask(
                    new SetLikeRequest(currentUsername, currentPassword, newPost.id, !liked),
                    serverConnectionSocket
            );

            likeRequestSendTask.setOnSucceeded(workerStateEvent -> {
                SetLikeResponse response = (SetLikeResponse) likeRequestSendTask.getValue();

                if (response.successful) {
                    if (response.liked) {
                        likeButton.setText("Liked " + (newPost.likedUsersUsernames.size() + 1));
                    } else {
                        likeButton.setText("Like " + (newPost.likedUsersUsernames.size() - 1));
                    }
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Cannot like post");
                    errorAlert.setContentText(response.message);
                    errorAlert.showAndWait();
                }
            });

            Thread likeRequestThread = new Thread(likeRequestSendTask);
            likeRequestThread.setDaemon(true);
            likeRequestThread.start();
        });

        repostButton.setOnAction(actionEvent -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setHeaderText("Do you want to repost this post?");
            Optional<ButtonType> result =  confirmationAlert.showAndWait();
            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return;
            }

            Socket serverConnectionSocket;

            try {
                serverConnectionSocket = new Socket("localhost", 8228);
            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Cannot repost");
                errorAlert.setContentText("Server connection failed");
                errorAlert.showAndWait();
                return;
            }

            RequestSendTask repostRequestTask = new RequestSendTask(
                    new RepostRequest(currentUsername, currentPassword, newPost.id),
                    serverConnectionSocket
            );

            repostRequestTask.setOnSucceeded(workerStateEvent -> {
                RepostResponse response = (RepostResponse) repostRequestTask.getValue();

                if (response.successful) {
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setHeaderText("Reposted successfully");
                    infoAlert.showAndWait();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Cannot repost");
                    errorAlert.setContentText(response.message);
                    errorAlert.showAndWait();
                }
            });

            Thread repostRequestThread = new Thread(repostRequestTask);
            repostRequestThread.setDaemon(true);
            repostRequestThread.start();
        });

        dateTimeLabel.setText(
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(newPost.postedTime))
        );

        if (newPost.photoFilename.isEmpty()) {
            root.getChildren().remove(postPhotoImageView);
            postTextVBox.prefWidthProperty().bind(this.prefWidthProperty().subtract(20));
        } else {
            try {
                Utils.loadImageFromUploadedFile(postPhotoImageView, newPost.photoFilename);
            } catch (IOException e) {
                postPhotoImageView.setImage(new Image("/icons/error_small.png"));
            }
        }

        this.setGraphic(root);
    }

    @Override
    protected void updateItem(Post post, boolean empty) {
        super.updateItem(post, empty);

        if (post == null || empty) {
            this.setGraphic(null); // Possibly incomplete
        }
    }
}
