package com.service;

import com.bean.Project;
import com.dto.RequestProjectDto;
import com.dto.ResponseProjectResultDto;
import com.dto.ResponseUserInfoDto;

import java.util.List;

public interface ProjectService extends BaseService {
    Project getProjectById(long projectId);

    ResponseProjectResultDto getProjectByIdForProjectResult(long projectId);

    List<ResponseUserInfoDto> getProjectMember(long projectId);

    ResponseProjectResultDto getProjectIfUserHaveProject(long projectId, String account);

    List<ResponseProjectResultDto> getAllProject();

    boolean deleteProjectMember(String account, long projectId);

    boolean isProjectOwner(long projectId, String account);

    List<ResponseProjectResultDto> getProjectByOwnerAccount(String account);

    void createProject(RequestProjectDto requestProjectDto, String userAccount) throws Exception;

    void deleteProject(long projectId);

    void saveProject(Project project);
}
