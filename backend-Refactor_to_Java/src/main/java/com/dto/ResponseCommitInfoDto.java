package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseCommitInfoDto {
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class WeekTotalData {
        private String week;
        private int total;
    }

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class DayCommit {
        private String day;
        private int commit;
    }

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class DayOfWeekData {
        private String week;
        private List<DayCommit> detailDatas;
    }

    private List<WeekTotalData> weekTotalData;
    private List<DayOfWeekData> dayOfWeekData;
}
