package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RepoIssuesDto {

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class User {
        // uesr name who proposed the issue
        private String login;
        // user url
        private String html_url;
    }
    // issue number
    private int number;
    // issue url
    private String html_url;
    /// issue title
    private String title;
    // uesr who proposed the issue
    private User user;
    // issue state
    private String state;
    // issue created datetime
    private String created_at;
    // issue closed datetime
    private String closed_at;
}
