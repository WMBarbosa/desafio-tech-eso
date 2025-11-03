package com.barbosa.desafio_tech.controller.controllerException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class ValidationError extends CustomError{

    private List<FieldMessage> fieldErrors = new ArrayList<>();

    public void addFieldError(String fieldError, String message) {
        fieldErrors.add(new FieldMessage(fieldError, message));
    }

}
