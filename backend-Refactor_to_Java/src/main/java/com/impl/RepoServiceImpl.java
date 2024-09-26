package com.impl;

import com.bean.*;
import com.dao.ProjectRepository;
import com.dto.AddRepoDto;
import com.service.RepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("RepositoryService")
public class RepoServiceImpl implements RepoService {

    private final ProjectRepository projectRepository;

    @Autowired
    public RepoServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Repo> getRepoByProjectId(long projectId) {
        List<Jira> jiras = projectRepository.getById(projectId).getJiras();
        List<Sonarqube> sonarqubes = projectRepository.getById(projectId).getSonarqubes();
        List<GitHub> githubs = projectRepository.getById(projectId).getGitHubs();
        List<GitLab> gitlabs = projectRepository.getById(projectId).getGitLabs();

        // TODO getById may be null, so we must add try catch or addALL func throw error
        List<Repo> repo = new ArrayList<>();
        repo.addAll(jiras);
        repo.addAll(sonarqubes);
        repo.addAll(githubs);
        repo.addAll(gitlabs);
        return repo;
    }

    // TODO 新增 Sonarqube 還未實作
    public void addRepo(AddRepoDto addRepoDto, String projectOwner) throws Exception {
        if (addRepoDto.isSonarqube) { // Sonarqube
            throw new Exception("Service not support.");
        }

        Project project = projectRepository.getById(addRepoDto.projectId);
        if (!project.getOwner().getAccount().equals(projectOwner)) {
            throw new Exception("You're not Project owner!!!");
        }

        Pattern githubPattern = Pattern.compile("^https://github.com/(.*)/(.*)$");
        Pattern gitlabPattern = Pattern.compile("^https://.*(gitlab)+.*/(.*)/(.*)/?$");
        Matcher githubMatcher = githubPattern.matcher(addRepoDto.url);
        Matcher gitlabMatcher = gitlabPattern.matcher(addRepoDto.url);

        if (githubMatcher.find()) {
            GitHub github = new GitHub();
            github.setUrl(addRepoDto.getUrl());
            github.setName(githubMatcher.group(2));
            github.setOwner(githubMatcher.group(1));
            project.getGitHubs().add(github);
        }

        if (gitlabMatcher.find()) {
            GitLab gitlab = new GitLab();
            gitlab.setUrl(addRepoDto.getUrl());
            gitlab.setOwner(gitlabMatcher.group(2));
            gitlab.setName(gitlabMatcher.group(3));
            project.getGitLabs().add(gitlab);
        }
        projectRepository.save(project);
    }

    // TODO 刪除 Sonarqube 還未實作
    public boolean deleteRepo(long projectId, long repoId, String projectOwner) throws Exception {
        Project project = projectRepository.getById(projectId);
        if(!project.getOwner().getAccount().equals(projectOwner)) {
            throw new Exception("You're not Project owner!!!");
        }

        // Gitlab
        List<GitLab> gitlabs = project.getGitLabs();
        for(GitLab gitlab: gitlabs) {
            if(gitlab.getId() == repoId) {
                gitlabs.remove(gitlab);
                projectRepository.save(project);
                return true;
            }
        }

        // Github
        List<GitHub> githubs = project.getGitHubs();
        for(GitHub github: githubs) {
            if(github.getId() == repoId) {
                githubs.remove(github);
                projectRepository.save(project);
                return true;
            }
        }

        // Jira
        List<Jira> jiras = project.getJiras();
        for(Jira jira: jiras) {
            if(jira.getId() == repoId) {
                jiras.remove(jira);
                projectRepository.save(project);
                return true;
            }
        }

        throw new Exception("Not found Repo");
    }
}
