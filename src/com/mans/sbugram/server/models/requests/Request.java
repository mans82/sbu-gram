package com.mans.sbugram.server.models.requests;

import com.mans.sbugram.server.models.interfaces.JSONRepresentable;

public abstract class Request implements JSONRepresentable {

    public abstract RequestType getRequestType();

}
