package org.javaproteam27.socialnetwork.handler.exception;

public class UnableCreateEntityException extends RuntimeException {
    public UnableCreateEntityException(String message) {
        super("Unable to create entity: " + message + ".");
    }
}
