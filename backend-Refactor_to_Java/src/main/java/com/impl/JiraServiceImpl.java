package com.impl;

import com.bean.Jira;
import com.bean.Project;
import com.dao.JiraRepository;
import com.dao.ProjectRepository;
import com.dto.*;
import com.service.JiraService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

//todo Domain_url needs to be Full_url
@Service("JiraService")
public class JiraServiceImpl extends BaseServiceImpl implements JiraService {

    @Autowired
    private JiraRepository jiraRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private Calendar calendar;

    @Override
    public List<JiraBoardDetailDto> getJiraBoardDetail(RequestJiraLoginDto requestJiraLoginDto) {
        String uri = String.format("https://%s.atlassian.net/rest/agile/1.0/board", requestJiraLoginDto.getDomainURL());

        //定義一個header供存取data用
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(requestJiraLoginDto.getAccount(), requestJiraLoginDto.getAPIToken());
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.
                exchange(uri, HttpMethod.GET, request, String.class);

        //以json形式存取request得到的值
        JSONObject jsonResponse = new JSONObject(response.getBody());
        List<JiraBoardDetailDto> jiraBoardDetailDtos = new ArrayList<>();
        JSONArray boardList = jsonResponse.getJSONArray("values");

        //提取我們需要的attribute
        for(int i  = 0 ; i < boardList.length() ; i++) {
            JSONObject boardInfo = boardList.getJSONObject(i);
            jiraBoardDetailDtos.add(new JiraBoardDetailDto(boardInfo.getInt("id"), boardInfo.getString("name")));
        }

        return jiraBoardDetailDtos;
    }

    @Override
    public void createRepoOfJira(RequestJiraLoginDto requestJiraLoginDto) throws Exception {
//        ToDo : 由於createRepoOfJira 這個Method本身是不會丟出Exception的，目前為了測試通過才加上throws Exception，日後若情況允許，請Refactor。
        List<JiraBoardDetailDto> boardDetailDtos = getJiraBoardDetail(requestJiraLoginDto);
        String boardName = null;
        for (JiraBoardDetailDto boardDetailDto : boardDetailDtos) {
            if (boardDetailDto.getId() == requestJiraLoginDto.getBoardId()) {
                boardName = boardDetailDto.getName();
            }
        }

        // Setup Jira
        Jira jira = new Jira();
        jira.setUrl(requestJiraLoginDto.getDomainURL());
        jira.setApiToken(requestJiraLoginDto.getAPIToken());
        jira.setAccount(requestJiraLoginDto.getAccount());
        jira.setName(boardName);
        jira.setBoardId(requestJiraLoginDto.getBoardId());
        jira.setType("Jira");
        jiraRepository.save(jira);

        // Add Jira to Project
        Project project = projectRepository.getById(requestJiraLoginDto.getProjectId());
        project.getJiras().add(jira);
        projectRepository.save(project);
    }

    @Override
    public List<ResponseJiraIssueDto> getAllIssueByBoardId(long repoId) {
        Jira jira = jiraRepository.getById(repoId);
        String uri = String.format("https://%s.atlassian.net/rest/agile/1.0/board/%s/issue",jira.getUrl(),jira.getBoardId());

        //定義一個header供存取data用
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jira.getAccount(), jira.getApiToken());
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        //因JSON過多巢狀結構，先以String儲存，再轉為JSONArray一筆一筆資料寫入
        ResponseEntity<String> responseByString = restTemplate.
                exchange(uri, HttpMethod.GET, request, String.class);
        JSONObject response = new JSONObject(responseByString.getBody());
        JSONArray processResponse = response.getJSONArray("issues");
        List<ResponseJiraIssueDto> result = new ArrayList<>();

        for(int i = 0 ; i < processResponse.length() ; i++){
            JSONObject fields = processResponse.getJSONObject(i).getJSONObject("fields");
            ResponseJiraIssueDto issueReadyToAdd = new ResponseJiraIssueDto();
            issueReadyToAdd.setSummary(fields.getString("summary"));
            issueReadyToAdd.setType(fields.getJSONObject("issuetype").getString("name"));
            issueReadyToAdd.setStatus(fields.getJSONObject("status").getString("name"));
            issueReadyToAdd.setPriority(fields.getJSONObject("priority").getString("name"));
            issueReadyToAdd.setKey(processResponse.getJSONObject(i).getString("key"));
            if(fields.isNull("resolution")){
                issueReadyToAdd.setResolution("Unresolved");
            }else {
                issueReadyToAdd.setResolution("Done");
            }
            issueReadyToAdd.setCreated(fields.getString("created"));
            issueReadyToAdd.setUpdated(fields.getString("updated"));
            List<String> labels = new ArrayList<>();
            JSONArray labelsToAdd = fields.getJSONArray("labels");
            for(int j = 0 ; j < labelsToAdd.length() ; j ++){
                labels.add(labelsToAdd.getString(j));
            }
            issueReadyToAdd.setLabel(labels);
            result.add(issueReadyToAdd);
        }
        return result;
    }

    @Override
    public List<ResponseJiraIssueDto> getIssueBySprintId(long repoId, int sprintId) {
        Jira jira = jiraRepository.getById(repoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jira.getAccount(),jira.getApiToken());
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        String uri = String.format("https://%s.atlassian.net/rest/agile/1.0/sprint/%d/issue",jira.getUrl(),sprintId);

        ResponseEntity<String> response = restTemplate.
                exchange(uri, HttpMethod.GET, request, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("issues");
        List<ResponseJiraIssueDto> result = new ArrayList<>();
        for(int i = 0 ; i < jsonArray.length() ; i++) {
            JSONObject fields = jsonArray.getJSONObject(i).getJSONObject("fields");
            ResponseJiraIssueDto issueReadyToAdd = new ResponseJiraIssueDto();
            issueReadyToAdd.setSummary(fields.getString("summary"));
            issueReadyToAdd.setType(fields.getJSONObject("issuetype").getString("name"));
            issueReadyToAdd.setStatus(fields.getJSONObject("status").getString("name"));
            issueReadyToAdd.setPriority(fields.getJSONObject("priority").getString("name"));
            issueReadyToAdd.setKey(jsonArray.getJSONObject(i).getString("key"));
            List<String> labels = new ArrayList<>();
            JSONArray labelsToAdd = fields.getJSONArray("labels");
            for (int j = 0; j < labelsToAdd.length(); j++) {
                labels.add(labelsToAdd.getString(j));
            }
            issueReadyToAdd.setLabel(labels);
            issueReadyToAdd.setEstimatePoint(fields.optInt("customfield_10016", 0));
            issueReadyToAdd.setDescription(fields.optString("description", ""));
            List<ResponseJiraIssueDto> subtasks = new ArrayList<>();
            JSONArray subtasksJsonArray = fields.getJSONArray("subtasks");
            if(subtasksJsonArray.length() > 0){
                for (int j = 0; j < subtasksJsonArray.length(); j++) {
                    ResponseJiraIssueDto subtask = new ResponseJiraIssueDto();
                    subtask.setKey(subtasksJsonArray.getJSONObject(j).getString("key"));
                    subtask.setSummary(subtasksJsonArray.getJSONObject(j).getJSONObject("fields").getString("summary"));
                    subtask.setStatus(subtasksJsonArray.getJSONObject(j).getJSONObject("fields").getJSONObject("status").getString("name"));
                    subtasks.add(subtask);
                }
                issueReadyToAdd.setSubTasks(subtasks);
            }else{
                issueReadyToAdd.setSubTasks(null);
            }

            result.add(issueReadyToAdd);
        }
        return result;
    }

    @Override
    public List<ResponseSprintDto> getAllSprintByBoardId(long repoId){
        Jira jira = jiraRepository.getById(repoId);
        List<ResponseSprintDto> result = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jira.getAccount(),jira.getApiToken());
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
//        type = Jira 的 Repo，其RepoId = BoardId
        String uri = String.format("https://%s.atlassian.net/rest/agile/1.0/board/%s/sprint", jira.getUrl(), jira.getBoardId());

        ResponseEntity<String> responseByString = restTemplate.
                exchange(uri, HttpMethod.GET, request, String.class);

        JSONObject jsonObject = new JSONObject(responseByString.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("values");

        for(int i = 0 ; i < jsonArray.length() ; i++) {
            ResponseSprintDto countedSprint = new ResponseSprintDto();
            JSONObject values = jsonArray.getJSONObject(i);

            countedSprint.setId(values.getInt("id"));
            countedSprint.setName(values.getString("name"));
            countedSprint.setGoal(values.getString("goal"));

            result.add(countedSprint);
        }
        return result;
    }

    @Override
    public List<ResponseBurndownChartDto> getBurndownChart(long repoId, int sprintId) {
//        先把最終要format的格式 & 時區設定好
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Jira jira = jiraRepository.getById(repoId);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jira.getAccount(), jira.getApiToken());
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        // TODO : 依照 178 行的解釋 RepoId = BoardId ，所以更換成 jira.getBoardId()，需再與學長確認或自行測試
        // TODO : 根據解釋是不是表示 110 學年度的 repo_id 是 BoardId，還是 Repositories 的 id 是 BoardId ?
        // TODO : 如果 Repositories 的 id 是 BoardId 的話，那 getBoardId() 就需改成 getId() !
        String uri = String.format("https://%s.atlassian.net/rest/greenhopper/1.0/rapid/" +
                "charts/scopechangeburndownchart?rapidViewId=%d&sprintId=%d", jira.getUrl(), jira.getBoardId(), sprintId);

        ResponseEntity<String> response = restTemplate.
                exchange(uri, HttpMethod.GET, request, String.class);
        JSONObject jsonObject = new JSONObject(response.getBody());

        List<ResponseBurndownChartDto> result = new ArrayList<>();
        Long startTime = jsonObject.getLong("startTime");
        Long endTime = jsonObject.getLong("endTime");
        Hashtable<String, Integer> stories = new Hashtable<>();
        int totalPoint = 0;
        int sprintStartPoint = 0;
        Long timestamp;

        JSONObject allTime = jsonObject.getJSONObject("changes");
        Iterator<String> keys = allTime.keys();

        while(keys.hasNext()){
            String jsonKey = keys.next();
            JSONArray time = allTime.getJSONArray(jsonKey);
            JSONObject value = time.getJSONObject(0);
            timestamp = Long.valueOf(jsonKey);
            String key = value.getString("key");

            if(!value.isNull("statC") && !value.getJSONObject("statC").isNull("newValue")){ // 估點
                if(stories.containsKey(key)){
                    totalPoint += value.getJSONObject("statC").getInt("newValue") - stories.get(key);
                    stories.put(key, value.getJSONObject("statC").getInt("newValue"));
                }else{
                    stories.put(key, value.getJSONObject("statC").getInt("newValue"));
                }
            }else if (!value.isNull("added") && value.getBoolean("added")){ // 拉入Sprint
                if(stories.containsKey(key)){
                    totalPoint += stories.get(key);
                }else{
                    stories.put(key, 0);
                }
            }else if(!value.isNull("added") && !value.getBoolean("added")){ // 拉出Sprint
                totalPoint -= stories.get(key);
                stories.remove(key);
            }else if(!value.isNull("column") && !value.getJSONObject("column").getBoolean("notDone")){ // 完成
                totalPoint -= stories.get(key);
                stories.put(key, 0);
            }else{
                continue;
            }

            if(timestamp.compareTo(startTime) > 0){
                result.add(new ResponseBurndownChartDto(simpleDateFormat.format(timestamp), totalPoint));
            }else if(result.size() == 0){ // sprint還沒開始時，紀錄sprint開始時會有多少story point
                sprintStartPoint = totalPoint;
            }
        }

        // sprint第一天
        result.add(0, new ResponseBurndownChartDto(simpleDateFormat.format(startTime), sprintStartPoint));
        // 如果發api當天還在sprint內，加一個當天的total story point
        if(endTime.compareTo(calendar.getTimeInMillis()) > 0){
            result.add(new ResponseBurndownChartDto(new SimpleDateFormat("yyyy-MM-dd").format(new Date(calendar.getTimeInMillis())), totalPoint));
        }
        // sprint最後一天
        result.add(new ResponseBurndownChartDto(simpleDateFormat.format(endTime), 0));

        return result;
    }
}