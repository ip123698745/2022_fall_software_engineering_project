package com.impl;

import com.Application;
import com.bean.GitHub;
import com.bean.Repo;
import com.dao.GitHubRepository;
import com.dto.*;
import com.service.RepoInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class GithubInfoServiceTest {

    @Autowired
    @Qualifier("githubInfoService")
    private RepoInfoService githubInfoService;

    @MockBean
    private GitHubRepository gitHubRepository;

    @SpyBean
    private RestTemplate restTemplate;

    @Test
    public void testRequestContributorsActivitySuccess() {
        Repo stubRepo = new GitHub();
        stubRepo.setOwner("shiftwave00");
        stubRepo.setName("SE_Project_Backend");
        List<ResponseContributorsActivityDto> stubContributors = List.of(
                new ResponseContributorsActivityDto(
                        new ResponseContributorsActivityDto.Author(
                                "109598023",
                                "https://avatars.githubusercontent.com/u/70119979?v=4",
                                "https://github.com/109598023",
                                null),
                        List.of(new ResponseContributorsActivityDto.Week(null, 1604188800, 0, 0, 0),
                                new ResponseContributorsActivityDto.Week(null, 1604793600, 200, 50, 1),
                                new ResponseContributorsActivityDto.Week(null, 1605398400, 0, 0, 0),
                                new ResponseContributorsActivityDto.Week(null, 1606003200, 100, 70, 2),
                                new ResponseContributorsActivityDto.Week(null, 1606608000, 0, 0, 0)),
                        3, 0, 0, null
                ),
                new ResponseContributorsActivityDto(
                        new ResponseContributorsActivityDto.Author(
                                "zxjte9411",
                                "https://avatars.githubusercontent.com/u/31059035?v=4",
                                "https://github.com/zxjte9411",
                                null),
                        List.of(new ResponseContributorsActivityDto.Week(null, 1604188800, 10, 50, 1),
                                new ResponseContributorsActivityDto.Week(null, 1604793600, 20, 40, 2),
                                new ResponseContributorsActivityDto.Week(null, 1605398400, 30, 30, 3),
                                new ResponseContributorsActivityDto.Week(null, 1606003200, 40, 20, 4),
                                new ResponseContributorsActivityDto.Week(null, 1606608000, 50, 0, 5)),
                        15, 0, 0, null
                ),
                new ResponseContributorsActivityDto(
                        new ResponseContributorsActivityDto.Author(
                                "Jing-Xun-Lin",
                                "https://avatars.githubusercontent.com/u/37148109?v=4",
                                "https://github.com/Jing-Xun-Lin",
                                null),
                        List.of(new ResponseContributorsActivityDto.Week(null, 1604188800, 0, 0, 0),
                                new ResponseContributorsActivityDto.Week(null, 1604793600, 0, 0, 0),
                                new ResponseContributorsActivityDto.Week(null, 1605398400, 0, 0, 0),
                                new ResponseContributorsActivityDto.Week(null, 1606003200, 0, 0, 0),
                                new ResponseContributorsActivityDto.Week(null, 1606608000, 0, 0, 0)),
                        0, 0, 0, null
                )
        );

        doReturn(ResponseEntity.ok().body(stubContributors)).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));

        List<ResponseContributorsActivityDto> actualDto = githubInfoService.requestContributorsActivity(stubRepo);
        Assert.assertEquals("zxjte9411", actualDto.get(0).getAuthor().getLogin());
        Assert.assertEquals(150, actualDto.get(0).getTotalAdditions());
        Assert.assertEquals(140, actualDto.get(0).getTotalDeletions());
        Assert.assertEquals("https://github.com/shiftwave00/SE_Project_Backend/commits?author=zxjte9411", actualDto.get(0).getCommitsHtmlUrl());
        Assert.assertEquals("109598023", actualDto.get(1).getAuthor().getLogin());
        Assert.assertEquals(300, actualDto.get(1).getTotalAdditions());
        Assert.assertEquals(120, actualDto.get(1).getTotalDeletions());
        Assert.assertEquals("https://github.com/shiftwave00/SE_Project_Backend/commits?author=109598023", actualDto.get(1).getCommitsHtmlUrl());
        Assert.assertEquals("Jing-Xun-Lin", actualDto.get(2).getAuthor().getLogin());
        Assert.assertEquals(0, actualDto.get(2).getTotalAdditions());
        Assert.assertEquals(0, actualDto.get(2).getTotalDeletions());
        Assert.assertEquals("https://github.com/shiftwave00/SE_Project_Backend/commits?author=Jing-Xun-Lin", actualDto.get(2).getCommitsHtmlUrl());
    }

    @Test(expected = NullPointerException.class)
    public void testRequestContributorsActivityFail() {
        Repo stubRepo = new GitHub();
        stubRepo.setOwner("shiftwave00");
        stubRepo.setName("SE_Project_Backend");
        doReturn(ResponseEntity.ok().body(null)).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));
        githubInfoService.requestContributorsActivity(stubRepo);
    }

    @Test
    public void testRequestIssueSuccess() {
        Repo stubRepo =  new GitHub();
        stubRepo.setOwner("google");
        stubRepo.setName("googletest");
        List<RepoIssuesDto> stubOpenIssuesPage1 = List.of(
                new RepoIssuesDto(1, null, null, null, "open", "2022-08-05T05:39:03Z", null),
                new RepoIssuesDto(2, null, null, null, "open", "2022-08-02T15:39:03Z", null),
                new RepoIssuesDto(3, null, null, null, "open", "2022-07-16T10:00:00Z", null)
        );
        List<RepoIssuesDto> stubOpenIssuesPage2 = List.of(
                new RepoIssuesDto(4, null, null, null, "open", "2022-07-12T12:00:00Z", null),
                new RepoIssuesDto(5, null, null, null, "open", "2022-07-11T10:00:00Z", null)
        );
        List<RepoIssuesDto> stubClosedIssuesPage1 = List.of(
                new RepoIssuesDto(6, null, null, null, "closed", "2022-07-15T05:00:00Z", "2022-07-16T05:00:00Z"),
                new RepoIssuesDto(7, null, null, null, "closed", "2022-07-14T05:00:00Z", "2022-07-14T08:00:00Z"),
                new RepoIssuesDto(8, null, null, null, "closed", "2022-07-13T05:00:00Z", "2022-07-15T08:00:00Z")
        );
        HttpHeaders responseHeaders1 = new HttpHeaders();
        responseHeaders1.set("Link", "<https://url>; rel=\"next\", <https://url>; rel=\"last\"");
        HttpHeaders responseHeaders2 = new HttpHeaders();
        responseHeaders2.set("Link", "<https://url>; rel=\"prev\", <https://url>; rel=\"first\"");
        HttpHeaders responseHeaders3 = new HttpHeaders();

        doReturn(ResponseEntity.ok().headers(responseHeaders1).body(stubOpenIssuesPage1),
                ResponseEntity.ok().headers(responseHeaders2).body(stubOpenIssuesPage2),
                ResponseEntity.ok().headers(responseHeaders3).body(stubClosedIssuesPage1))
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));
        ResponseIssuesDto actualDto = githubInfoService.requestIssue(stubRepo);

        Assert.assertEquals(5, actualDto.getOpenIssues().size());
        Assert.assertEquals(3, actualDto.getCloseIssues().size());
        Assert.assertEquals("1 Day(s) 2 Hour(s) 0 Minute(s) 0 Seconds", actualDto.getAverageDealWithIssueTime());
    }

    @Test
    public void testRequestIssueEmpty() {
        Repo stubRepo =  new GitHub();
        stubRepo.setOwner("google");
        stubRepo.setName("googletest");
        doReturn(ResponseEntity.ok().body(List.of())).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));
        ResponseIssuesDto actualDto = githubInfoService.requestIssue(stubRepo);
        Assert.assertTrue(actualDto.getOpenIssues().isEmpty());
        Assert.assertTrue(actualDto.getCloseIssues().isEmpty());
        Assert.assertEquals("No Data", actualDto.getAverageDealWithIssueTime());
    }

    @Test(expected = RuntimeException.class)
    public void testRequestIssueRuntimeParseException() {
        Repo stubRepo =  new GitHub();
        stubRepo.setOwner("google");
        stubRepo.setName("googletest");
        List<RepoIssuesDto> stubOpenIssuesPage = List.of(
                new RepoIssuesDto(1, null, null, null, "open", "2022-08-05T05:39:03Z", null)
        );
        List<RepoIssuesDto> stubClosedIssuesPage = List.of(
                new RepoIssuesDto(1, null, null, null, "closed", "errorTimeFormat", "errorTimeFormat")
        );
        HttpHeaders responseHeaders = new HttpHeaders();
        doReturn(ResponseEntity.ok().headers(responseHeaders).body(stubOpenIssuesPage),
                ResponseEntity.ok().headers(responseHeaders).body(stubClosedIssuesPage))
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));
        githubInfoService.requestIssue(stubRepo);
    }

    @Test
    public void testRequestCommitSuccess() {
        Repo stubRepo =  new GitHub();
        stubRepo.setOwner("shiftwave00");
        stubRepo.setName("SE_Project_Backend");
        List<CommitInfoDto> stubCommitInfo = List.of(
                new CommitInfoDto(1657411200, List.of(0, 0, 0, 0, 0, 1, 0), 1),
                new CommitInfoDto(1658016000, List.of(0, 2, 0, 0, 0, 0, 0), 2),
                new CommitInfoDto(1658620800, List.of(0, 0, 0, 0, 0, 0, 0), 0),
                new CommitInfoDto(1659225600, List.of(0, 0, 0, 0, 0, 0, 0), 0),
                new CommitInfoDto(1659830400, List.of(0, 0, 1, 0, 0, 0, 3), 4)
        );
        ResponseCommitInfoDto expectedDto = new ResponseCommitInfoDto(
                List.of(new ResponseCommitInfoDto.WeekTotalData("2022-07-10", 1),
                        new ResponseCommitInfoDto.WeekTotalData("2022-07-17", 2),
                        new ResponseCommitInfoDto.WeekTotalData("2022-07-24", 0),
                        new ResponseCommitInfoDto.WeekTotalData("2022-07-31", 0),
                        new ResponseCommitInfoDto.WeekTotalData("2022-08-07", 4)
                ),
                List.of(new ResponseCommitInfoDto.DayOfWeekData("2022-07-10", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 0),
                                new ResponseCommitInfoDto.DayCommit("Monday", 0),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 1),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 0)
                        )),
                        new ResponseCommitInfoDto.DayOfWeekData("2022-07-17", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 0),
                                new ResponseCommitInfoDto.DayCommit("Monday", 2),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 0),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 0)
                        )),
                        new ResponseCommitInfoDto.DayOfWeekData("2022-07-24", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 0),
                                new ResponseCommitInfoDto.DayCommit("Monday", 0),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 0),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 0)
                        )),
                        new ResponseCommitInfoDto.DayOfWeekData("2022-07-31", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 0),
                                new ResponseCommitInfoDto.DayCommit("Monday", 0),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 0),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 0)
                        )),
                        new ResponseCommitInfoDto.DayOfWeekData("2022-08-07", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 0),
                                new ResponseCommitInfoDto.DayCommit("Monday", 0),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 1),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 0),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 3)
                        )))
        );

        doReturn(ResponseEntity.ok().body(stubCommitInfo)).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));

        ResponseCommitInfoDto actualDto = githubInfoService.requestCommit(stubRepo);
        Assert.assertEquals(expectedDto, actualDto);
    }

    @Test
    public void testRequestCodebaseSuccess() {
        Repo stubRepo =  new GitHub();
        stubRepo.setOwner("shiftwave00");
        stubRepo.setName("SE_Project_Backend");
        List<List<Integer>> stubCodebase = List.of(
                List.of(1657411200, 100, 0),
                List.of(1658016000, 370, -220),
                List.of(1658620800, 0, 0),
                List.of(1659225600, 0, 0),
                List.of(1659830400, 30, 0)
        );
        List<ResponseCodebaseDto> expectedDto = List.of(
                new ResponseCodebaseDto("2022-07-10", 100, 0, 100),
                new ResponseCodebaseDto("2022-07-17", 370, -220, 250),
                new ResponseCodebaseDto("2022-07-24", 0, 0, 250),
                new ResponseCodebaseDto("2022-07-31", 0, 0, 250),
                new ResponseCodebaseDto("2022-08-07", 30, 0, 280)
        );

        doReturn(ResponseEntity.ok().body(stubCodebase)).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));

        List<ResponseCodebaseDto> actualDto = githubInfoService.requestCodebase(stubRepo);
        Assert.assertEquals(expectedDto, actualDto);
    }

    @Test
    public void testGetRepoBySuccess() {
        GitHub stubRepo =  new GitHub();
        stubRepo.setRepoId(323);
        when(gitHubRepository.getById(45l)).thenReturn(stubRepo);
        Assert.assertEquals(stubRepo, githubInfoService.getRepoBy(45));
    }

    @Test
    public void testGetRepoIssuesHttpServerErrorException() {
        Repo repo =  new GitHub();
        repo.setOwner("google");
        repo.setName("googletest");
        doThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY))
                .when(restTemplate)
                .exchange(any(String.class), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));
        List<RepoIssuesDto> actualDto = ReflectionTestUtils.invokeMethod(githubInfoService, "getRepoIssues", repo, "closed");
        Assert.assertEquals(new ArrayList<>(), actualDto);
    }

    @Test
    public void testExistRepoBy() {
        when(gitHubRepository.existsById(1L)).thenReturn(true);
        when(gitHubRepository.existsById(2L)).thenReturn(false);
        Assert.assertTrue(githubInfoService.existRepoBy((1L)));
        Assert.assertFalse(githubInfoService.existRepoBy((2L)));
    }
}