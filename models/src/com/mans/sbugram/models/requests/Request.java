package com.mans.sbugram.models.requests;

import com.mans.sbugram.models.interfaces.JSONRepresentable;

public abstract class Request implements JSONRepresentable {

    public abstract RequestType getRequestType();

}
