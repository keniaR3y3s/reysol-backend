package com.revoktek.reysol.controllers;

import com.revoktek.reysol.core.exceptions.NotFoundException;
import com.revoktek.reysol.core.exceptions.ServiceLayerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@ControllerAdvice
@RestControllerAdvice
public class ExceptionController {


    @ExceptionHandler(value = {ServiceLayerException.class})
    public ResponseEntity<Map<String, String>> runtimeException(ServiceLayerException ex) {
        return ResponseEntity.badRequest().body(getMessage(ex));
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Map<String, String>> runtimeNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMessage(ex));
    }

    private Map<String, String> getMessage(Exception ex) {
        return Collections.singletonMap("message", ex.getMessage());
    }


}
