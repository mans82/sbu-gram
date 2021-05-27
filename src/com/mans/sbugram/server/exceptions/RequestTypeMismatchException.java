package com.mans.sbugram.server.exceptions;

public class RequestTypeMismatchException extends Exception {

    public RequestTypeMismatchException(String expected) {
        super("Expected " + expected + ".");
    }
}
