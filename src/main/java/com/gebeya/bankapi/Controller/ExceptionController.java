package com.gebeya.bankapi.Controller;

import com.gebeya.bankAPI.Exception.ErrorMessage;
import com.gebeya.bankAPI.Model.DTO.ErrorMessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(ErrorMessage.class)
    public ResponseEntity<ErrorMessageDTO> handleException(ErrorMessage ex)
    {
        ErrorMessageDTO errorObject = new ErrorMessageDTO(ex.getStatus(),ex.getMessage());

        return new ResponseEntity<ErrorMessageDTO>(errorObject,ex.getStatus());
    }
}