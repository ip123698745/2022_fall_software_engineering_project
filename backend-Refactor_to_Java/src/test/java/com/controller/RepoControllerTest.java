package com.controller;

import com.Application;
import com.bean.GitLab;
import com.bean.Repo;
import com.dto.AddRepoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.RepoInfoService;
import com.service.RepoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)

public class RepoControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private RepoService repoService;

    @Autowired
    private RepoController repoController;

    @Before
    public void setUp() {
        this.mockMvc = standaloneSetup(this.repoController).build();
    }

    @Test
    public void addRepoSuccess() throws Exception {
        String autoToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwODM0MjE3Ny02NjE2LTRlMmYtOWE5ZC03ZDZkZTZjZTM4MWQiLCJpYXQiOjE2NzI2NjI3NzAsInN1YiI6ImdpdGh1Yl9hMDkzNTIxMDU3MDYwMiIsImlzcyI6Ikp3dEF1dGgiLCJleHAiOjE2NzI2NjMzNzB9.bIVcjkKIlp7TJ9SInEuFW9_zDa5F_hlB_MEE_Kt-cLY";
        //AddRepoDto(projectId=1, url=123, isSonarqube=false, accountColonPassword=OjEyMw==, sonarqubeUrl=, projectKey=)

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", autoToken);

        AddRepoDto addRepoDto = new AddRepoDto(1, "123", false, null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(addRepoDto);
        } catch (JsonProcessingException e) {
            // handle exception
        }

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/repo")
                .header("Authorization", autoToken)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON);

        RepoService mock = mock(RepoService.class);
        doNothing().when(mock).addRepo(addRepoDto, "github_a0935210570602");

        // TODO: fix: org.hibernate.LazyInitializationException: could not initialize proxy [com.bean.Project#1] - no Session
        mockMvc.perform(requestBuilder).andExpect(status().isOk());
//        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

//        MockHttpServletResponse response = result.getResponse();
//        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
}
