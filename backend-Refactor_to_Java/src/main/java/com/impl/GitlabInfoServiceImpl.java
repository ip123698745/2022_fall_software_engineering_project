package com.impl;

import com.bean.Repo;
import com.dao.GitLabRepository;
import com.dto.*;
import com.service.RepoInfoService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("gitlabInfoService")
public class GitlabInfoServiceImpl extends BaseServiceImpl implements RepoInfoService {

    // 目前gitlab的repoInfo只支援我們實驗室所架設網域，故將此寫成常數
    private static final String DOMAIN_NAME = "service.selab.ml";
    private static final String TOKEN = "access_token=nKswk3SkyZVyMR_q9KJ4";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat dateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private GitLabRepository gitLabRepository;
    private GregorianCalendar gregorianCalendar = new GregorianCalendar();

    @Override
    public List<ResponseContributorsActivityDto> requestContributorsActivity(Repo repo) {
        String url = String.format("https://%s/gitlab/api/v4/projects/%d/repository/contributors?%s", DOMAIN_NAME, repo.getRepoId(), TOKEN);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, null, String.class);

        JSONArray responseJson = new JSONArray(responseEntity.getBody());
        List<CommitsDto> commitsDtos = getCommits(repo.getRepoId());

        List<ResponseContributorsActivityDto> responseContributorsActivityDtos = new ArrayList<>();
        for (int i = 0; i < responseJson.length(); i++) {
            // 建構DTO
            JSONObject contributor = responseJson.getJSONObject(i);
            ResponseContributorsActivityDto dto = new ResponseContributorsActivityDto();
            // 取得avatar_url
            String uri = String.format("https://%s/gitlab/api/v4/avatar?email=%s&%s",
                    DOMAIN_NAME, contributor.getString("email"), TOKEN);
            JSONObject responseAvatar = new JSONObject(restTemplate.getForObject(uri, String.class));
            // 取得web_url
            uri = String.format("https://%s/gitlab/api/v4/users?search=%s&%s",
                    DOMAIN_NAME, contributor.getString("email"), TOKEN);
            String responseStr = restTemplate.getForObject(uri, String.class);
            JSONObject targetUser;
            if (responseStr.equals("[]")) {
                targetUser = new JSONObject("{'web_url':''}");
            } else {
                targetUser = new JSONArray(responseStr).getJSONObject(0);
            }
            dto.setAuthor(new ResponseContributorsActivityDto.Author(
                    contributor.getString("name"),
                    responseAvatar.getString("avatar_url"),
                    targetUser.getString("web_url"),
                    contributor.getString("email")
            ));
            dto.setWeeks(buildWeeks(
                    commitsDtos.get(commitsDtos.size() - 1).getCommitted_date()
            ));
            responseContributorsActivityDtos.add(dto);
        }
        mapCommitsToWeeks(responseContributorsActivityDtos, commitsDtos);
        // 依照貢獻者總提交的次數進行高到低排序
        responseContributorsActivityDtos.sort(Comparator.comparingInt(ResponseContributorsActivityDto::getTotal).reversed());
        return responseContributorsActivityDtos;
    }

    /**
     * Get all Commits records (not limited by API response length).
     */
    private List<CommitsDto> getCommits(int repoId) {
        String url = String.format("https://%s/gitlab/api/v4/projects/%d/repository/commits?%s&with_stats=true&per_page=100", DOMAIN_NAME, repoId, TOKEN);

        ResponseEntity<List<CommitsDto>> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        List<CommitsDto> commitsDtos = new ArrayList<>(responseEntity.getBody());

        HttpHeaders headers = responseEntity.getHeaders();
        int totalPages = Integer.parseInt(headers.get("X-Total-Pages").get(0));

        for (int i = 2; i <= totalPages; i++) {
            ResponseEntity<List<CommitsDto>> response = restTemplate.exchange(String.format("%s&page=%d", url, i),
                    HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });
            commitsDtos.addAll(response.getBody());
        }
        commitsDtos.removeIf(dto -> dto.getParent_ids().size() > 1);
        return commitsDtos;
    }

    /**
     * Build a list Weeks from commitDate to today's "start day"(Sunday) of each week.
     */
    private List<ResponseContributorsActivityDto.Week> buildWeeks(Date commitDate) {
        List<ResponseContributorsActivityDto.Week> weeks = new ArrayList<>();
        List<Date> vs = buildFirstDaysOfWeeks(commitDate);
        for (Date v : vs) {
            weeks.add(new ResponseContributorsActivityDto.Week(v, 0, 0, 0, 0));
        }
        return weeks;
    }

    /**
     * Build a list of strings from commitDate to today's "start day"(Sunday) of each week.
     */
    private List<Date> buildFirstDaysOfWeeks(Date commitDate) {
        List<Date> weeks = new ArrayList<>();
        GregorianCalendar commitCalendar = new GregorianCalendar();
        GregorianCalendar currentCalendar = new GregorianCalendar();
        commitCalendar.setTime(commitDate);
        commitCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        while (commitCalendar.compareTo(currentCalendar) < 0) {
            weeks.add(commitCalendar.getTime());
            commitCalendar.add(Calendar.DATE, 7);
        }
        return weeks;
    }

    /**
     * Map commits to contributor data.
     */
    private void mapCommitsToWeeks(List<ResponseContributorsActivityDto> contributors, List<CommitsDto> commits) {
        HashMap<String, ResponseContributorsActivityDto> hashContributors = new LinkedHashMap<>();
        for (ResponseContributorsActivityDto contributor : contributors) {
            hashContributors.put(contributor.getAuthor().getEmail(), contributor);
        }
        for (CommitsDto commit : commits) {
            String commitWeek = getDateOfWeek(commit.getCommitted_date());
            ResponseContributorsActivityDto contributor = hashContributors.get(commit.getCommitter_email());
            if (contributor != null) {
                ResponseContributorsActivityDto.Week week = contributor.getWeeks()
                        .stream().filter(w -> dateFormat.format(w.getWs()).equals(commitWeek)).findFirst().orElseThrow();
                week.setA(week.getA() + commit.getStats().getAdditions());
                week.setD(week.getD() + commit.getStats().getDeletions());
                week.setC(week.getC() + 1);
                contributor.setTotalAdditions(contributor.getTotalAdditions() + commit.getStats().getAdditions());
                contributor.setTotalDeletions(contributor.getTotalDeletions() + commit.getStats().getDeletions());
                contributor.setTotal(contributor.getTotal() + 1);
            }
        }
    }

    @Override
    public ResponseIssuesDto requestIssue(Repo repo) {
        String url = String.format("https://%s/gitlab/api/v4/projects/%d/issues?%s", DOMAIN_NAME, repo.getRepoId(), TOKEN);

        ResponseEntity<List<IssueDto>> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        List<IssueDto> issueDtos = responseEntity.getBody();
        List<RepoIssuesDto> openIssues = new ArrayList<>();
        List<RepoIssuesDto> closeIssues = new ArrayList<>();
        List<Long> closedTime = new ArrayList<>();
        ResponseIssuesDto repoIssues = new ResponseIssuesDto(null, openIssues, closeIssues);

        for (IssueDto issue : issueDtos) {
            if (issue.getState().equals("opened")) {
                openIssues.add(createRepoIssuesDto(issue));
            } else {
                closeIssues.add(createRepoIssuesDto(issue));
                closedTime.add((issue.getClosed_at().getTime() - issue.getCreated_at().getTime()));
            }
        }
        if (!closedTime.isEmpty()) {
            long totalSeconds = 0L;
            for (Long milliseconds : closedTime) {
                totalSeconds += milliseconds / 1000L;
            }
            long averageSeconds = totalSeconds / closedTime.size();
            String averageTime = String.format("%d Day(s) %d Hour(s) %d Minute(s) %d Seconds",
                    averageSeconds / 86400, averageSeconds / 3600 % 24, averageSeconds / 60 % 60, averageSeconds % 60);
            repoIssues.setAverageDealWithIssueTime(averageTime);
        } else {
            repoIssues.setAverageDealWithIssueTime("No Data");
        }
        return repoIssues;
    }

    /**
     * Convert RequestIssueDto to ResponseRepoIssuesDto.
     */
    private RepoIssuesDto createRepoIssuesDto(IssueDto issue) {
        RepoIssuesDto.User user = new RepoIssuesDto.User(
                issue.getAuthor().getUsername(),
                issue.getAuthor().getWeb_url()
        );
        return new RepoIssuesDto(
                issue.getIid(),
                issue.getWeb_url(),
                issue.getTitle(),
                user,
                issue.getState(),
                dateFormatDetail.format(issue.getCreated_at()),
                issue.getClosed_at() == null ? "" : dateFormatDetail.format(issue.getClosed_at())
        );
    }

    @Override
    public ResponseCommitInfoDto requestCommit(Repo repo) {
        List<CommitsDto> commitsDtos = getCommits(repo.getRepoId());
        return new ResponseCommitInfoDto(
                getWeekTotalDatas(commitsDtos),
                getDayOfWeekDatas(commitsDtos)
        );
    }

    /**
     * Get number of commits for the week.
     */
    private List<ResponseCommitInfoDto.WeekTotalData> getWeekTotalDatas(List<CommitsDto> commitsDtos) {
        List<ResponseCommitInfoDto.WeekTotalData> weekTotalDatas = new ArrayList<>();
        List<ResponseContributorsActivityDto.Week> weeks = buildWeeks(
                commitsDtos.get(commitsDtos.size() - 1).getCommitted_date());

        for (CommitsDto commitsDto : commitsDtos) {
            String commitWeek = getDateOfWeek(commitsDto.getCommitted_date());
            // find the first matching commitWeek
            ResponseContributorsActivityDto.Week week = weeks
                    .stream().filter(w -> dateFormat.format(w.getWs()).equals(commitWeek)).findFirst().orElseThrow();
            week.setC(week.getC() + 1);
        }
        for (ResponseContributorsActivityDto.Week week : weeks) {
            weekTotalDatas.add(new ResponseCommitInfoDto.WeekTotalData(dateFormat.format(week.getWs()), week.getC()));
        }
        return weekTotalDatas;
    }

    /**
     * Get number of commits for the day.
     */
    private List<ResponseCommitInfoDto.DayOfWeekData> getDayOfWeekDatas(List<CommitsDto> commitsDtos) {
        List<ResponseCommitInfoDto.DayOfWeekData> dayOfWeekDatas = new ArrayList<>();
        List<ResponseContributorsActivityDto.Week> weeks = buildWeeks(
                commitsDtos.get(commitsDtos.size() - 1).getCommitted_date());
        GregorianCalendar calendar = new GregorianCalendar();
        final List<String> DAY_OF_WEEK = List.of("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

        // prepare the number of commits per day
        HashMap<String, Integer> commitsOfDay = new HashMap<>();
        for (CommitsDto commitsDto : commitsDtos) {
            String commitDay = dateFormat.format(commitsDto.getCommitted_date());
            Integer count = commitsOfDay.getOrDefault(commitDay, 0);
            commitsOfDay.put(commitDay, count + 1);
        }

        for (ResponseContributorsActivityDto.Week week : weeks) {
            List<ResponseCommitInfoDto.DayCommit> dayCommits = new ArrayList<>();
            dayOfWeekDatas.add(new ResponseCommitInfoDto.DayOfWeekData(dateFormat.format(week.getWs()), dayCommits));

            // set calendar to the Sunday of the week.
            calendar.setTime(week.getWs());
            // add number of commits of the day.
            for (String dayOfWeek : DAY_OF_WEEK) {
                dayCommits.add(new ResponseCommitInfoDto.DayCommit(
                        dayOfWeek,
                        commitsOfDay.getOrDefault(dateFormat.format(calendar.getTime()), 0)
                ));
                calendar.add(Calendar.DATE, 1);
            }
        }
        return dayOfWeekDatas;
    }

    @Override
    public List<ResponseCodebaseDto> requestCodebase(Repo repo) {
        List<CommitsDto> commitsDtos = getCommits(repo.getRepoId());
        List<ResponseCodebaseDto> responseCodebaseDtos = buildResponseCodebaseDto(
                commitsDtos.get(commitsDtos.size() - 1).getCommitted_date());

        for (CommitsDto commit : commitsDtos) {
            String commitWeek = getDateOfWeek(commit.getCommitted_date());
            // find the first matching commitWeek
            ResponseCodebaseDto responseCodebaseDto = responseCodebaseDtos
                    .stream().filter(c -> c.getDate().equals(commitWeek)).findFirst().orElseThrow();
            responseCodebaseDto.setNumberOfRowsAdded(responseCodebaseDto.getNumberOfRowsAdded() + commit.getStats().getAdditions());
            responseCodebaseDto.setNumberOfRowsDeleted(responseCodebaseDto.getNumberOfRowsDeleted() - commit.getStats().getDeletions());
        }
        int numberOfRows = 0;
        for (ResponseCodebaseDto responseCodebaseDto : responseCodebaseDtos) {
            numberOfRows += responseCodebaseDto.getNumberOfRowsAdded() + responseCodebaseDto.getNumberOfRowsDeleted();
            responseCodebaseDto.setNumberOfRows(numberOfRows);
        }
        return responseCodebaseDtos;
    }

    /**
     * Create empty ResponseCodebaseDto to save data later.
     */
    private List<ResponseCodebaseDto> buildResponseCodebaseDto(Date commitDate) {
        List<ResponseCodebaseDto> responseCodebaseDtos = new ArrayList<>();
        List<Date> weeks = buildFirstDaysOfWeeks(commitDate);
        for (Date week : weeks) {
            responseCodebaseDtos.add(new ResponseCodebaseDto(dateFormat.format(week), 0, 0, 0));
        }
        return responseCodebaseDtos;
    }

    /**
     * Get Date of week.
     */
    private String getDateOfWeek(Date commitDate) {
        gregorianCalendar.setTime(commitDate);
        gregorianCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return dateFormat.format(gregorianCalendar.getTime());
    }

    /**
     * Find RepoOld entity by ID of table Repositories in database.
     */
    @Override
    public Repo getRepoBy(long id) {
        return gitLabRepository.getById(id);
    }

    @Override
    public boolean existRepoBy(long id) {
        return gitLabRepository.existsById(id);
    }
}