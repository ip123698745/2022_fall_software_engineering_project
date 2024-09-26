package com.impl;

import com.bean.Sonarqube;
import com.bean.User;
import com.dao.SonarqubeRepository;
import com.dto.CodeSmellDataDto;
import com.dto.ResponseSonarqubeDto;
import com.service.SonarqubeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.util.*;

@Service("sonarqubeService")
public class SonarqubeServiceImpl extends BaseServiceImpl implements SonarqubeService {

    @Autowired
    private SonarqubeRepository sonarqubeRepository;

    private final int PAGE_SIZE = 500;
    private HttpHeaders headers = new HttpHeaders(); // for basic auth

    @Override
    public ResponseSonarqubeDto getSonarqubeInfo(long repoId) {
        Sonarqube sonarqube = sonarqubeRepository.getById(repoId);
        // Sonarqube measures API
        String sonarqubeHostUrl = sonarqube.getUrl();
        String apiUrl = "api/measures/component?";
        String component = "component=" + sonarqube.getToken();
        String query = "&metricKeys=bugs,vulnerabilities,code_smells,duplicated_lines_density,coverage";
        String url = sonarqubeHostUrl + apiUrl + component + query;

        // Basic Authorization
        headers.setBasicAuth(sonarqube.getToken(), "");

        // Get Project Info from Sonarqube API
        ResponseEntity<String> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        // Process json string
        JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
        JSONArray Measures = responseJsonObject.getJSONObject("component").getJSONArray("measures");
        List<ResponseSonarqubeDto.Measure> MeasureList = new ArrayList<>();
        // Add every metrics
        for (int i = 0; i < Measures.length(); i++) {
            JSONObject objTemp = Measures.getJSONObject(i);
            MeasureList.add(new ResponseSonarqubeDto.Measure(
                    objTemp.getString("metric"),
                    objTemp.getString("value"),
                    "", // in Sonarqube API, didn't see that value
                    objTemp.getBoolean("bestValue")
            ));
        }

        // Return SonarqubeDto
        return new ResponseSonarqubeDto(MeasureList, sonarqube.getToken());
    }

    @Override
    public Map<String, List<CodeSmellDataDto.Issues>> getSonarqubeCodeSmell(long repoId) {
        Sonarqube sonarqube = sonarqubeRepository.getById(repoId);
        // Basic Authorization
        headers.setBasicAuth(sonarqube.getToken(), "");

        CodeSmellDataDto result = requestCodeSmellData(repoId, 1);
        int totalPages = (result.getTotal() - 1) / PAGE_SIZE + 1;

        for (int i = 2; i <= totalPages; i++) {
            CodeSmellDataDto others = requestCodeSmellData(repoId, i);
            List<CodeSmellDataDto.Issues> temp = result.getIssues();
            temp.addAll(others.getIssues());
            result.setIssues(temp);
        }

        return mapCodeSmellBy(result.getIssues());
    }

    private Map<String, List<CodeSmellDataDto.Issues>> mapCodeSmellBy(List<CodeSmellDataDto.Issues> issues) {
        Map<String, List<CodeSmellDataDto.Issues>> info = new HashMap<>();

        for(CodeSmellDataDto.Issues issue : issues) {
            if(info.containsKey(issue.getComponent())) {
                // add issue from key value if key exist
                List<CodeSmellDataDto.Issues> issuesValue = info.get(issue.getComponent());
                issuesValue.add(issue);
            } else {
                // create list if not exist key
                List<CodeSmellDataDto.Issues> issuesValue = new ArrayList<CodeSmellDataDto.Issues>();
                issuesValue.add(issue);
                info.put(issue.getComponent(), issuesValue);
            }
        }

        return info;
    }

    // pageIndex default value is 1 (pageIndex = 1)
    private CodeSmellDataDto requestCodeSmellData(long repoId, int pageIndex) {
        Sonarqube sonarqube = sonarqubeRepository.getById(repoId);
        // Sonarqube issues API
        String sonarqubeHostUrl = sonarqube.getUrl();
        String apiUrl = "api/issues/search?";
        String projectKeys = "projectKeys=" + sonarqube.getToken();
        String query = "componentKeys=" + sonarqube.getToken() + "&s=FILE_LINE&resolved=false&ps=" + PAGE_SIZE + "&organization=default-organization&facets=severities,types&types=CODE_SMELL";
        String url = sonarqubeHostUrl + apiUrl + projectKeys + query + "&p=" + pageIndex;

        // Get CodeSmellData from Sonarqube API
        ResponseEntity<String> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        // Process json string
        JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
        JSONArray IssueArray = responseJsonObject.getJSONArray("issues");
        List<CodeSmellDataDto.Issues> Issues = new ArrayList<>();
        for (int i = 0; i < IssueArray.length(); i++) {
            JSONObject objTemp = IssueArray.getJSONObject(i);

            // some issue don't have line at json object
            int lineInteger = 0;

            if(objTemp.has("line")) {
                lineInteger = objTemp.getInt("line");
            }

            Issues.add(new CodeSmellDataDto.Issues(
                    objTemp.getString("key"),
                    objTemp.getString("severity"),
                    objTemp.getString("component"),
                    lineInteger,
                    objTemp.getString("message")
            ));
        }

        return new CodeSmellDataDto(responseJsonObject.getInt("total"), Issues);
    }

    public boolean isHaveSonarqube(long repoId) {
        Optional<Sonarqube> sonarqube = sonarqubeRepository.findById(repoId);
        return sonarqube.isPresent();
    }
}

/*
// The below code need to put into RepoInfoController
[Authorize]
[HttpGet("sonarqube/{repoId}")]
public async Task<IActionResult> GetSonarqube(int repoid)
{
    return Ok(await _sonarqubeService.GetSonarqubeInfoAsync(repoid));
}

[Authorize]
[HttpGet("ishavesonarqube/{repoId}")]
public async Task<IActionResult> IsHaveSonarqube(int repoid)
{
    return Ok(await _sonarqubeService.IsHaveSonarqube(repoid));
}

[HttpGet("sonarqube/codesmell/{repoId}")]
public async Task<IActionResult> GetSonarqubeCodeSmell(int repoid)
{
    return Ok(await _sonarqubeService.GetSonarqubeCodeSmellAsync(repoid));
}
*/
