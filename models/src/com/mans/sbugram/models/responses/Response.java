package com.mans.sbugram.models.responses;

import com.mans.sbugram.models.interfaces.JSONRepresentable;

public abstract class Response implements JSONRepresentable {

    public abstract ResponseType getResponseType();

}
