package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.controllers.listcells.TimelineListCell;
import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.client.utils.Utils;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.requests.UserTimelineRequest;
import com.mans.sbugram.models.responses.UserTimelineResponse;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Pair;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Timeline implements Initializable {

    public VBox rootVBox;
    public GridPane headerGridPane;
    public ImageView profilePhotoImageView;
    public Button refreshButton;
    public ListView<Post> timelineListView;

    private ObservableList<Post> timelinePosts;

    private final Property<Pair<String, String>> credentials = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timelinePosts = FXCollections.observableArrayList();
        timelineListView.setItems(timelinePosts);

        profilePhotoImageView.setImage(new Image("/icons/account.png"));
        credentials.addListener((observableValue, oldCredentials, newCredentials) -> {
            if (newCredentials == null) {
                return;
            }
            String username = newCredentials.getKey();
            try {
                Utils.loadUser(username, user -> {
                    if (!user.profilePhotoFilename.isEmpty()) {
                        try {
                            Utils.loadImageFromUploadedFile(profilePhotoImageView, user.profilePhotoFilename);
                        } catch (IOException e) {
                            profilePhotoImageView.setImage(new Image("/icons/error.png"));
                        }
                    }
                    timelineListView.setCellFactory(postListView -> new TimelineListCell(postListView, credentials.getValue().getKey(), credentials.getValue().getValue()));
                });
            } catch (IOException e) {
                e.printStackTrace();
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setHeaderText("Cannot load user data");
                warningAlert.setContentText("An error occurred while trying to load you user data. Please login to the app again to fix the issue.");
                warningAlert.showAndWait();
                return;
            }

            try {
                this.refreshButton.fire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setCredentials(String username, String password) {
        this.credentials.setValue(new Pair<>(username, password));
    }

    public void onRefreshButtonAction() {
        String username = this.credentials.getValue().getKey();
        String password = this.credentials.getValue().getValue();
        Socket serverConnectionSocket;
        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert =  new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Server connection failed");
            errorAlert.initModality(Modality.WINDOW_MODAL);
            errorAlert.show();
            return;
        }

        RequestSendTask timelineRequestTask = new RequestSendTask(
                new UserTimelineRequest(username, password, 0, 10),
                serverConnectionSocket
        );

        timelineRequestTask.setOnSucceeded(workerStateEvent -> {
            UserTimelineResponse response = ((UserTimelineResponse) timelineRequestTask.getValue());

            if (response.successful) {
                timelinePosts.clear();
                timelinePosts.addAll(response.timelinePosts);
            }

            this.refreshButton.setText("Refresh");
            this.refreshButton.setDisable(false);
        });

        new Thread(timelineRequestTask).start();
        this.refreshButton.setText("Refreshing...");
        this.refreshButton.setDisable(true);
    }
}
