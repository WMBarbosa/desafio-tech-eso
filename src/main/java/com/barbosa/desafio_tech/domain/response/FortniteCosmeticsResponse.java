package com.barbosa.desafio_tech.domain.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FortniteCosmeticsResponse {
    private int status;
    private List<Map<String, Object>> data;
}
