package eu.lindroos.taas.teams.service.service.controller;

import eu.lindroos.taas.teams.service.service.controller.exception.UnauthorizedException;
import eu.lindroos.taas.teams.service.service.security.exception.InvalidRightsException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by Andreas on 13.4.2019
 */
@ControllerAdvice
@Log4j2
public class GenericExceptionHandler {

    // Exception handlers: If Rest controller throw any of these exceptions, they will automatically end up here

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity handleUnauthorizedException(UnauthorizedException e) {
        log.warn("User tried to access unauthorized resource", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(InvalidRightsException.class)
    public ResponseEntity handleRightsException(InvalidRightsException e) {
        log.error("Invalid rights", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleError(Exception e) {
        log.error("Unhandled exception thrown", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
