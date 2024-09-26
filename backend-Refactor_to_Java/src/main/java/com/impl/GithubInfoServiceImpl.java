package com.impl;

import com.bean.Repo;
import com.dao.GitHubRepository;
import com.dto.*;
import com.service.RepoInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("githubInfoService")
public class GithubInfoServiceImpl extends BaseServiceImpl implements RepoInfoService {

    @Autowired
    private GitHubRepository gitHubRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat dateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public List<ResponseContributorsActivityDto> requestContributorsActivity(Repo repo) {
        // TODO: 此api沒有提供Author.email資訊，請修正此問題
        //  可參考 https://docs.github.com/en/rest/users/emails
        String url = String.format("https://api.github.com/repos/%s/%s/stats/contributors", repo.getOwner(), repo.getName());

        ResponseEntity<List<ResponseContributorsActivityDto>> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        List<ResponseContributorsActivityDto> contributors = new ArrayList<>(responseEntity.getBody());
        // 依照貢獻者總提交的次數進行高到低排序
        contributors.sort(Comparator.comparingInt(ResponseContributorsActivityDto::getTotal).reversed());

        for (ResponseContributorsActivityDto contributor : contributors) {
            contributor.setCommitsHtmlUrl(String.format("https://github.com/%s/%s/commits?author=%s",
                    repo.getOwner(), repo.getName(), contributor.getAuthor().getLogin()));
            for (ResponseContributorsActivityDto.Week week : contributor.getWeeks()) {
                week.setWs(convertToDate(week.getW()));
                contributor.setTotalAdditions(contributor.getTotalAdditions() + week.getA());
                contributor.setTotalDeletions(contributor.getTotalDeletions() + week.getD());
            }
        }
        return contributors;
    }

    @Override
    public ResponseIssuesDto requestIssue(Repo repo) {
        ResponseIssuesDto repoIssues = new ResponseIssuesDto(
                null, getRepoIssues(repo, "open"), getRepoIssues(repo, "closed"));

        List<Long> closedTime = new ArrayList<>();
        try {
            for (RepoIssuesDto repoIssue : repoIssues.getCloseIssues()) {
                Date closed = fullDateFormat.parse(repoIssue.getClosed_at());
                Date created = fullDateFormat.parse(repoIssue.getCreated_at());
                repoIssue.setClosed_at(dateFormatDetail.format(closed));
                repoIssue.setCreated_at(dateFormatDetail.format(created));
                closedTime.add((closed.getTime() - created.getTime()));
            }
            for (RepoIssuesDto repoIssue : repoIssues.getOpenIssues()) {
                Date created = fullDateFormat.parse(repoIssue.getCreated_at());
                repoIssue.setCreated_at(dateFormatDetail.format(created));
            }
        } catch (ParseException ex) {
            throw new RuntimeException();
        }

        if(!closedTime.isEmpty()) {
            long totalSeconds = 0L;
            for (Long milliseconds : closedTime) {
                totalSeconds += milliseconds / 1000L;
            }
            long averageSeconds = totalSeconds / closedTime.size();
            String averageTime = String.format("%d Day(s) %d Hour(s) %d Minute(s) %d Seconds",
                    averageSeconds / 86400, averageSeconds / 3600 % 24, averageSeconds / 60 % 60, averageSeconds % 60);
            repoIssues.setAverageDealWithIssueTime(averageTime);
        }
        else {
            repoIssues.setAverageDealWithIssueTime("No Data");
        }
        return repoIssues;
    }

    private List<RepoIssuesDto> getRepoIssues(Repo repo, String state) {
        boolean isMatched;
        List<RepoIssuesDto> repoIssuesDtos = new ArrayList<>();
        String url = String.format("https://api.github.com/repos/%s/%s/issues?state=%s&per_page=100&page=1&sort=created",
                repo.getOwner(), repo.getName(), state);
        try {
            do {
                ResponseEntity<List<RepoIssuesDto>> responseEntity = restTemplate.exchange(url,
                        HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
                repoIssuesDtos.addAll(responseEntity.getBody());

                HttpHeaders headers = responseEntity.getHeaders();
                Pattern pattern = Pattern.compile("<(.+)>; rel=\"next\"");
                isMatched = false;
                if (headers.containsKey("Link")) {
                    Matcher matcher = pattern.matcher(headers.get("Link").get(0));
                    if (matcher.find()) {
                        url = matcher.group(1);
                        isMatched = true;
                    }
                }
            } while (isMatched);
        } catch (HttpServerErrorException ex) { // TODO:同一時間最多發15次api,超過則會丟出例外
            return repoIssuesDtos;
        }
        return repoIssuesDtos;
    }

    /**
     * Get the last year of commit activity.
     * ref: https://docs.github.com/en/rest/metrics/statistics#get-the-last-year-of-commit-activity
     */
    @Override
    public ResponseCommitInfoDto requestCommit(Repo repo) {
        String url = String.format("https://api.github.com/repos/%s/%s/stats/commit_activity", repo.getOwner(), repo.getName());

        ResponseEntity<List<CommitInfoDto>> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        List<CommitInfoDto> commitInfoDtos = new ArrayList<>(responseEntity.getBody());

        List<ResponseCommitInfoDto.WeekTotalData> weekChartDatas = new ArrayList<>();
        List<ResponseCommitInfoDto.DayOfWeekData> detailChartDatas = new ArrayList<>();
        for (CommitInfoDto commitInfoDto : commitInfoDtos) {
            weekChartDatas.add(convertToWeekChartData(commitInfoDto));
            detailChartDatas.add(convertToDetailChartData(commitInfoDto));
        }
        return new ResponseCommitInfoDto(weekChartDatas, detailChartDatas);
    }

    private ResponseCommitInfoDto.WeekTotalData convertToWeekChartData(CommitInfoDto commitInfoDto) {
        return new ResponseCommitInfoDto.WeekTotalData(
                dateFormat.format(convertToDate(commitInfoDto.getWeek())),
                commitInfoDto.getTotal()
        );
    }

    private ResponseCommitInfoDto.DayOfWeekData convertToDetailChartData(CommitInfoDto commitInfoDto) {
        List<ResponseCommitInfoDto.DayCommit> detailDatas = new ArrayList<>();
        final List<String> DAY_OF_WEEK = List.of("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

        int dayOfWeekCount = 0;
        for (int day : commitInfoDto.getDays()) {
            detailDatas.add(new ResponseCommitInfoDto.DayCommit(
                DAY_OF_WEEK.get(dayOfWeekCount++),
                day
            ));
        }
        return new ResponseCommitInfoDto.DayOfWeekData(
                dateFormat.format(convertToDate(commitInfoDto.getWeek())),
                detailDatas
        );
    }

    @Override
    public List<ResponseCodebaseDto> requestCodebase(Repo repo) {
        String url = String.format("https://api.github.com/repos/%s/%s/stats/code_frequency", repo.getOwner(), repo.getName());

        ResponseEntity<List<List<Integer>>> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        List<List<Integer>> codebases = new ArrayList<>(responseEntity.getBody());

        List<ResponseCodebaseDto> responseCodebaseDtos = new ArrayList<>();
        int thisWeekRows = 0;
        for (List<Integer> codebase : codebases) {
            thisWeekRows += codebase.get(1) + codebase.get(2);
            ResponseCodebaseDto responseCodebaseDto = new ResponseCodebaseDto(
                    dateFormat.format(convertToDate(codebase.get(0))),
                    codebase.get(1),
                    codebase.get(2),
                    thisWeekRows
            );
            responseCodebaseDtos.add(responseCodebaseDto);
        }
        return responseCodebaseDtos;
    }

    private Date convertToDate(int w) {
        return new Date((long) w * 1000);
    }

    @Override
    public Repo getRepoBy(long id) {
        return gitHubRepository.getById(id);
    }

    @Override
    public boolean existRepoBy(long id) {
        return gitHubRepository.existsById(id);
    }
}