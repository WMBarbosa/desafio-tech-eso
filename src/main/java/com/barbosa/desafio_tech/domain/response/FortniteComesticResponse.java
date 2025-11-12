package com.barbosa.desafio_tech.domain.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FortniteComesticResponse {

    private DataWrapper data;

    @Data
    public static class DataWrapper {
        private Map<String, List<Map<String, Object>>> items;
        private String date;
        private String build;
        private String previousBuild;
    }
}




