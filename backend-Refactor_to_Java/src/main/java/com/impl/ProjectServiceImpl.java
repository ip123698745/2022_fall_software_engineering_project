package com.impl;

import com.bean.*;
import com.dao.ProjectRepository;
import com.dao.UserRepository;
import com.dto.RequestProjectDto;
import com.dto.ResponseProjectResultDto;
import com.dto.ResponseUserInfoDto;
import com.exception.ProjectNotFoundException;
import com.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Service("ProjectService")
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectServiceImpl(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public Project getProjectById(long projectId) {
        return projectRepository.getById(projectId);
    }

    @Override
    public ResponseProjectResultDto getProjectByIdForProjectResult(long projectId) {
        Project project = projectRepository.getById(projectId);
        return new ResponseProjectResultDto(
                project.getId(),
                project.getName(),
                project.getOwner().getAccount(),
                project.getOwner().getName(),
                project.getUsers().size()
        );
    }

    @Override
    public void createProject(RequestProjectDto requestProjectDto, String userAccount) throws Exception {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+");
        Matcher matcher = pattern.matcher(requestProjectDto.getProjectName());
        if (requestProjectDto.getProjectName().equals(""))
            throw new Exception("Please enter a project name");
        else if (!matcher.find())
            throw new Exception(String.format("The new name \"%s\" does not match the regex \"%s\"",
                    requestProjectDto.getProjectName(),
                    matcher.pattern().pattern()));

        User user = userRepository.getById(userAccount);

        if (user != null) {
            List<Project> projects = user.getProjects().stream()
                    .filter(p -> p.getName().equals(requestProjectDto.getProjectName()))
                    .collect(toList());

            if (projects.size() == 0) {
                Project newProject = new Project();
                List<User> users = new ArrayList<>();
                newProject.setName(requestProjectDto.getProjectName());
                newProject.setOwner(user);
                users.add(user);
                newProject.setUsers(users);
                newProject.setGitHubs(new ArrayList<>());
                newProject.setGitLabs(new ArrayList<>());
                newProject.setJiras(new ArrayList<>());
                projectRepository.save(newProject);

                user.getProjects().add(newProject);
                userRepository.save(user);
            } else {
                throw new Exception("duplicate project name");
            }
        } else {
            throw new Exception("user fail, can not find this user");
        }
    }

    @Override
    public void deleteProject(long projectId) {
        Project project = projectRepository.getById(projectId);
        if (project != null) {
            List<User> projectMembers = project.getUsers();
            for (User user : projectMembers) {
                List<Project> userProjects = user.getProjects();
                for (int i = 0; i < userProjects.size(); i++) {
                    if (userProjects.get(i).getId() == projectId) {
                        userProjects.remove(i);
                        break;
                    }
                }
                userRepository.save(user);
            }
            // Project 刪除時，必須把關聯資料給清除，才不會把關聯資料通通刪除
            project.setOwner(null);
            project.setUsers(new ArrayList<>());
            projectRepository.delete(project);
        } else {
            throw new ProjectNotFoundException(projectId);
        }
    }

    @Override
    public void saveProject(Project project) {
        projectRepository.save(project);
    }

    @Override
    public List<ResponseUserInfoDto> getProjectMember(long projectId) {
        Project project = projectRepository.getById(projectId);

        if (project != null) {
            List<User> projectMembers = project.getUsers();

            List<ResponseUserInfoDto> result = new ArrayList<>();

            for (User user : projectMembers) {
                result.add(new ResponseUserInfoDto(user.getAccount(), user.getName(), user.getAvatarUrl()));
            }

            return result;
        }

        throw new ProjectNotFoundException(projectId);
    }

    @Override
    public ResponseProjectResultDto getProjectIfUserHaveProject(long projectId, String account) {
        List<ResponseProjectResultDto> projectResultDtos = this.getProjectByOwnerAccount(account);

        for (ResponseProjectResultDto projectResultDto : projectResultDtos) {
            if (projectResultDto.getId() == projectId)
                return projectResultDto;
        }

        throw new ProjectNotFoundException(projectId);
    }

    @Override
    public List<ResponseProjectResultDto> getProjectByOwnerAccount(String account) {

        User user = projectRepository.getOwnerByAccount(account);
        List<ResponseProjectResultDto> projectResultDtos = new ArrayList<>();
        if (user != null) {
            for (Project project : user.getProjects()) {
                User u = project.getOwner();
                projectResultDtos.add(new ResponseProjectResultDto(project.getId(), project.getName(), u.getAccount(), u.getName(), project.getUsers().size()));
            }
            return projectResultDtos;
        }
        throw new ProjectNotFoundException(account);
    }

    @Override
    public List<ResponseProjectResultDto> getAllProject() {

        List<Project> projects = projectRepository.findAllProject();
        List<ResponseProjectResultDto> projectResultDtos = new ArrayList<>();
        for (Project project : projects) {
            projectResultDtos.add(new ResponseProjectResultDto(project.getId(), project.getName(), project.getOwner().getAccount(), project.getOwner().getName(), project.getUsers().size()));
        }

        return projectResultDtos;
    }

    @Override
    public boolean deleteProjectMember(String account, long projectId) {
        User user = userRepository.getById(account);
        List<Project> projects = user.getProjects();
        for (Project project : projects) {
            if (project.getId() == projectId) {
                projects.remove(project);
                userRepository.save(user);
                project.getUsers().remove(user);
                projectRepository.save(project);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isProjectOwner(long projectId, String account) {
        Project project = projectRepository.findById(projectId).orElseThrow(RuntimeException::new);
        return project.getOwner()
                .getAccount()
                .equals(account);
    }
}
