package com.akvelon.cdp.endpoints;

import com.akvelon.cdp.exceptions.RequestNotFoundException;
import com.akvelon.cdp.exceptions.WebPageNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Contains methods to handle exception that are thrown in this application
 */
@ControllerAdvice
public class ApplicationExceptionsController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({WebPageNotFoundException.class})
    public ResponseEntity<Object> handleWebPageNotFoundException() {
        return new ResponseEntity<>("Requested Web Page was not found",
                                    new HttpHeaders(),
                                    HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RequestNotFoundException.class})
    public ResponseEntity<Object> handleRequestNotFoundException() {
        return new ResponseEntity<>("Internal request with specified id was not found",
                                    new HttpHeaders(),
                                    HttpStatus.NOT_FOUND);
    }
}
