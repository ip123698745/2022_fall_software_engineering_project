package com.service;

import com.bean.Repo;
import com.dto.AddRepoDto;

import java.sql.SQLException;
import java.util.List;

public interface RepoService extends BaseService{
    List<Repo> getRepoByProjectId(long projectId);

    void addRepo(AddRepoDto repo, String projectOwner) throws Exception;

    boolean deleteRepo(long projectId, long repoId, String projectOwner) throws Exception;
}
