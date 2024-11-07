package com.ram.exception.handlers;

import com.ram.exception.ExceptionDto;
import com.ram.exception.VengaException;
import org.keycloak.common.VerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class VengaExceptionHandler {
    @ExceptionHandler(value = VengaException.class)
    public ResponseEntity<ExceptionDto> handleFileUploadingError(VengaException exception) {
        return ResponseEntity.status(BAD_REQUEST).body(new ExceptionDto(exception.getMessage(),BAD_REQUEST));
    }


}
