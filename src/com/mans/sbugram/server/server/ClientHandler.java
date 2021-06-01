package com.mans.sbugram.server.server;

import com.mans.sbugram.server.events.EventManager;
import com.mans.sbugram.server.models.factories.RequestFactory;
import com.mans.sbugram.server.models.requests.Request;
import com.mans.sbugram.server.models.responses.Response;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;

class ClientHandler implements Runnable {

    private final InputStream socketInputStream;
    private final OutputStream socketOutputStream;
    private final EventManager eventManager;

    ClientHandler(InputStream socketInputStream, OutputStream socketOutputStream, EventManager eventManager) {
        this.socketInputStream  = socketInputStream;
        this.socketOutputStream = socketOutputStream;
        this.eventManager = eventManager;
    }

    @Override
    public void run() {
        JSONObject requestJsonObject = new JSONObject(new JSONTokener(socketInputStream));
        try {
            socketInputStream.close();
        } catch (IOException ignored) {}

        Optional<Request> incomingRequest = RequestFactory.getRequest(requestJsonObject);
        if (!incomingRequest.isPresent()) {
            try {
                socketOutputStream.close();
            } catch (IOException ignored) {}
            return;
        }

        Response generatedResponse;
        try {
            generatedResponse = this.eventManager.handleRequest(incomingRequest.get());
        } catch (Exception e) { // TODO send general error response
            try {
                socketOutputStream.close();
            } catch (IOException ignored) {}
            return;
        }

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.socketOutputStream)) {
            outputStreamWriter.write(generatedResponse.toJSON().toString());
            outputStreamWriter.flush();
        } catch (IOException ignored) {}
    }
}

