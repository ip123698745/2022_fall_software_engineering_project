package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CodeSmellDataDto {
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class Issues {
        private String key;
        private String severity;
        private String component;
        private int line;
        private String message;
    }

    private int total;
    private List<Issues> issues;
}