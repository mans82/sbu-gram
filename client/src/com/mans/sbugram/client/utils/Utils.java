package com.mans.sbugram.client.utils;

import com.mans.sbugram.client.tasks.RequestSendTask;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.FileDownloadRequest;
import com.mans.sbugram.models.requests.UserInfoRequest;
import com.mans.sbugram.models.responses.FileDownloadResponse;
import com.mans.sbugram.models.responses.UserInfoResponse;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64;
import java.util.function.Consumer;

public class Utils {

    public static void loadImageFromUploadedFile(ImageView imageView, String filename) throws IOException {
        FileDownloadRequest fileDownloadRequest = new FileDownloadRequest(filename);
        RequestSendTask requestSendTask = new RequestSendTask(
                fileDownloadRequest,
                new Socket("localhost", 8228)
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            FileDownloadResponse response = (FileDownloadResponse) requestSendTask.getValue();
            if (response.successful) {
                byte[] decodedImageBytes;
                try {
                    decodedImageBytes = Base64.getDecoder().decode(response.blob);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    imageView.setImage(new Image(Utils.class.getResource("/icons/error.png").toString()));
                    return;
                }
                imageView.setImage(new Image(new ByteArrayInputStream(decodedImageBytes)));
            } else {
                imageView.setImage(new Image(Utils.class.getResource("/icons/error.png").toString()));
            }
        });

        Thread requestTaskThread = new Thread(requestSendTask);
        requestTaskThread.setDaemon(true);

        requestTaskThread.start();
    }

    public static void loadUser(String username, Consumer<User> consumer) throws IOException {
        UserInfoRequest userInfoRequest = new UserInfoRequest(username);
        RequestSendTask requestSendTask = new RequestSendTask(
                userInfoRequest,
                new Socket("localhost", 8228)
        );

        requestSendTask.setOnSucceeded(workerStateEvent -> {
            UserInfoResponse response = (UserInfoResponse) requestSendTask.getValue();

            if (response.successful) {
                consumer.accept(response.user);
            }
        });

        Thread requestTaskThread = new Thread(requestSendTask);
        requestTaskThread.setDaemon(true);

        requestTaskThread.start();
    }

}
