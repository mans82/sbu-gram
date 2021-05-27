package com.mans.sbugram.server.events;

import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import com.mans.sbugram.server.exceptions.HandlerAlreadyExistsException;
import com.mans.sbugram.server.exceptions.UnhandledRequestTypeException;
import com.mans.sbugram.server.requests.Request;
import com.mans.sbugram.server.requests.RequestType;
import com.mans.sbugram.server.responses.Response;

import java.util.HashMap;
import java.util.Map;

public class EventManager {

    private final Map<RequestType, EventHandler> handlerMap = new HashMap<>();

    public void addEventHandler(EventHandler handler) throws HandlerAlreadyExistsException {
        if (this.handlerMap.containsKey(handler.getRequestType())) {
            throw new HandlerAlreadyExistsException();
        }
        this.handlerMap.put(handler.getRequestType(), handler);
    }

    public Response handleRequest(Request request) throws UnhandledRequestTypeException, RequestTypeMismatchException {
        if (!this.handlerMap.containsKey(request.getRequestType())) {
            throw new UnhandledRequestTypeException();
        }
        return this.handlerMap.get(request.getRequestType()).handleEvent(request);
    }
}
