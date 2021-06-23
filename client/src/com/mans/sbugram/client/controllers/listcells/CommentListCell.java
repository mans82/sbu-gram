package com.mans.sbugram.client.controllers.listcells;

import com.mans.sbugram.models.Comment;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class CommentListCell extends ListCell<Comment> {

    public CommentListCell(ListView<Comment> commentListCellListView) {}

    @Override
    protected void updateItem(Comment comment, boolean empty) {
        super.updateItem(comment, empty);
        if (comment == null || empty) {
            this.setGraphic(null);
            return;
        }

        HBox root;
        try {
            root = FXMLLoader.load(getClass().getResource("/views/CommentCell.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            this.setGraphic(null);
            return;
        }

        Label usernameLabel = (Label) root.lookup("#usernameLabel");
        Label contentLabel = (Label) root.lookup("#contentLabel");

        usernameLabel.setText("@" + comment.username);
        contentLabel.setText(comment.text);

        this.setGraphic(root);
    }
}
