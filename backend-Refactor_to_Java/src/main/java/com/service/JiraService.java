package com.service;

import com.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;


public interface JiraService extends BaseService {
    List<JiraBoardDetailDto> getJiraBoardDetail(RequestJiraLoginDto requestJiraLoginDto);

    void createRepoOfJira(RequestJiraLoginDto requestJiraLoginDto) throws Exception;

    List<ResponseJiraIssueDto> getAllIssueByBoardId(long repoId);

    List<ResponseJiraIssueDto> getIssueBySprintId(long repoId, int sprintId);

    List<ResponseSprintDto> getAllSprintByBoardId(long repoId);

    List<ResponseBurndownChartDto> getBurndownChart(long repoId, int sprintId);
}