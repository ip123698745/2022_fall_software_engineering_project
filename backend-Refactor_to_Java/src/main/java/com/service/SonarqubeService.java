package com.service;

import com.bean.Sonarqube;
import com.dto.CodeSmellDataDto;
import com.dto.ResponseSonarqubeDto;

import java.util.List;
import java.util.Map;

public interface SonarqubeService extends BaseService{
    ResponseSonarqubeDto getSonarqubeInfo(long repoId);

    Map<String, List<CodeSmellDataDto.Issues>> getSonarqubeCodeSmell(long repoId);

    boolean isHaveSonarqube(long repoId);
}
