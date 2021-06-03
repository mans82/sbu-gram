package com.mans.sbugram.client.tasks;

import com.mans.sbugram.models.factories.ResponseFactory;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.responses.Response;
import javafx.concurrent.Task;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Optional;

public class RequestSendTask extends Task<Response> {

    public final Request requestToSend;
    private final Socket socket;

    public RequestSendTask(Request requestToSend, Socket socket) {
        this.requestToSend = requestToSend;
        this.socket = socket;
    }

    @Override
    protected Response call() throws Exception{
        Optional<Response> receivedResponse;

        try {
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write(requestToSend.toJSON().toString());
            writer.flush();

            InputStreamReader reader = new InputStreamReader(socket.getInputStream());
            receivedResponse = ResponseFactory.getResponse(
                    new JSONObject(new JSONTokener(reader))
            );
        } catch (IOException e) {
            throw new Exception(e);
        }

        if (!receivedResponse.isPresent()) {
            updateMessage("Invalid response error");
            throw new Exception();
        }

        return receivedResponse.get();
    }
}
