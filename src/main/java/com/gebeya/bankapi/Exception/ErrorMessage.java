package com.gebeya.bankapi.Exception;

import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;

import java.util.Date;

public class ErrorMessage extends RuntimeException {

    private static final long serialVersionUID = 1;

    private HttpStatus status;
    private String message;
    public ErrorMessage(HttpStatus status, String message)
    {
        super(message);
        this.status=status;
        this.message=message;
    }


    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}