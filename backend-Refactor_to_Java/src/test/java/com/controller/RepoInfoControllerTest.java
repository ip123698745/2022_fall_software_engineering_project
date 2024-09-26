package com.controller;

import com.Application;
import com.bean.GitLab;
import com.bean.Repo;
import com.service.RepoInfoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RepoInfoControllerTest {

    private MockMvc mockMvc;
    private Repo stubRepo;

    @Autowired
    private RepoInfoController repoInfoController;

    @MockBean
    @Qualifier("gitlabInfoService")
    private RepoInfoService gitlabInfoService;

    @Before
    public void setUp() {
        this.mockMvc = standaloneSetup(this.repoInfoController).build();
        this.stubRepo = new GitLab();
        this.stubRepo.setId(45);
        when(gitlabInfoService.existRepoBy(anyLong())).thenReturn(true);
        when(gitlabInfoService.getRepoBy(anyLong())).thenReturn(stubRepo);
    }

    @Test
    public void testGetContributorsActivityIsOk() throws Exception {
        when(this.gitlabInfoService.requestContributorsActivity(stubRepo)).thenReturn(any());

        mockMvc.perform(get("/repoInfo/contribute/45"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetContributorsActivityIsBadRequest() throws Exception {
        when(this.gitlabInfoService.requestContributorsActivity(stubRepo)).thenThrow();

        mockMvc.perform(get("/repoInfo/contribute/45"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetIssueIsOk() throws Exception {
        when(this.gitlabInfoService.requestIssue(stubRepo)).thenReturn(any());

        mockMvc.perform(get("/repoInfo/issue/45"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIssueIsBadRequest() throws Exception {
        when(this.gitlabInfoService.requestIssue(stubRepo)).thenThrow();

        mockMvc.perform(get("/repoInfo/issue/45"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCommitIsOk() throws Exception {
        when(this.gitlabInfoService.requestCommit(stubRepo)).thenReturn(any());

        mockMvc.perform(get("/repoInfo/commit/45"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCommitIsBadRequest() throws Exception {
        when(this.gitlabInfoService.requestCommit(stubRepo)).thenThrow();

        mockMvc.perform(get("/repoInfo/commit/45"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCodebaseIsOk() throws Exception {
        when(this.gitlabInfoService.requestCodebase(stubRepo)).thenReturn(any());

        mockMvc.perform(get("/repoInfo/codebase/45"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCodebaseIsBadRequest() throws Exception {
        when(this.gitlabInfoService.requestCodebase(stubRepo)).thenThrow();

        mockMvc.perform(get("/repoInfo/codebase/45"))
                .andExpect(status().isBadRequest());
    }
}