package com.mans.sbugram.client.controllers;

import com.mans.sbugram.client.controllers.listcells.TimelineListCell;
import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.requests.UserTimelineRequest;
import com.mans.sbugram.models.responses.UserTimelineResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    private String username;
    private String password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timelinePosts = FXCollections.observableArrayList();
        timelineListView.setItems(timelinePosts);

        timelineListView.setCellFactory(TimelineListCell::new);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void onRefreshButtonAction(ActionEvent actionEvent) {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Socket serverConnectionSocket;
        try {
            serverConnectionSocket = new Socket("localhost", 8228);
        } catch (IOException e) {
            Alert errorAlert =  new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Server connection failed");
            errorAlert.initModality(Modality.WINDOW_MODAL);
            errorAlert.initOwner(currentStage);
            errorAlert.show();
            return;
        }

        RequestSendTask timelineRequestTask = new RequestSendTask(
                new UserTimelineRequest(username, password, 0, 10),
                serverConnectionSocket
        );

        timelineRequestTask.setOnSucceeded(workerStateEvent -> {
            UserTimelineResponse response = ((UserTimelineResponse) timelineRequestTask.getValue());

            timelinePosts.clear();
            timelinePosts.addAll(response.timelinePosts);

            this.refreshButton.setText("Refresh");
            this.refreshButton.setDisable(false);
        });

        new Thread(timelineRequestTask).start();
        this.refreshButton.setText("Refreshing...");
        this.refreshButton.setDisable(true);
    }
}
