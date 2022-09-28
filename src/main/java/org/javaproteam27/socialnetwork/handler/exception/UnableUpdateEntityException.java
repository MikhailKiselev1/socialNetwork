package org.javaproteam27.socialnetwork.handler.exception;

public class UnableUpdateEntityException extends RuntimeException {
    public UnableUpdateEntityException(String message) {
        super("Unable update entity " + message);
    }
}
