package com.ram.exception.handlers;

import com.ram.exception.ExceptionDto;
import com.ram.exception.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import java.io.FileNotFoundException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = MultipartException.class)
    public ResponseEntity<ExceptionDto> handleFileUploadingError(Exception exception) {
        HttpStatus status = BAD_REQUEST;
        ExceptionDto customApiException = new ExceptionDto(exception.getMessage(), status);

        return new ResponseEntity<>(customApiException, status);
    }

    @ExceptionHandler(value = FileNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleFileNotFoundError(Exception exception) {
        HttpStatus status = INTERNAL_SERVER_ERROR;
        ExceptionDto customApiException = new ExceptionDto(exception.getMessage(), status);

        return new ResponseEntity<>(customApiException, status);
    }

    @ExceptionHandler(value = UserAlreadyExistException.class)
    public ResponseEntity<ExceptionDto> handleUserNotFoundError(UserAlreadyExistException exception) {
        HttpStatus status = NOT_FOUND;
        ExceptionDto customApiException = new ExceptionDto(exception.getMessage(), status);

        return new ResponseEntity<>(customApiException, status);
    }


}