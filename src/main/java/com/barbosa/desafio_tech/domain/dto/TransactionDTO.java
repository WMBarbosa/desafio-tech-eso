package com.barbosa.desafio_tech.domain.dto;

import com.barbosa.desafio_tech.domain.entities.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;
    private Type type;
    private Double amount;
    private String date;
    private String description;
}
