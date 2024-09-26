package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CommitsDto {

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class Stats {
        private int additions;
        private int deletions;
        private int total;
    }
    private String committer_name;
    private String committer_email;
    private Date committed_date;
    private Stats stats;
    private List<String> parent_ids;
}