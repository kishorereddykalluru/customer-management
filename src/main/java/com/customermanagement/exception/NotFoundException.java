package com.customermanagement.exception;

public class NotFoundException extends RuntimeException{

    private static final long serialVersionUID = 7267723629213241311L;

    public NotFoundException(String message){
        super(message);
    }
}
