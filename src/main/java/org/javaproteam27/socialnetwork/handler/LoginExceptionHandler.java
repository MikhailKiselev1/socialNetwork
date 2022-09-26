package org.javaproteam27.socialnetwork.handler;

import liquibase.pro.packaged.T;
import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class LoginExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ListResponseRs<T>> catchInvalidRequestException(InvalidRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ListResponseRs<>("invalid_request", null, null,
                null, null, null, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ListResponseRs<T>> catchEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ListResponseRs<>("invalid_request", null, null,
                null, null, null, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
