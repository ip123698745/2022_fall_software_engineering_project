package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseIssuesDto {

    private String averageDealWithIssueTime;
    private List<RepoIssuesDto> openIssues;
    private List<RepoIssuesDto> closeIssues;
}