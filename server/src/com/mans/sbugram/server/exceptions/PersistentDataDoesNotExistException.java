package com.mans.sbugram.server.exceptions;

public class PersistentDataDoesNotExistException extends Exception {
    public PersistentDataDoesNotExistException(String dataName) {
        super(dataName + " not found");
    }
}
