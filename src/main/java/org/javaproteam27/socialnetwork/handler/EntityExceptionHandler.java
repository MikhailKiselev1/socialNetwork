package org.javaproteam27.socialnetwork.handler;

import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.handler.exception.UnableCreateEntityException;
import org.javaproteam27.socialnetwork.handler.exception.UnableUpdateEntityException;
import org.javaproteam27.socialnetwork.model.dto.response.ErrorRs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class EntityExceptionHandler {

    private final static String INVALID_REQUEST = "invalid_request";
    
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
}
