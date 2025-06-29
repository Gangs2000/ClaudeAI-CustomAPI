package com.claude.customapi.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CityInfoGlobalException {
    @ExceptionHandler(value = CityInfoException.class)
    public ResponseEntity<ExceptionTemplate> exceptionHandler(CityInfoException cityInfoException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .errorCode(String.valueOf(HttpStatus.BAD_REQUEST))
                .message(cityInfoException.getMessage())
                .time(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }
}
