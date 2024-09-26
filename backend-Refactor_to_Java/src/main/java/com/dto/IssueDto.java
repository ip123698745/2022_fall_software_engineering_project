package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class IssueDto {

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class Author {
        private String username;
        private String web_url;
    }
    // number of issue sequence
    private int iid;
    // url of issue website
    private String web_url;
    // title
    private String title;
    // user who proposed the issue
    private Author author;
    // state of issue
    private String state;
    // time of creation
    private Date created_at;
    // time of close
    private Date closed_at;
}
