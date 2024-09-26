package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseContributorsActivityDto {

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class Author {
        private String login;
        private String avatar_url;
        private String html_url;
        private String email;
    }

    // ws - convert w to string date of week
    // w - Start of the week, given as a Unix timestamp.
    // a - Number of additions
    // d - Number of deletions
    // c - Number of commits
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class Week {
        private Date ws;
        private int w;
        private int a;
        private int d;
        private int c;
    }
    private Author author;
    private List<Week> weeks;
    private int total;
    private int totalAdditions;
    private int totalDeletions;
    private String commitsHtmlUrl;
}