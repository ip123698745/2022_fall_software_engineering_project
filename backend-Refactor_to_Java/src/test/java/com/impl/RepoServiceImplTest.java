package com.impl;

import com.Application;
import com.bean.*;
import com.dao.*;
import com.dto.AddRepoDto;
import com.service.RepoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RepoServiceImplTest {

    @Autowired
    private RepoService repoService;

    @MockBean
    private ProjectRepository projectRepository;
    @MockBean
    private SonarqubeRepository sonarqubeRepository;
    @MockBean
    private JiraRepository jiraRepository;
    @MockBean
    private GitHubRepository githubRepository;
    @MockBean
    private GitLabRepository gitlabRepository;

    @Test(expected = Exception.class)
    public void testAddRepoFaidedByServiceNotSupport() throws Exception {
        repoService.addRepo(new AddRepoDto(1L, null, true, null, null, null), null);
    }

    @Test
    public void testAddRepoShouldAddGitHub() throws Exception {
        String name = "googletest";
        String owner = "google";
        String url = String.format("https://github.com/%s/%s", owner, name);
        Project project = new Project();
        User user = new User();
        user.setAccount("testUser");
        project.setOwner(user);
        when(projectRepository.getById(anyLong())).thenReturn(project);

        AddRepoDto addRepoDto = new AddRepoDto();
        addRepoDto.url = url;
        addRepoDto.projectId = project.getId();
        repoService.addRepo(addRepoDto, "testUser");

        ArgumentCaptor<Project> actualCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(actualCaptor.capture());
        Project actualProject = actualCaptor.getValue();
        assertFalse(actualProject.getGitHubs().isEmpty());
        GitHub actual = actualProject.getGitHubs().get(0);
        assertEquals(name, actual.getName());
        assertEquals(owner, actual.getOwner());
    }

    @Test
    public void testAddRepoShouldAddGitLab() throws Exception {
        String name = "backend";
        String owner = "ptt4pbl";
        String url = String.format("https://service.selab.ml/gitlab/%s/%s", owner, name);
        Project project = new Project();
        User user = new User();
        user.setAccount("testUser");
        project.setOwner(user);
        when(projectRepository.getById(anyLong())).thenReturn(project);

        AddRepoDto addRepoDto = new AddRepoDto();
        addRepoDto.url = url;
        addRepoDto.projectId = project.getId();
        repoService.addRepo(addRepoDto, "testUser");

        ArgumentCaptor<Project> actualCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(actualCaptor.capture());
        Project actualProject = actualCaptor.getValue();
        assertFalse(actualProject.getGitLabs().isEmpty());
        GitLab actual = actualProject.getGitLabs().get(0);
        assertEquals(name, actual.getName());
        assertEquals(owner, actual.getOwner());
    }

    @Test(expected = Exception.class)
    public void testAddRepoShouldThrowExceptionWhenProjectOwnerNotCorrect() throws Exception {
        String name = "googletest";
        String owner = "google";
        String url = String.format("https://github.com/%s/%s", owner, name);
        Project project = new Project();
        User user = new User();
        user.setAccount("testUser");
        project.setOwner(user);
        when(projectRepository.getById(anyLong())).thenReturn(project);

        AddRepoDto addRepoDto = new AddRepoDto(0, url, false, null, null, null);
        repoService.addRepo(addRepoDto, "timtimno1");
    }

    @Test(expected = Exception.class)
    public void testDeleteRepoShouldThrowExceptionWithNotProjectOwner() throws Exception {
        long projectId = 0;
        long deleteRepoId = 0;
        String projectOwner = "user1";
        User owner = new User();
        owner.setAccount(projectOwner);
        Project project = new Project();
        project.setOwner(owner);
        project.setId(0);
        when(projectRepository.getById(projectId)).thenReturn(project);

        repoService.deleteRepo(projectId, deleteRepoId, "user2");
    }

    @Test()
    public void testDeleteRepoShouldDeleteGitHub() throws Exception {
        long repoId = 0;
        GitHub gitHub = new GitHub();
        gitHub.setId(repoId);
        Project project = new Project();
        project.setOwner(new User() {{
            setAccount("user1");
        }});
        project.setGitHubs(new ArrayList<>() {{
            add(gitHub);
        }});
        when(projectRepository.getById(project.getId())).thenReturn(project);

        boolean result = repoService.deleteRepo(project.getId(), repoId, project.getOwner().getAccount());

        assertTrue(result);
        ArgumentCaptor<Project> actualCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(actualCaptor.capture());
        Project actual = actualCaptor.getValue();
        assertTrue(actual.getGitHubs().isEmpty());
    }

    @Test()
    public void testDeleteRepoShouldDeleteGitLab() throws Exception {
        long repoId = 0;
        GitLab gitLab = new GitLab();
        gitLab.setId(repoId);
        Project project = new Project();
        project.setOwner(new User() {{
            setAccount("user1");
        }});
        project.setGitLabs(new ArrayList<>() {{
            add(gitLab);
        }});
        when(projectRepository.getById(project.getId())).thenReturn(project);

        boolean result = repoService.deleteRepo(project.getId(), repoId, project.getOwner().getAccount());

        assertTrue(result);
        ArgumentCaptor<Project> actualCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(actualCaptor.capture());
        Project actual = actualCaptor.getValue();
        assertTrue(actual.getGitLabs().isEmpty());
    }

    @Test()
    public void testDeleteRepoShouldDeleteJira() throws Exception {
        long repoId = 0;
        Jira jira = new Jira();
        jira.setId(repoId);
        Project project = new Project();
        project.setOwner(new User() {{
            setAccount("user1");
        }});
        project.setJiras(new ArrayList<>() {{
            add(jira);
        }});
        when(projectRepository.getById(project.getId())).thenReturn(project);

        boolean result = repoService.deleteRepo(project.getId(), repoId, project.getOwner().getAccount());

        assertTrue(result);
        ArgumentCaptor<Project> actualCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(actualCaptor.capture());
        Project actual = actualCaptor.getValue();
        assertTrue(actual.getJiras().isEmpty());
    }

    // TODO　should add repo not existing  test
    @Test()
    public void getRepoByProjectId() {
        List<Jira> jiras = new ArrayList<>(Arrays.asList(new Jira(null, 1L, null, null)));
        List<Sonarqube> sonarqubes = new ArrayList<>(Arrays.asList(new Sonarqube(null)));
        List<GitHub> gitHubs = new ArrayList<>(Arrays.asList(new GitHub(null, null)));
        List<GitLab> gitLabs = new ArrayList<>(Arrays.asList(new GitLab(null, null)));

        when(projectRepository.getById(1L)).thenReturn(new Project(1, "testProject", null, null, jiras, sonarqubes, gitLabs, gitHubs));
        assertEquals(4, repoService.getRepoByProjectId(1L).size());
    }

    @Test(expected = Exception.class)
    public void testAddRepoWithSonarqube() throws Exception {
        AddRepoDto addRepoDto = new AddRepoDto(0, null, true, null, null, null);
        repoService.addRepo(addRepoDto, null);
    }

    @Test()
    public void testGetRepoByProjectId() {
        var gitHub = new GitHub();
        var gitLab = new GitLab();
        var jira = new Jira();
        var sonarqube = new Sonarqube();
        List<Repo> expected = new ArrayList<>();
        expected.add(gitHub);
        expected.add(gitLab);
        expected.add(jira);
        expected.add(sonarqube);
        long projectId = 0;
        Project project = new Project();
        project.setId(projectId);
        project.getGitHubs().add(gitHub);
        project.getGitLabs().add(gitLab);
        project.getJiras().add(jira);
        project.getSonarqubes().add(sonarqube);
        when(projectRepository.getById(projectId)).thenReturn(project);

        List<Repo> actual = repoService.getRepoByProjectId(projectId);

        verify(projectRepository, times(4)).getById(projectId);
        assertEquals(4, actual.size());
        expected.forEach(repo -> assertTrue(actual.contains(repo)));
    }

    @Test(expected = Exception.class)
    public void testDeleteRepoFailed() throws Exception {
        var project = new Project();
        User owner = new User() {{
            setAccount("user1");
        }};
        project.setOwner(owner);
        project.getGitHubs().add(new GitHub());
        project.getGitLabs().add(new GitLab());
        project.getJiras().add(new Jira());
        when(projectRepository.getById(anyLong())).thenReturn(project);

        boolean result = repoService.deleteRepo(0, -1, owner.getAccount());
    }
}