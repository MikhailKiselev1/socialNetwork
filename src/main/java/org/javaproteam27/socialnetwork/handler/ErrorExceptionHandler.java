package org.javaproteam27.socialnetwork.handler;

import liquibase.pro.packaged.T;
import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.handler.exception.ErrorException;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorExceptionHandler {
    
    @ExceptionHandler
    public ResponseEntity<ResponseRs<T>> catchErrorException(ErrorException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ResponseRs<>("invalid_request", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
