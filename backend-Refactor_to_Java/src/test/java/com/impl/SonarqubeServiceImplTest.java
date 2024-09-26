package com.impl;

import com.Application;
import com.bean.Sonarqube;
import com.dao.SonarqubeRepository;
import com.dto.CodeSmellDataDto;
import com.dto.ResponseSonarqubeDto;
import com.service.SonarqubeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SonarqubeServiceImplTest {
    @Autowired
    private SonarqubeService sonarqubeService;

    @MockBean
    private SonarqubeRepository sonarqubeRepository;

    private final String COMPONENT_NAME = "PTT4PBL";
    private final String SONARQUBE_HOST_URL = "https://service.selab.ml/sonarqube/";
    private final String SONARQUBE_TOKEN = "111598030_javaweb_AYK3DVmoImQUE4HrLT5W"; // TODO need place account & password encode with Base64

    @MockBean
    private RestTemplate restTemplate;

    private String getResponseOfOverall() {
        String json = "{'component': {"
                    + "'key': '111598030_javaweb_AYK3DVmoImQUE4HrLT5W',"
                    + "'name': 'HappyCamp',"
                    + "'qualifier': 'TRK',"
                    + "'measures': ["
                        + "{'metric': 'duplicated_lines_density', 'value': '5.0', 'bestValue': false},"
                        + "{'metric': 'code_smells', 'value': '55', 'bestValue': false},"
                        + "{'metric': 'bugs', 'value': '2', 'bestValue': false},"
                        + "{'metric': 'vulnerabilities', 'value': '36', 'bestValue': false},"
                        + "{'metric': 'coverage', 'value': '0.0', 'bestValue': false}"
                    + "]"+ "}}";
        return json;
    }

    private String getResponseOfCodeSmell() {
        String json = "{"
                    + "'total': 501,"
                    + "'p': 1,"
                    + "'ps': 500,"
                    + "'paging': {'pageIndex': 1,'pageSize': 500,'total': 55},"
                    + "'effortTotal': 556,"
                    + "'issues': ["
                    + "{"
                        + "'key':'01fc972e-2a3c-433e-bcae-0bd7f88f5123',"
                        + "'severity':'MINOR',"
                        + "'component': 'PTT4PBL',"
                        + "'line': 81,"
                        + "'message': \"'System.Exception' should not be thrown by user code.\""
                    + "}" + "," + "{"
                        + "'key':'01fc972e-2a3c-433e-bcae-0bd7f88f5123',"
                        + "'severity':'MINOR',"
                        + "'component': 'PTT4PBL',"
                        + "'line': 81,"
                        + "'message': \"'System.Exception' should not be thrown by user code.\""
                    + "}]}";
        return json;
    }

    @Test
    public void testGetSonarqubeInfo() {
        HttpHeaders headers = new HttpHeaders();
        Sonarqube sonar1 = new Sonarqube();
        sonar1.setUrl(SONARQUBE_HOST_URL);
        sonar1.setToken(SONARQUBE_TOKEN);
        when(sonarqubeRepository.getById(anyLong())).thenReturn(sonar1);

        headers.setBasicAuth(SONARQUBE_TOKEN, "");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(getResponseOfOverall()));

        ResponseSonarqubeDto response = sonarqubeService.getSonarqubeInfo(1);
        Assert.assertEquals(SONARQUBE_TOKEN, response.getProjectName());
        Assert.assertEquals(5, response.getMeasures().size());

        Assert.assertEquals("duplicated_lines_density", response.getMeasures().get(0).getMetric());
        Assert.assertFalse(response.getMeasures().get(0).isBestValue());
        Assert.assertEquals("5.0", response.getMeasures().get(0).getValue());

        Assert.assertEquals("code_smells", response.getMeasures().get(1).getMetric());
        Assert.assertFalse(response.getMeasures().get(1).isBestValue());
        Assert.assertEquals("55", response.getMeasures().get(1).getValue());

        Assert.assertEquals("bugs", response.getMeasures().get(2).getMetric());
        Assert.assertFalse(response.getMeasures().get(2).isBestValue());
        Assert.assertEquals("2", response.getMeasures().get(2).getValue());

        Assert.assertEquals("vulnerabilities", response.getMeasures().get(3).getMetric());
        Assert.assertFalse(response.getMeasures().get(3).isBestValue());
        Assert.assertEquals("36", response.getMeasures().get(3).getValue());

        Assert.assertEquals("coverage", response.getMeasures().get(4).getMetric());
        Assert.assertFalse(response.getMeasures().get(4).isBestValue());
        Assert.assertEquals("0.0", response.getMeasures().get(4).getValue());
    }

    @Test
    public void testGetSonarqubeCodeSmell() {
        HttpHeaders headers = new HttpHeaders();
        Sonarqube sonar1 = new Sonarqube();
        sonar1.setUrl(SONARQUBE_HOST_URL);
        sonar1.setToken(SONARQUBE_TOKEN);
        when(sonarqubeRepository.getById(anyLong())).thenReturn(sonar1);

        headers.setBasicAuth(SONARQUBE_TOKEN, "");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(getResponseOfCodeSmell()));

        Map<String, List<CodeSmellDataDto.Issues>> response = sonarqubeService.getSonarqubeCodeSmell(1);
        Assert.assertEquals(4, response.get(COMPONENT_NAME).size());
        CodeSmellDataDto.Issues issues = response.get(COMPONENT_NAME).get(0);
        Assert.assertEquals("01fc972e-2a3c-433e-bcae-0bd7f88f5123", issues.getKey());
        Assert.assertEquals("MINOR", issues.getSeverity());
        Assert.assertEquals(COMPONENT_NAME, issues.getComponent());
        Assert.assertEquals(81, issues.getLine());
        Assert.assertEquals("'System.Exception' should not be thrown by user code.", issues.getMessage());

        issues = response.get(COMPONENT_NAME).get(1);
        Assert.assertEquals("01fc972e-2a3c-433e-bcae-0bd7f88f5123", issues.getKey());
        Assert.assertEquals("MINOR", issues.getSeverity());
        Assert.assertEquals(COMPONENT_NAME, issues.getComponent());
        Assert.assertEquals(81, issues.getLine());
        Assert.assertEquals("'System.Exception' should not be thrown by user code.", issues.getMessage());

        issues = response.get(COMPONENT_NAME).get(2);
        Assert.assertEquals("01fc972e-2a3c-433e-bcae-0bd7f88f5123", issues.getKey());
        Assert.assertEquals("MINOR", issues.getSeverity());
        Assert.assertEquals(COMPONENT_NAME, issues.getComponent());
        Assert.assertEquals(81, issues.getLine());
        Assert.assertEquals("'System.Exception' should not be thrown by user code.", issues.getMessage());

        issues = response.get(COMPONENT_NAME).get(3);
        Assert.assertEquals("01fc972e-2a3c-433e-bcae-0bd7f88f5123", issues.getKey());
        Assert.assertEquals("MINOR", issues.getSeverity());
        Assert.assertEquals(COMPONENT_NAME, issues.getComponent());
        Assert.assertEquals(81, issues.getLine());
        Assert.assertEquals("'System.Exception' should not be thrown by user code.", issues.getMessage());
    }

    @Test
    public void testIsHaveSonarqube() {
        when(sonarqubeRepository.findById(anyLong())).thenReturn(Optional.of(new Sonarqube()));
        Assert.assertTrue(sonarqubeService.isHaveSonarqube(1));

        when(sonarqubeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
        Assert.assertFalse(sonarqubeService.isHaveSonarqube(1));
    }
}
