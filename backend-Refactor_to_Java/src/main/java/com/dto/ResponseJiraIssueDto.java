package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseJiraIssueDto {
    private String summary;
    private String type;
    private String status;
    private String priority;
    private String key;
    private String resolution;
    private String created;
    private String updated;
    private List<String> label;
    private int estimatePoint;
    private String description;
    private List<ResponseJiraIssueDto> subTasks;
}
