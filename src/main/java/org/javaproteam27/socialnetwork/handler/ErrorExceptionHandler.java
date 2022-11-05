package org.javaproteam27.socialnetwork.handler;

import liquibase.pro.packaged.T;
import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.handler.exception.*;
import org.javaproteam27.socialnetwork.model.dto.response.ErrorRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorExceptionHandler {

    private static final String INVALID_REQUEST = "invalid_request";
    
    @ExceptionHandler
    public ResponseEntity<ErrorRs> catchInvalidRequestException(InvalidRequestException e) {
        log.error(e.getMessage(), e);
        ErrorRs errorRs = ErrorRs.builder()
                .error(INVALID_REQUEST)
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorRs, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler
    public ResponseEntity<ErrorRs> catchEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        ErrorRs errorRs = ErrorRs.builder()
                .error(INVALID_REQUEST)
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorRs, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler
    public ResponseEntity<ErrorRs> catchUnableCreateEntityException(UnableCreateEntityException e) {
        log.error(e.getMessage(), e);
        ErrorRs errorRs = ErrorRs.builder()
                .error(INVALID_REQUEST)
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorRs, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler
    public ResponseEntity<ErrorRs> catchUnableUpdateEntityException(UnableUpdateEntityException e) {
        log.error(e.getMessage(), e);
        ErrorRs errorRs = ErrorRs.builder()
                .error(INVALID_REQUEST)
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorRs, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ResponseRs<T>> catchErrorException(ErrorException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ResponseRs<>(INVALID_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
