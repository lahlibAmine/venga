package com.ram.exception;

public class InvalidProfileException extends RuntimeException{
    public InvalidProfileException(String message){
        super(message);
    }
}
