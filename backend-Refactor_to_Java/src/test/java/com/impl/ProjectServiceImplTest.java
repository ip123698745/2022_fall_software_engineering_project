package com.impl;

import com.Application;
import com.bean.Project;
import com.bean.User;
import com.dao.ProjectRepository;
import com.dao.UserRepository;
import com.dto.RequestProjectDto;
import com.dto.ResponseProjectResultDto;
import com.dto.ResponseUserInfoDto;
import com.exception.ProjectNotFoundException;
import com.service.ProjectService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ProjectServiceImplTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testGetProjectMemberFromProjectModelByExistingMember() {
        User user1 = new User("1", "testUser1", null, null, null, new ArrayList<>(), new ArrayList<>());
        User user2 = new User("2", "testUser2", null, null, null, new ArrayList<>(), new ArrayList<>());
        User user3 = new User("3", "testUser3", null, null, null, new ArrayList<>(), new ArrayList<>());

        List<User> users = List.of(
                user1,
                user2,
                user3
        );

        //name = anyString() raise exception
        Project project = new Project(1, "testProject", user1, users, null, null, null, null);
        when(projectRepository.getById(project.getId())).thenReturn(project);
        List<ResponseUserInfoDto> actual = projectService.getProjectMember(project.getId());

        Assert.assertEquals(3, actual.size());
        Assert.assertEquals("testUser1", actual.get(0).getName());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testGetProjectMemberFromProjectModelByNotExistingMember() {
        long projectId = 1;
        when(projectRepository.findById(projectId)).thenReturn(null);

        projectService.getProjectMember(projectId);
    }

    @Test
    public void testGetProjectFromOwnerByExistingProjectId() {
        User user1 = new User("1", "testUser1", null, null, null, new ArrayList<>(), new ArrayList<>());
        User user2 = new User("2", "testUser2", null, null, null, new ArrayList<>(), new ArrayList<>());

        List<User> users = List.of(
                user1,
                user2
        );
        Project project = new Project(1, "testProject", user1, users, null, null, null, null);

        when(projectRepository.getOwnerByAccount(user1.getAccount())).thenReturn(
                new User("1", "testUser1", null, null, null, List.of(project), List.of(project))
        );

        var actual = projectService.getProjectIfUserHaveProject(project.getId(), user1.getAccount());
        Assert.assertEquals("testProject", actual.getName());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testGetProjectFromOwnerDoNotOwnProject() {
        User user1 = new User("1", "testUser1", null, null, null, new ArrayList<>(), new ArrayList<>());
        User user2 = new User("2", "testUser2", null, null, null, new ArrayList<>(), new ArrayList<>());

        List<User> users = List.of(
                user1,
                user2
        );
        Project project = new Project(1, "testProject", user1, users, null, null, null, null);

        when(projectRepository.getOwnerByAccount(user2.getAccount())).thenReturn(
                null
        );

        projectService.getProjectIfUserHaveProject(project.getId(), user2.getAccount());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testGetProjectFromNotExistingProject() {
        User user1 = new User("1", "testUser1", null, null, null, new ArrayList<>(), new ArrayList<>());
        User user2 = new User("2", "testUser2", null, null, null, new ArrayList<>(), new ArrayList<>());

        List<User> users = List.of(
                user1,
                user2
        );
        Project project = new Project(1, "testProject", user1, users, null, null, null, null);

        when(projectRepository.getOwnerByAccount(user2.getAccount())).thenReturn(
                new User("1", "testUser1", null, null, null, List.of(project), List.of(project))
        );

        projectService.getProjectIfUserHaveProject(3, user2.getAccount());
    }

    @Test
    public void testGetAllProject() {
        User user1 = new User("1", "testUser1", null, null, null, new ArrayList<>(), new ArrayList<>());
        User user2 = new User("2", "testUser2", null, null, null, new ArrayList<>(), new ArrayList<>());

        List<User> users = List.of(
                user1,
                user2
        );

        List<Project> projects = List.of(
                new Project(1, "testProject1", user1, users, null, null, null, null),
                new Project(2, "testProject2", user1, users, null, null, null, null)
        );
        when(projectRepository.findAllProject()).thenReturn(projects);
        List<ResponseProjectResultDto> result = projectService.getAllProject();

        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testIsProjectOwnerWithProjectOwner() {
        Project project = new Project(1, null, null, null, null, null, null, null);
        User user = new User("test", null, null, null, null, List.of(project), new ArrayList<>());
        project.setOwner(user);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        boolean actual = projectService.isProjectOwner(project.getId(), user.getAccount());

        Assertions.assertTrue(actual);
    }

    @Test()
    public void testGetProjectById() {
        var expected = new Project(1, "test", null, null, null, null, null, null);
        when(projectRepository.getById(expected.getId())).thenReturn(expected);

        Project actual = projectService.getProjectById(1L);

        Assertions.assertEquals(expected, actual);
    }

    @Test()
    public void testSaveProject() {
        var expected = new Project(1, "test", null, null, null, null, null, null);

        projectService.saveProject(expected);

        verify(projectRepository).save(expected);
    }

    @Test(expected = Exception.class)
    public void testCreateProjectWithEmptyProjectName() throws Exception {
        RequestProjectDto requestProjectDto = new RequestProjectDto(0, "");

        projectService.createProject(requestProjectDto, null);
    }

    @Test(expected = Exception.class)
    public void testCreateProjectWithInvalidProjectName() throws Exception {
        RequestProjectDto requestProjectDto = new RequestProjectDto(0, "@$#@$");

        projectService.createProject(requestProjectDto, null);
    }

    @Test(expected = Exception.class)
    public void testCreateProjectWithNoUser() throws Exception {
        RequestProjectDto requestProjectDto = new RequestProjectDto(0, "test");
        when(userRepository.getById(anyString())).thenReturn(null);

        projectService.createProject(requestProjectDto, "");
    }

    @Test(expected = Exception.class)
    public void testCreateProjectWithDuplicateProjectName() throws Exception {
        RequestProjectDto requestProjectDto = new RequestProjectDto(0, "test");
        User user = new User();
        user.getProjects().add(new Project() {{
            setName("test");
        }});
        when(userRepository.getById(anyString())).thenReturn(user);

        projectService.createProject(requestProjectDto, "");
    }

    @Test
    public void testCreateProjectSuccess() throws Exception {
        RequestProjectDto requestProjectDto = new RequestProjectDto(0, "test");
        User user = new User();
        when(userRepository.getById(anyString())).thenReturn(user);

        projectService.createProject(requestProjectDto, "");

        verify(projectRepository).save(argThat(project -> project.getName().equals("test")));
    }

    @Test(expected = Exception.class)
    public void testDeleteProjectWithNullProject() {
        when(projectRepository.getById(anyLong())).thenReturn(null);

        projectService.deleteProject(1L);
    }

    @Test()
    public void testDeleteProject() {
        long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        User user = new User();
        user.getProjects().add(project);
        project.getUsers().add(user);
        when(projectRepository.getById(anyLong())).thenReturn(project);

        projectService.deleteProject(projectId);

        user.getProjects().clear();
        verify(userRepository).save(user);
        verify(projectRepository).delete(project);
    }

    @Test
    public void deleteProjectMemberSuccess() {
        Project project = new Project(1L, "test", null, null, null, null, null, null);
        List<Project> projects = new ArrayList<>(Arrays.asList(project));
        User user = new User("testUser", null, null, null, null, projects, new ArrayList<>());
        List<User> users = new ArrayList<>(Arrays.asList(user));
        user.setProjects(projects);
        project.setUsers(users);

        when((userRepository.getById("testUser"))).thenReturn(user);

        assertTrue(projectService.deleteProjectMember(user.getAccount(), 1L));
        verify(userRepository).save(user);
        verify(projectRepository).save(project);
    }

    @Test
    public void deleteProjectMemberFailed() {
        List<Project> projects = new ArrayList<>(Arrays.asList(new Project(2L, "test", null, null, null, null, null, null)));
        User user = new User("testUser", null, null, null, null, projects, new ArrayList<>());
        user.setProjects(projects);
        when((userRepository.getById("testUser"))).thenReturn(user);

        assertFalse(projectService.deleteProjectMember(user.getAccount(), 1L));
    }

    @Test
    public void testGetProjectByIdForProjectResult() {
        User owner = new User();
        owner.setName("owner");
        owner.setAccount("owner_acc");
        long projectId = 1L;
        String projectName = "test";
        Project project = new Project();
        project.setId(projectId);
        project.setName(projectName);
        project.setOwner(owner);
        project.setUsers(new ArrayList<>());
        when(projectRepository.getById(anyLong())).thenReturn(project);
        ResponseProjectResultDto actual = projectService.getProjectByIdForProjectResult(projectId);
        Assertions.assertEquals(projectId, actual.getId());
        Assertions.assertEquals(projectName, actual.getName());
        Assertions.assertEquals(owner.getAccount(), actual.getOwnerId());
        Assertions.assertEquals(owner.getName(), actual.getOwnerName());
        Assertions.assertEquals(project.getUsers().size(), actual.getMembers());
    }
}
