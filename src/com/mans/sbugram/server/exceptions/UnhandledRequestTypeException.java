package com.mans.sbugram.server.exceptions;

public class UnhandledRequestTypeException extends Exception {
    public UnhandledRequestTypeException() {
    }

    public UnhandledRequestTypeException(String s) {
        super(s);
    }
}
