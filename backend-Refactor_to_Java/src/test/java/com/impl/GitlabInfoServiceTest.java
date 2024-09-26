package com.impl;

import com.Application;
import com.PerformanceTests;
import com.bean.GitLab;
import com.bean.Repo;
import com.dao.GitLabRepository;
import com.dto.*;
import com.service.RepoInfoService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class GitlabInfoServiceTest {

    @Autowired
    @Qualifier("gitlabInfoService")
    private RepoInfoService gitlabInfoService;

    @MockBean
    private GitLabRepository gitLabRepository;

    @SpyBean
    private RestTemplate restTemplate;

    @Test
    public void testRequestContributorsActivitySuccess() throws ParseException {
        Repo stubRepo = new GitLab();
        stubRepo.setRepoId(323);
        String stubRequestContributors = "[{'name':'Alice','email':'alice@gmail.com'}," +
                "{'name':'Bob','email':'bob1203@gmail.com'}]";
        List<CommitsDto> stubCommitsDtos = List.of(
                new CommitsDto("Alice", "alice@gmail.com",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2022-1-10"),
                        new CommitsDto.Stats(20, 50, 70),
                        List.of("312e99e294ddb95a4de8b22b13c7bcad80b93187")),
                new CommitsDto("Bob", "bob1203@gmail.com",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2022-1-9"),
                        new CommitsDto.Stats(10, 20, 30),
                        List.of("a6a923534320a6861acf7066f68e3c6e17cd912f")),
                new CommitsDto("Alice", "alice@gmail.com",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2022-1-1"),
                        new CommitsDto.Stats(60, 0, 60),
                        List.of("24a6990f7862f99c58a66cbcda43006d216addd7")),
                new CommitsDto("Alice", "not_exist@gmail.com",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2022-1-1"),
                        new CommitsDto.Stats(0, 10, 10),
                        List.of("24a6990f7862f99c58a6acf7066f68e3c6e17cd9"))
        );

        String domainUrl = "https://service.selab.ml/gitlab/api/v4/";
        String tokenUrl = "&access_token=nKswk3SkyZVyMR_q9KJ4";
        String dummyUrlAliceAvatar = domainUrl + "avatar?email=alice@gmail.com" + tokenUrl;
        String dummyUrlAliceWeb = domainUrl + "users?search=alice@gmail.com" + tokenUrl;
        String dummyUrlBobAvatar = domainUrl + "avatar?email=bob1203@gmail.com" + tokenUrl;
        String dummyUrlBobWeb = domainUrl + "users?search=bob1203@gmail.com" + tokenUrl;

        String stubAliceAvatar = "{'avatar_url':'https://secure.gravatar.com" +
                "/avatar/f589e1b3232c5c773b7f4bcc74b6394b?s=80&d=identicon'}";
        String stubAliceWeb = "[{'web_url':'https://service.selab.ml/gitlab/109598028'}]";
        String stubBobAvatar = "{'avatar_url':'https://service.selab.ml/" +
                "gitlab/uploads/-/system/user/avatar/80/avatar.png'}";
        String stubBobWeb = "[]";

        doReturn(ResponseEntity.ok().body(stubRequestContributors)).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), eq(String.class));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Total-Pages", "2");
        doReturn(ResponseEntity.ok().headers(responseHeaders).body(new ArrayList<CommitsDto>()),
                ResponseEntity.ok().body(stubCommitsDtos))
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        doReturn(stubAliceAvatar).when(restTemplate).getForObject(dummyUrlAliceAvatar, String.class);
        doReturn(stubAliceWeb).when(restTemplate).getForObject(dummyUrlAliceWeb, String.class);
        doReturn(stubBobAvatar).when(restTemplate).getForObject(dummyUrlBobAvatar, String.class);
        doReturn(stubBobWeb).when(restTemplate).getForObject(dummyUrlBobWeb, String.class);
        List<ResponseContributorsActivityDto> actualDto = gitlabInfoService.requestContributorsActivity(stubRepo);
        Assert.assertEquals(new ResponseContributorsActivityDto.
                Author("Alice", "https://secure.gravatar.com/avatar/f589e1b3232c5c773b7f4bcc74b6394b?s=80&d=identicon",
                "https://service.selab.ml/gitlab/109598028", "alice@gmail.com"), actualDto.get(0).getAuthor());
        Assert.assertEquals(new ResponseContributorsActivityDto.Week(dateFormat.parse("2021-12-26"), 0, 60, 0, 1), actualDto.get(0).getWeeks().get(0));
        Assert.assertEquals(new ResponseContributorsActivityDto.Week(dateFormat.parse("2022-01-02"), 0, 0, 0, 0), actualDto.get(0).getWeeks().get(1));
        Assert.assertEquals(new ResponseContributorsActivityDto.Week(dateFormat.parse("2022-01-09"), 0, 20, 50, 1), actualDto.get(0).getWeeks().get(2));
        Assert.assertEquals(2, actualDto.get(0).getTotal());
        Assert.assertEquals(80, actualDto.get(0).getTotalAdditions());
        Assert.assertEquals(50, actualDto.get(0).getTotalDeletions());
        Assert.assertNull(actualDto.get(0).getCommitsHtmlUrl());
        Assert.assertEquals(new ResponseContributorsActivityDto.
                Author("Bob", "https://service.selab.ml/gitlab/uploads/-/system/user/avatar/80/avatar.png",
                "", "bob1203@gmail.com"), actualDto.get(1).getAuthor());
        Assert.assertEquals(new ResponseContributorsActivityDto.Week(dateFormat.parse("2021-12-26"), 0, 0, 0, 0), actualDto.get(1).getWeeks().get(0));
        Assert.assertEquals(new ResponseContributorsActivityDto.Week(dateFormat.parse("2022-01-02"), 0, 0, 0, 0), actualDto.get(1).getWeeks().get(1));
        Assert.assertEquals(new ResponseContributorsActivityDto.Week(dateFormat.parse("2022-01-09"), 0, 10, 20, 1), actualDto.get(1).getWeeks().get(2));
        Assert.assertEquals(1, actualDto.get(1).getTotal());
        Assert.assertEquals(10, actualDto.get(1).getTotalAdditions());
        Assert.assertEquals(20, actualDto.get(1).getTotalDeletions());
        Assert.assertNull(actualDto.get(1).getCommitsHtmlUrl());
    }

    @Test(expected = NullPointerException.class)
    public void testRequestContributorsActivityFail() {
        Repo stubRepo = new GitLab();
        stubRepo.setRepoId(0);
        doReturn(ResponseEntity.badRequest().body(null)).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), eq(String.class));
        gitlabInfoService.requestContributorsActivity(stubRepo);
    }

    @Test
    public void testGetRequestCommitsSuccess() throws ParseException {
        List<CommitsDto> stubCommitsDtos = List.of(
                new CommitsDto("Alice", "alice@gmail.com",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2022-1-10"),
                        new CommitsDto.Stats(20, 50, 70),
                        List.of("312e99e294ddb95a4de8b22b13c7bcad80b93187")),
                new CommitsDto("Bob", "bob1203@gmail.com",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2022-1-9"),
                        new CommitsDto.Stats(10, 20, 30),
                        List.of("a6a923534320a6861acf7066f68e3c6e17cd912f")),
                new CommitsDto("Alice", "alice@gmail.com",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2022-1-1"),
                        new CommitsDto.Stats(60, 0, 60),
                        List.of("24a6990f7862f99c58a66cbcda43006d216addd7",
                                "24a6990f7862f99c58a66cbcda43006d216addd7"))
        );
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Total-Pages", "4");

        // iterator methods stubbing
        doReturn(ResponseEntity.ok().headers(responseHeaders).body(new ArrayList<CommitsDto>()),
                ResponseEntity.ok().body(stubCommitsDtos))
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));

        List<CommitsDto> actualDto = ReflectionTestUtils.invokeMethod(gitlabInfoService, "getCommits", 323);
        Assert.assertEquals(6, actualDto.size());
    }

    @Test(expected = NullPointerException.class)
    public void testGetRequestCommitsFail() {
        doReturn(ResponseEntity.badRequest().body(null))
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));
        ReflectionTestUtils.invokeMethod(gitlabInfoService, "getCommits", 0);
    }

    @Test
    public void testRequestIssueSuccess() throws ParseException {
        Repo stubRepo = new GitLab();
        stubRepo.setRepoId(283);
        DateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        List<IssueDto> stubIssueDtos = List.of(
                new IssueDto(
                        1,
                        "https://service.selab.ml/gitlab/Alice/ipf_detector/-/issues/1",
                        "title_1",
                        new IssueDto.Author("Bob", "https://service.selab.ml/gitlab/Bob"),
                        "closed",
                        fullDateFormat.parse("2021-06-17T15:22:24.147+08:00"),
                        fullDateFormat.parse("2021-06-19T02:46:51.123+08:00")
                ),
                new IssueDto(
                        2,
                        "https://service.selab.ml/gitlab/Alice/ipf_detector/-/issues/2",
                        "title_2",
                        new IssueDto.Author("Bob", "https://service.selab.ml/gitlab/Bob"),
                        "opened",
                        fullDateFormat.parse("2021-07-01T00:00:00.000+08:00"),
                        null
                ),
                new IssueDto(
                        3,
                        "https://service.selab.ml/gitlab/Alice/ipf_detector/-/issues/3",
                        "title_3",
                        new IssueDto.Author("Hung", "https://service.selab.ml/gitlab/Hung"),
                        "closed",
                        fullDateFormat.parse("2021-07-01T10:00:00.000+08:00"),
                        fullDateFormat.parse("2021-07-01T10:30:00.000+08:00")
                ),
                new IssueDto(
                        4,
                        "https://service.selab.ml/gitlab/Alice/ipf_detector/-/issues/4",
                        "title_4",
                        new IssueDto.Author("Hung", "https://service.selab.ml/gitlab/Hung"),
                        "closed",
                        fullDateFormat.parse("2021-07-01T10:00:00.000+08:00"),
                        fullDateFormat.parse("2021-07-02T06:00:00.000+08:00")
                )
        );
        ResponseIssuesDto expectedDto = new ResponseIssuesDto(
                "0 Day(s) 18 Hour(s) 38 Minute(s) 8 Seconds",
                List.of(new RepoIssuesDto(
                        2,
                        "https://service.selab.ml/gitlab/Alice/ipf_detector/-/issues/2",
                        "title_2",
                        new RepoIssuesDto.User("Bob", "https://service.selab.ml/gitlab/Bob"),
                        "opened",
                        "2021-07-01 00:00:00",
                        ""
                )),
                List.of(new RepoIssuesDto(
                        1,
                        "https://service.selab.ml/gitlab/Alice/ipf_detector/-/issues/1",
                        "title_1",
                        new RepoIssuesDto.User("Bob", "https://service.selab.ml/gitlab/Bob"),
                        "closed",
                        "2021-06-17 15:22:24",
                        "2021-06-19 02:46:51"
                ),
                        new RepoIssuesDto(
                        3,
                        "https://service.selab.ml/gitlab/Alice/ipf_detector/-/issues/3",
                        "title_3",
                        new RepoIssuesDto.User("Hung", "https://service.selab.ml/gitlab/Hung"),
                        "closed",
                        "2021-07-01 10:00:00",
                        "2021-07-01 10:30:00"
                ),
                        new RepoIssuesDto(
                        4,
                        "https://service.selab.ml/gitlab/Alice/ipf_detector/-/issues/4",
                        "title_4",
                        new RepoIssuesDto.User("Hung", "https://service.selab.ml/gitlab/Hung"),
                        "closed",
                        "2021-07-01 10:00:00",
                        "2021-07-02 06:00:00"
                ))
        );
        doReturn(ResponseEntity.ok().body(stubIssueDtos)).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));
        ResponseIssuesDto actualDto = gitlabInfoService.requestIssue(stubRepo);

        Assert.assertEquals(expectedDto, actualDto);
    }

    @Test
    public void testRequestIssueEmpty() {
        Repo stubRepo = new GitLab();
        stubRepo.setRepoId(323);
        doReturn(ResponseEntity.ok().body(List.of())).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));
        ResponseIssuesDto actualDto = gitlabInfoService.requestIssue(stubRepo);
        Assert.assertTrue(actualDto.getOpenIssues().isEmpty());
        Assert.assertTrue(actualDto.getCloseIssues().isEmpty());
        Assert.assertEquals("No Data", actualDto.getAverageDealWithIssueTime());
    }

    @Test
    public void testRequestCommitSuccess() throws ParseException {
        Repo stubRepo = new GitLab();
        stubRepo.setRepoId(323);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // only use the Committed_date attributes.
        List<CommitsDto> stubCommitsDtos = List.of(
                new CommitsDto(null, null, dateFormat.parse("2022-5-6"), null, new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-15"), null, new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-15"), null, new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-15"), null, new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-12"), null, new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-10"), null, new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-10"), null, new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-9"), null, new ArrayList<>())
        );
        ResponseCommitInfoDto expectedDto = new ResponseCommitInfoDto(
                List.of(new ResponseCommitInfoDto.WeekTotalData("2022-04-03", 1),
                        new ResponseCommitInfoDto.WeekTotalData("2022-04-10", 6),
                        new ResponseCommitInfoDto.WeekTotalData("2022-04-17", 0),
                        new ResponseCommitInfoDto.WeekTotalData("2022-04-24", 0),
                        new ResponseCommitInfoDto.WeekTotalData("2022-05-01", 1)
                ),
                List.of(new ResponseCommitInfoDto.DayOfWeekData("2022-04-03", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 0),
                                new ResponseCommitInfoDto.DayCommit("Monday", 0),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 0),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 1)
                        )),
                        new ResponseCommitInfoDto.DayOfWeekData("2022-04-10", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 2),
                                new ResponseCommitInfoDto.DayCommit("Monday", 0),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 1),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 3),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 0)
                        )),
                        new ResponseCommitInfoDto.DayOfWeekData("2022-04-17", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 0),
                                new ResponseCommitInfoDto.DayCommit("Monday", 0),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 0),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 0)
                        )),
                        new ResponseCommitInfoDto.DayOfWeekData("2022-04-24", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 0),
                                new ResponseCommitInfoDto.DayCommit("Monday", 0),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 0),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 0)
                        )),
                        new ResponseCommitInfoDto.DayOfWeekData("2022-05-01", List.of(
                                new ResponseCommitInfoDto.DayCommit("Sunday", 0),
                                new ResponseCommitInfoDto.DayCommit("Monday", 0),
                                new ResponseCommitInfoDto.DayCommit("Tuesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Wednesday", 0),
                                new ResponseCommitInfoDto.DayCommit("Thursday", 0),
                                new ResponseCommitInfoDto.DayCommit("Friday", 1),
                                new ResponseCommitInfoDto.DayCommit("Saturday", 0)
                        )))
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Total-Pages", "1");
        doReturn(ResponseEntity.ok().headers(responseHeaders).body(stubCommitsDtos))
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));

        ResponseCommitInfoDto actualDto = gitlabInfoService.requestCommit(stubRepo);
        actualDto.setWeekTotalData(actualDto.getWeekTotalData().stream().limit(5).collect(Collectors.toList()));
        actualDto.setDayOfWeekData(actualDto.getDayOfWeekData().stream().limit(5).collect(Collectors.toList()));
        Assert.assertEquals(expectedDto, actualDto);
    }

    @Test
    public void testRequestCodebaseSuccess() throws ParseException {
        Repo stubRepo = new GitLab();
        stubRepo.setRepoId(323);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // only use the Committed_date and Stats attributes.
        List<CommitsDto> stubCommitsDtos = List.of(
                new CommitsDto(null, null, dateFormat.parse("2022-5-6"),
                        new CommitsDto.Stats(30, 0, 30), new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-15"),
                        new CommitsDto.Stats(100, 100, 200), new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-15"),
                        new CommitsDto.Stats(0, 80, 80), new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-15"),
                        new CommitsDto.Stats(200, 0, 200), new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-12"),
                        new CommitsDto.Stats(0, 30, 30), new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-10"),
                        new CommitsDto.Stats(50, 10, 60), new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-10"),
                        new CommitsDto.Stats(20, 0, 20), new ArrayList<>()),
                new CommitsDto(null, null, dateFormat.parse("2022-4-9"),
                        new CommitsDto.Stats(100, 0, 100), new ArrayList<>())
        );
        List<ResponseCodebaseDto> expectedDto = List.of(
                new ResponseCodebaseDto("2022-04-03", 100, 0, 100),
                new ResponseCodebaseDto("2022-04-10", 370, -220, 250),
                new ResponseCodebaseDto("2022-04-17", 0, 0, 250),
                new ResponseCodebaseDto("2022-04-24", 0, 0, 250),
                new ResponseCodebaseDto("2022-05-01", 30, 0, 280)
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Total-Pages", "1");
        doReturn(ResponseEntity.ok().headers(responseHeaders).body(stubCommitsDtos))
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class));

        List<ResponseCodebaseDto> actualDto = gitlabInfoService.requestCodebase(stubRepo);
        actualDto = actualDto.stream().limit(5).collect(Collectors.toList());
        Assert.assertEquals(expectedDto, actualDto);
    }

    @Test
    public void testGetRepoBySuccess() {
        GitLab stubRepo = new GitLab();
        stubRepo.setRepoId(323);
        when(gitLabRepository.getById(45l)).thenReturn(stubRepo);
        Assert.assertEquals(stubRepo, gitlabInfoService.getRepoBy(45));
    }

    @Test()
    public void testExistRepoById() {
        when(gitLabRepository.existsById(anyLong())).thenReturn(true);
        boolean actual = gitlabInfoService.existRepoBy(0);
        Assert.assertTrue(actual);
    }

    @Test(timeout = 10000)
    @Category(PerformanceTests.class)
    @Ignore
    public void testRequestContributorsActivityPerformance() {  // Integration test
        Repo stubRepo = new GitLab();
        stubRepo.setRepoId(323);
        gitlabInfoService.requestContributorsActivity(stubRepo);
    }

    @Test
    public void testExistRepoBy() {
        when(gitLabRepository.existsById(1L)).thenReturn(true);
        when(gitLabRepository.existsById(2L)).thenReturn(false);
        Assert.assertTrue(gitlabInfoService.existRepoBy((1L)));
        Assert.assertFalse(gitlabInfoService.existRepoBy((2L)));
    }
}