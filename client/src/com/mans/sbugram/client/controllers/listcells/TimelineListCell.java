package com.mans.sbugram.client.controllers.listcells;

import com.mans.sbugram.client.controllers.Comments;
import com.mans.sbugram.client.utils.Utils;
import com.mans.sbugram.models.Post;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

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
        HBox userInfoHBox;
        ImageView profilePhotoImageView;
        HBox buttonsHBox;
        Button commentsButton;

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
        } catch (NullPointerException e) {
            return;
        }

        titleLabel.setText(newPost.title);
        contentLabel.setText(newPost.content);
        usernameLabel.setText("@" + newPost.posterUsername);
        commentsButton.setText("Comments " + newPost.comments.size());

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
            });
        } catch (IOException e) {
            profilePhotoImageView.setImage(new Image("/icons/error_small.png"));
        }

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
