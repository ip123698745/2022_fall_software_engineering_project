package com.controller;

import com.dto.*;
import com.service.JiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class JiraController {

    @Autowired
    private JiraService jiraService;

    // TODO 未完成，前後端接口有問題
    // 前端 : response.data.result.List
    // 後段 : response.data.List
    // 這六個 Interfaces 都是這個問題！

    @PostMapping("/jira/boardInfo")
    public ResponseEntity<ResponseBoardDto> getBoardInfo(@RequestBody RequestJiraLoginDto requestJiraLoginDto){
        try {
            return ResponseEntity.ok().body(
                    new ResponseBoardDto(true, jiraService.getJiraBoardDetail(requestJiraLoginDto))
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new ResponseBoardDto(false, null)
            );
        }
    }

    @PostMapping("/jira/createRepo")
    public ResponseEntity<ResponseDto> createJiraRepo(@RequestBody RequestJiraLoginDto requestJiraLoginDto){
        try {
            jiraService.createRepoOfJira(requestJiraLoginDto);
            return ResponseEntity.ok().body(
                    new ResponseDto(true,"Added Success")
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new ResponseDto(false, "Added Error:　"+ex.getMessage())
            );
        }
    }

    @PostMapping("/jira/issue")
    public ResponseEntity<List<ResponseJiraIssueDto>> getAllIssueByBoardId(@RequestBody RequestRepoIdDto requestRepoIdDto){
        try {
            return ResponseEntity.ok().body(
                    jiraService.getAllIssueByBoardId(requestRepoIdDto.getRepoId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    null
            );
        }
    }

    @PostMapping("/jira/issueInfo")
    public ResponseEntity<List<ResponseJiraIssueDto>> getIssueBySprintId(@RequestBody RequestIdDto requestIdDto){
        try {
            return ResponseEntity.ok().body(
                    jiraService.getIssueBySprintId(requestIdDto.getRepoId(), requestIdDto.getSprintId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    null
            );
        }
    }

    @PostMapping("/jira/sprint")
    public ResponseEntity<List<ResponseSprintDto>> getAllSprintByBoardId(@RequestBody RequestIdDto requestIdDto){
        try {
            return ResponseEntity.ok().body(
                    jiraService.getAllSprintByBoardId(requestIdDto.getRepoId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    null
            );
        }
    }

    // TODO : 前端顯示會有問題，需修正！
    @PostMapping("/jira/burndownChart")
    public ResponseEntity<List<ResponseBurndownChartDto>> getBurndownChart(@RequestBody RequestIdDto requestIdDto){
        try {
            return ResponseEntity.ok().body(
                    jiraService.getBurndownChart(requestIdDto.getRepoId(), requestIdDto.getSprintId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    null
            );
        }
    }
}