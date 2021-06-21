package com.mans.sbugram.client.controllers.listcells;

import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.requests.FileDownloadRequest;
import com.mans.sbugram.models.responses.FileDownloadResponse;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64;

public class TimelineListCell extends ListCell<Post> {

    public TimelineListCell(ListView<Post> postListView) {
        super();

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

        Label usernameLabel;
        Label contentLabel;
        ImageView postPhotoImageView;
        VBox postTextVBox;

        try {
            postTextVBox = (VBox) root.lookup("#postTextVbox");
            usernameLabel = (Label) postTextVBox.lookup("#usernameLabel");
            contentLabel = (Label) postTextVBox.lookup("#contentLabel");
            postPhotoImageView = (ImageView) root.lookup("#postPhotoImageView");
        } catch (NullPointerException e) {
            return;
        }

        contentLabel.setText(newPost.content);
        usernameLabel.setText("@" + newPost.posterUsername);

        if (newPost.photoFilename.isEmpty()) {
            root.getChildren().remove(postPhotoImageView);
            postTextVBox.prefWidthProperty().bind(this.prefWidthProperty().subtract(20));
        } else {
            Socket serverConnectionSocket;
            try {
                serverConnectionSocket = new Socket("localhost", 8228);
            } catch (IOException e) {
                return;
            }
            RequestSendTask postPhotoDownloadRequestTask = new RequestSendTask(
                    new FileDownloadRequest(newPost.photoFilename),
                    serverConnectionSocket
            );

            postPhotoDownloadRequestTask.setOnSucceeded(workerStateEvent -> {
                String photoBlob = ((FileDownloadResponse) postPhotoDownloadRequestTask.getValue()).blob;
                ByteArrayInputStream photoBytesStream = new ByteArrayInputStream(Base64.getDecoder().decode(photoBlob));
                postPhotoImageView.setImage(
                        new Image(photoBytesStream)
                );
            });

            new Thread(postPhotoDownloadRequestTask).start();
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
