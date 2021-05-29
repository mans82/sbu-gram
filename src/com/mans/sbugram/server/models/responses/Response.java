package com.mans.sbugram.server.models.responses;

import com.mans.sbugram.server.models.interfaces.JSONRepresentable;

public abstract class Response implements JSONRepresentable {

    public abstract ResponseType getResponseType();

}
