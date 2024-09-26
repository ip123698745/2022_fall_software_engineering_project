package com.impl;

import com.Application;
import com.bean.Jira;
import com.bean.Project;
import com.dao.JiraRepository;
import com.dao.ProjectRepository;
import com.dto.*;
import com.service.JiraService;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class JiraServiceImplTest {

    @SpyBean
    private JiraService jiraService;

    @MockBean
    private JiraRepository jiraRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private Calendar mockCalendar;

    @Test
    public void testGetJiraBoardDetailSuccess() {
        RequestJiraLoginDto stubRequestJiraLoginDto = new RequestJiraLoginDto("selab1623-pd",
                "r3YRMVLevx81HWHiBgEn6D18", "hung61601@gmail.com", 0, 0);
        String stubGetBoardResult = "{\"values\": [{\"id\": 1,\"name\": \"PD board\"},{\"id\": 2,\"name\": \"TFJA board\"}]}";

        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/agile/1.0/board"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(stubGetBoardResult));

        List<JiraBoardDetailDto> actualJiraBoardDetailDtos = jiraService.getJiraBoardDetail(stubRequestJiraLoginDto);

        List<JiraBoardDetailDto> expectedJiraBoardDetailDtos = List.of(
                new JiraBoardDetailDto(1,"PD board"),
                new JiraBoardDetailDto(2,"TFJA board")
        );

        Assert.assertEquals(expectedJiraBoardDetailDtos, actualJiraBoardDetailDtos);
    }

    @Test(expected = JSONException.class)
    public void testGetJiraBoardDetailFail() {
        RequestJiraLoginDto stubRequestJiraLoginDto = new RequestJiraLoginDto("", "", "", 0, 0);

        when(restTemplate.exchange(eq("https://.atlassian.net/rest/agile/1.0/board"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().body("{}"));

        jiraService.getJiraBoardDetail(stubRequestJiraLoginDto);
    }

    @Test
    public void testCreateRepoOfJiraSuccess() throws Exception {
        RequestJiraLoginDto stubRequestJiraLoginDto = new RequestJiraLoginDto("selab1623-pd", "r3YRMVLevx81HWHiBgEn6D18",
                "hung61601@gmail.com", 1, 0);
        List<JiraBoardDetailDto> stubJiraBoardDetailDtos = List.of(
                new JiraBoardDetailDto(1,"PD board"),
                new JiraBoardDetailDto(2,"TFJA board")
        );
        Jira dummyJira = new Jira();
        dummyJira.setUrl("selab1623-pd");
        dummyJira.setApiToken("r3YRMVLevx81HWHiBgEn6D18");
        dummyJira.setAccount("hung61601@gmail.com");
        dummyJira.setName("PD board");
        dummyJira.setId(1);

        Project project = new Project();
        project.setJiras(new ArrayList<>());

        doReturn(stubJiraBoardDetailDtos).when(jiraService).getJiraBoardDetail(stubRequestJiraLoginDto);
        when(jiraRepository.save(any(Jira.class))).thenReturn(dummyJira);
        when(projectRepository.getById(any(Long.class))).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        jiraService.createRepoOfJira(stubRequestJiraLoginDto);

        verify(jiraRepository, times(1)).save(any(Jira.class));
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRepoOfJiraFail() throws Exception {
        RequestJiraLoginDto stubRequestJiraLoginDto = new RequestJiraLoginDto("selab1623-pd", "r3YRMVLevx81HWHiBgEn6D18",
                "hung61601@gmail.com", 1, 0);
        List<JiraBoardDetailDto> stubJiraBoardDetailDtos = List.of(
                new JiraBoardDetailDto(1, "PD board"),
                new JiraBoardDetailDto(2, "TFJA board")
        );

        doReturn(stubJiraBoardDetailDtos).when(jiraService).getJiraBoardDetail(stubRequestJiraLoginDto);
        when(jiraRepository.save(any(Jira.class))).thenThrow(new IllegalArgumentException());
        jiraService.createRepoOfJira(stubRequestJiraLoginDto);
    }

    @Test
    public void testGetAllIssueByBoardIdSuccess(){
        Jira stubJira = new Jira("r3YRMVLevx81HWHiBgEn6D18", 1, "PD board", "hung61601@gmail.com");
        stubJira.setId(3);
        stubJira.setUrl("selab1623-pd");

        String stubGetAllIssueResult = "{\"issues\":[{\"key\":\"PD-18\",\"fields\":{\"issuetype\":{\"name\":\"Task\"}," +
                "\"resolution\":{\"name\":\"Done\"},\"created\":\"2021-10-20T17:27:06.187+0800\",\"priority\":{\"name\":" +
                "\"Medium\"},\"labels\":[\"Frontend\"],\"updated\":\"2021-11-19T15:03:13.895+0800\",\"status\":{\"name\"" +
                ":\"Done\"},\"summary\":\"身為開發者，我想新增commit圖表顯示方式(圓餅圖、柱狀圖)\"}},{\"key\":\"PD-19\",\"fie" +
                "lds\":{\"issuetype\":{\"name\":\"Subtask\"},\"resolution\":null,\"created\":\"2021-10-20T17:27:06.187+08" +
                "00\",\"priority\":{\"name\":\"Lowest\"},\"labels\":[\"Frontend\",\"JIRA\",\"Backend\"],\"updated\":\"202" +
                "1-11-19T15:03:13.895+0800\",\"status\":{\"name\":\"Done\"},\"summary\":\"身為開發者，我想設計Jira UI\"}}]}";

        List<ResponseJiraIssueDto> expectedIssues = List.of(
                new ResponseJiraIssueDto("身為開發者，我想新增commit圖表顯示方式(圓餅圖、柱狀圖)", "Task", "Done",
                        "Medium", "PD-18","Done", "2021-10-20T17:27:06.187+0800",
                        "2021-11-19T15:03:13.895+0800", List.of("Frontend"), 0, null, null
                ),
                new ResponseJiraIssueDto("身為開發者，我想設計Jira UI", "Subtask", "Done",
                        "Lowest", "PD-19","Unresolved", "2021-10-20T17:27:06.187+0800",
                        "2021-11-19T15:03:13.895+0800", List.of("Frontend", "JIRA", "Backend"), 0, null, null
                )
        );

        when(jiraRepository.getById(3L)).thenReturn(stubJira);
        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/agile/1.0/board/1/issue"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(stubGetAllIssueResult));

        List<ResponseJiraIssueDto> actualIssues = jiraService.getAllIssueByBoardId(3);
        Assert.assertEquals(expectedIssues, actualIssues);
    }

    @Test(expected = NullPointerException.class)
    public void testGetAllIssueByBoardIdFailedWhenAccessingDatabase(){
        when(jiraRepository.getById(anyLong())).thenReturn(null);
        jiraService.getAllIssueByBoardId(0);
        verify(restTemplate, times(0)).exchange(anyString(),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }

    @Test(expected=NullPointerException.class)
    public void testGetAllIssueByBoardIdFailedWhenRestTemplate(){
        Jira stubJira = new Jira("r3YRMVLevx81HWHiBgEn6D18", 3, "PD board", "hung61601@gmail.com");
        stubJira.setUrl("selab1623-pd");

        when(jiraRepository.getById(3L)).thenReturn(stubJira);
        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/agile/1.0/board/1/issue"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().body(null));

        jiraService.getAllIssueByBoardId(3);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testGetIssueBySprintIdSuccess(){
        Jira stubJira = new Jira("r3YRMVLevx81HWHiBgEn6D18", 1, "PD board", "hung61601@gmail.com");
        stubJira.setId(3);
        stubJira.setUrl("selab1623-pd");

        String stubGetIssueBySprintIdResult = "{\"issues\":[{\"key\":\"PD-61\",\"fields\":{\"issuetype\":" +
                "{\"name\":\"Task\"},\"priority\":{\"name\":\"High\",},\"labels\":[\"Refactor\"],\"customfield_10016" +
                "\":5.0,\"status\":{\"name\":\"Done\",},\"description\":\"JiraService\\n\\nRepositoryService\\n\\nController" +
                "\",\"summary\":\"身為開發者，我想將Jira Repository轉換為Java後的系統架構重構\",\"subtasks\":[{\"key\":\"PD-63\",\"fields\":" +
                "{\"summary\":\"改善RepoInfo.mapCommitsToWeeks時間複雜度使其<O(n^2)\",\"status\":{\"name\":\"Done\"}}},{\"key\":\"PD-64\"" +
                ",\"fields\":{\"summary\":\"將所有API連線方法改成RestTemplate\",\"status\":{\"name\":\"Done\"}}},{\"key\":\"PD-65\",\"fields\"" +
                ":{\"summary\":\"將restTemplate依賴注入方法重構\",\"status\":{\"name\":\"Done\"}}},{\"key\":\"PD-66\",\"fields\":{\"summary\":" +
                "\"統一test double命名style\",\"status\":{\"name\":\"Done\"}}}]}},{\"key\":\"PD-67\",\"fields\":{\"issuetype\":{\"name\":\"Task\"}" +
                ",\"priority\":{\"name\":\"Medium\"},\"labels\":[\"Refactor\"],\"customfield_10016\":5.0,\"status\":{\"name\":\"Done\"}," +
                "\"description\":\"支援可用http/https都可呼叫API\\n\\n將不安全的https權證改掉\",\"summary\":\"身為開發者，我想將http改為https\"," +
                "\"subtasks\":[]}}]}";

        List<ResponseJiraIssueDto> expectedSubTask = List.of(
//                由於 Int 型別不能給 Null ， 故此處改為 0
                new ResponseJiraIssueDto("改善RepoInfo.mapCommitsToWeeks時間複雜度使其<O(n^2)", null, "Done",
                        null, "PD-63",null, null,
                        null, null, 0, null, null
                ),
                new ResponseJiraIssueDto("將所有API連線方法改成RestTemplate", null, "Done",
                        null, "PD-64",null, null,
                        null, null, 0, null, null
                ),
                new ResponseJiraIssueDto("將restTemplate依賴注入方法重構", null, "Done",
                null, "PD-65",null, null,
                null, null, 0, null, null
                ),
                new ResponseJiraIssueDto("統一test double命名style", null, "Done",
                        null, "PD-66",null, null,
                        null, null, 0, null, null
                )
        );

        List<ResponseJiraIssueDto> expectedIssues = List.of(
                new ResponseJiraIssueDto("身為開發者，我想將Jira Repository轉換為Java後的系統架構重構", "Task", "Done",
                        "High", "PD-61",null, null,
                        null, List.of("Refactor"), 5,
                        "JiraService\n\nRepositoryService\n\nController", expectedSubTask
                ),
                new ResponseJiraIssueDto("身為開發者，我想將http改為https", "Task", "Done",
                        "Medium", "PD-67",null, null,
                        null, List.of("Refactor"), 5, "支援可用http/https都可呼叫API\n\n將不安全的https權證改掉", null
                )
        );

        when(jiraRepository.getById(3L)).thenReturn(stubJira);
        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/agile/1.0/sprint/20/issue"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(stubGetIssueBySprintIdResult));

        List<ResponseJiraIssueDto> actualIssues = jiraService.getIssueBySprintId(3, 20);
        Assert.assertEquals(expectedIssues, actualIssues);
    }

    @Test(expected = NullPointerException.class)
    public void testGetIssueBySprintIdFailedWhenAccessingDatabase(){
        when(jiraRepository.getById(anyLong())).thenReturn(null);
        jiraService.getIssueBySprintId(3, 20);
        verify(restTemplate, times(0)).exchange(anyString(),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }

    @Test(expected=NullPointerException.class)
    public void testGetIssueBySprintIdFailedWhenRestTemplate(){
        Jira stubJira = new Jira("r3YRMVLevx81HWHiBgEn6D18", 1, "PD board", "hung61601@gmail.com");
        stubJira.setId(3);
        stubJira.setUrl("selab1623-pd");

        when(jiraRepository.getById(3L)).thenReturn(stubJira);
        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/agile/1.0/sprint/20/issue"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().body(null));

        jiraService.getIssueBySprintId(3, 20);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testGetAllSprintByBoardIdSuccess(){
        Jira stubJira = new Jira("r3YRMVLevx81HWHiBgEn6D18", 1, "PD board", "hung61601@gmail.com");
        stubJira.setId(3);
        stubJira.setUrl("selab1623-pd");

        String stubResult = "{\"values\":[{\"id\": 4,\"name\":\"PD Sprint 1\",\"goal\":\"1. Jira UI 設計完成\\n2. 新增contribution的圓餅圖及柱狀圖\"}," +
                "{\"id\":6,\"name\":\"PD Sprint 2\",\"goal\":\"照預覽圖完成Jira前端 UI\"},{\"id\":8,\"name\":\"PD Sprint 3\",\"goal\":" +
                "\"完成Jira DB table、部分API、修改sprint頁面UI\"}]}";

        List<ResponseSprintDto> expectedSprints = List.of(
                new ResponseSprintDto(4,"PD Sprint 1","1. Jira UI 設計完成\n2. 新增contribution的圓餅圖及柱狀圖"),
                new ResponseSprintDto(6,"PD Sprint 2","照預覽圖完成Jira前端 UI"),
                new ResponseSprintDto(8,"PD Sprint 3","完成Jira DB table、部分API、修改sprint頁面UI")
        );

        when(jiraRepository.getById(3L)).thenReturn(stubJira);
        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/agile/1.0/board/1/sprint"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(stubResult));

        List<ResponseSprintDto> actualSprints = jiraService.getAllSprintByBoardId(3);
        Assert.assertEquals(expectedSprints, actualSprints);
    }

    @Test(expected = NullPointerException.class)
    public void testGetAllSprintByBoardIdFailedWhenAccessingDatabase(){
        when(jiraRepository.getById(anyLong())).thenReturn(null);
        jiraService.getAllSprintByBoardId(3);
        verify(restTemplate, times(0)).exchange(anyString(),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }

    @Test(expected=NullPointerException.class)
    public void testGetAllSprintByBoardIdFailedWhenRestTemplate(){
        Jira stubJira = new Jira("r3YRMVLevx81HWHiBgEn6D18", 1, "PD board", "hung61601@gmail.com");
        stubJira.setId(3);
        stubJira.setUrl("selab1623-pd");

        when(jiraRepository.getById(3L)).thenReturn(stubJira);
        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/agile/1.0/board/1/sprint"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().body(null));

        jiraService.getAllSprintByBoardId(3);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testGetBurndownChartSuccess(){
        Jira stubJira = new Jira("r3YRMVLevx81HWHiBgEn6D18", 1, "PD board", "hung61601@gmail.com");
        stubJira.setId(3);
        stubJira.setUrl("selab1623-pd");

        String stubResult = "{\"changes\":{\"1642004429000\":[{\"key\":\"PD-46\",\"column\":{\"notDone\":true,\"newStatus\":" +
                "\"10000\"}}],\"1653064821000\":[{\"key\":\"PD-46\",\"statC\":{\"newValue\":8.0}}],\"1654878600000\":" +
                "[{\"key\":\"PD-46\",\"added\":true}],\"1654878713000\":[{\"key\":\"PD-75\",\"column\":{\"notDone\":true," +
                "\"newStatus\":\"10000\"}}],\"1654878966000\":[{\"key\":\"PD-75\",\"statC\":{\"newValue\":5.0}}],\"1654878989000\"" +
                ":[{\"key\":\"PD-75\",\"added\":true}],\"1654878990000\":[{\"key\":\"PD-75\",\"column\":{\"notDone\":false}}]," +
                "\"1654879000000\":[{\"key\":\"PD-75\",\"added\":false}]},\"startTime\":1654878908760,\"endTime\":1656693396000}";

        List<ResponseBurndownChartDto> expectedResult = List.of(
                new ResponseBurndownChartDto("2022-06-10", 0),
                new ResponseBurndownChartDto("2022-06-10", 0),
                new ResponseBurndownChartDto("2022-06-10", 0),
                new ResponseBurndownChartDto("2022-06-10", 5),
                new ResponseBurndownChartDto("2022-06-10", 0),
                new ResponseBurndownChartDto("2022-06-20", 8),
                new ResponseBurndownChartDto("2022-07-01", 0)
        );

        when(jiraRepository.getById(3L)).thenReturn(stubJira);
        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/greenhopper/1.0/rapid/charts/scopechangeburndownchart?rapidViewId=1&sprintId=22"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(stubResult));

//        透過依賴注入的方式，將預期插入的 dto 時間設置好
        when(mockCalendar.getTimeInMillis()).thenReturn(1655654400000L);

        List<ResponseBurndownChartDto> actualResult = jiraService.getBurndownChart(3, 22);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetBurndownChartForCoverage() {
        Jira stubJira = new Jira("r3YRMVLevx81HWHiBgEn6D18", 1, "PD board", "hung61601@gmail.com");
        stubJira.setId(3);
        stubJira.setUrl("selab1623-pd");

        String stubResult = "{\"changes\":{\"1642004429000\":[{\"key\":\"PD-46\",\"column\":{\"notDone\":true,\"newStatus\":" +
                "\"10000\"}}],\"1653064821000\":[{\"key\":\"PD-46\",\"statC\":{\"newValue\":8.0}}],\"1654878600000\":" +
                "[{\"key\":\"PD-46\",\"added\":true}],\"1654878713000\":[{\"key\":\"PD-75\",\"column\":{\"notDone\":true," +
                "\"newStatus\":\"10000\"}}],\"1654878966000\":[{\"key\":\"PD-75\",\"statC\":{\"newValue\":5.0}}],\"1654878989000\"" +
                ":[{\"key\":\"PD-75\",\"added\":true}],\"1654878990000\":[{\"key\":\"PD-75\",\"column\":{\"notDone\":false}}]," +
                "\"1654879000000\":[{\"key\":\"PD-75\",\"added\":false}]},\"startTime\":1654879908760,\"endTime\":1656693396000}";

        List<ResponseBurndownChartDto> expectedResult = List.of(
                new ResponseBurndownChartDto("2022-06-10", 8),
                new ResponseBurndownChartDto("2022-06-20", 8),
                new ResponseBurndownChartDto("2022-07-01", 0)
        );

        when(jiraRepository.getById(3L)).thenReturn(stubJira);
        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/greenhopper/1.0/rapid/charts/scopechangeburndownchart?rapidViewId=1&sprintId=22"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(stubResult));

//        透過依賴注入的方式，將預期插入的 dto 時間設置好
        when(mockCalendar.getTimeInMillis()).thenReturn(1655654400000L);

        List<ResponseBurndownChartDto> actualResult = jiraService.getBurndownChart(3, 22);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test(expected = NullPointerException.class)
    public void testGetBurndownChartFailedWhenAccessingDatabase() {
        when(jiraRepository.getById(anyLong())).thenReturn(null);
        jiraService.getBurndownChart(3, 22);
        verify(restTemplate, times(0)).exchange(anyString(),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }

    @Test(expected = NullPointerException.class)
    public void testGetBurndownChartFailedWhenRestTemplate(){
        Jira stubJira = new Jira("r3YRMVLevx81HWHiBgEn6D18", 1, "PD board", "hung61601@gmail.com");
        stubJira.setUrl("selab1623-pd");

        when(jiraRepository.getById(3L)).thenReturn(stubJira);
        when(restTemplate.exchange(eq("https://selab1623-pd.atlassian.net/rest/greenhopper/1.0/rapid/charts/scopechangeburndownchart?rapidViewId=1&sprintId=22"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().body(null));

        jiraService.getBurndownChart(3, 22);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }
}