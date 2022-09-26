package org.javaproteam27.socialnetwork.handler.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super("Entity with " + message + " not found.");
    }
}