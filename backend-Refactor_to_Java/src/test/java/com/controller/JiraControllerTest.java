package com.controller;

import com.Application;
import com.dto.*;
import com.google.gson.Gson;
import com.service.JiraService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class JiraControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private JiraController jiraController;

    @MockBean
    private JiraService jiraService;

    @Before
    public void setUp() {
        this.mockMvc = standaloneSetup(this.jiraController).build();
    }

    @Test
    public void testGetBoardInfoIsOk() throws Exception {
        RequestJiraLoginDto stubRequestJiraLoginDto = new RequestJiraLoginDto("selab1623-pd", "r3YRMVLevx81HWHiBgEn6D18", "hung61601@gmail.com", 0, 0);
        List<JiraBoardDetailDto> dummyJiraBoardDetailDtos = List.of(
                new JiraBoardDetailDto(1, "PD board"),
                new JiraBoardDetailDto(2, "TFJA board"));

        String json = new Gson().toJson(stubRequestJiraLoginDto);

        when(this.jiraService.getJiraBoardDetail(stubRequestJiraLoginDto)).thenReturn(dummyJiraBoardDetailDtos);

        mockMvc.perform(post("/jira/boardInfo").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())  // 顯示回應結果訊息，僅人工查看，非必須
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBoardInfoIsBadRequest() throws Exception {
        RequestJiraLoginDto stubRequestJiraLoginDto = new RequestJiraLoginDto("selab1623-pd", "r3YRMVLevx81HWHiBgEn6D18", "hung61601@gmail.com", 0, 0);

        String json = new Gson().toJson(stubRequestJiraLoginDto);

        when(this.jiraService.getJiraBoardDetail(stubRequestJiraLoginDto)).thenThrow();

        mockMvc.perform(post("/jira/boardInfo").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateJiraRepoIsOk() throws Exception {
        RequestJiraLoginDto stubRequestJiraLoginDto = new RequestJiraLoginDto("selab1623-pd", "r3YRMVLevx81HWHiBgEn6D18", "hung61601@gmail.com", 0, 0);

        String json = new Gson().toJson(stubRequestJiraLoginDto);

        doNothing().when(this.jiraService).createRepoOfJira(stubRequestJiraLoginDto);

        mockMvc.perform(post("/jira/createRepo").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateJiraRepoIsBadRequest() throws Exception {
        RequestJiraLoginDto stubRequestJiraLoginDto = new RequestJiraLoginDto("selab1623-pd", "r3YRMVLevx81HWHiBgEn6D18", "hung61601@gmail.com", 0, 0);
        String json = new Gson().toJson(stubRequestJiraLoginDto);

        doThrow(Exception.class).when(this.jiraService).createRepoOfJira(stubRequestJiraLoginDto);

        mockMvc.perform(post("/jira/createRepo").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllIssueByBoardIdIsOk() throws Exception {
        String json = new Gson().toJson(new RequestRepoIdDto(3));

        when(this.jiraService.getAllIssueByBoardId(3)).thenReturn(any());
        mockMvc.perform(post("/jira/issue").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllIssueByBoardIdIsBadRequest() throws Exception {
        String json = new Gson().toJson(new RequestRepoIdDto(3));

        when(this.jiraService.getAllIssueByBoardId(anyInt())).thenThrow();
        mockMvc.perform(post("/jira/issue").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetIssueBySprintIdIsOk() throws Exception {
        String json = new Gson().toJson(new RequestIdDto(3, 20));
        List<ResponseJiraIssueDto> dummyResult = new ArrayList<>();

        when(this.jiraService.getIssueBySprintId(3, 20)).thenReturn(dummyResult);
        mockMvc.perform(post("/jira/issueInfo").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIssueBySprintIdIsBadRequest() throws Exception {
        String json = new Gson().toJson(new RequestIdDto(3, 20));

        when(this.jiraService.getIssueBySprintId(anyInt(), anyInt())).thenThrow();
        mockMvc.perform(post("/jira/issueInfo").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllSprintByBoardIdIsOk() throws Exception {
        String json = new Gson().toJson(new RequestIdDto(3, 20));
        List<ResponseSprintDto> dummyResult = new ArrayList<>();

        when(this.jiraService.getAllSprintByBoardId(3)).thenReturn(dummyResult);
        mockMvc.perform(post("/jira/sprint").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllSprintByBoardIdIsBadRequest() throws Exception {
        String json = new Gson().toJson(new RequestIdDto(3, 20));

        when(this.jiraService.getAllSprintByBoardId(anyInt())).thenThrow();
        mockMvc.perform(post("/jira/sprint").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetBurndownChartIsOk() throws Exception {
        String json = new Gson().toJson(new RequestIdDto(3, 22));
        List<ResponseBurndownChartDto> dummyResult = new ArrayList<>();

        when(this.jiraService.getBurndownChart(3, 22)).thenReturn(dummyResult);
        mockMvc.perform(post("/jira/burndownChart").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBurndownChartIsBadRequest() throws Exception {
        String json = new Gson().toJson(new RequestIdDto(3, 22));

        when(this.jiraService.getBurndownChart(3, 22)).thenThrow();
        mockMvc.perform(post("/jira/burndownChart").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }
}
// 參考：https://www.tpisoftware.com/tpu/articleDetails/1256