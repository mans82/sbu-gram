package com.mans.sbugram.server.events;

import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public interface EventHandler {

    Response handleEvent(Request request) throws RequestTypeMismatchException;

    RequestType getRequestType();

    default String formatDate(Instant instant) {
        return DateTimeFormatter
                .ofPattern("yyyy/MM/dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

}
