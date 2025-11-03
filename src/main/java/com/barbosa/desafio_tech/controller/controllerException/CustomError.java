package com.barbosa.desafio_tech.controller.controllerException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CustomError {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;


}
