package com.mans.sbugram.server.events;

import com.mans.sbugram.server.requests.Request;
import com.mans.sbugram.server.requests.RequestType;
import com.mans.sbugram.server.responses.Response;

public interface EventHandler {

    Response handleEvent(Request request);

    RequestType getRequestType();

}
