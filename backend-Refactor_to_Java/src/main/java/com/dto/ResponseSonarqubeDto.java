package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseSonarqubeDto {

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class Measure {
        private String metric;      // 測量的對象 e.g. coverage
        private String value;       // 測量數值
        private String component;   // 組件
        private boolean bestValue;  // 過去測量出最好的數值
    }

    private List<Measure> measures; // 度量
    private String projectName;     // 專案名稱
}