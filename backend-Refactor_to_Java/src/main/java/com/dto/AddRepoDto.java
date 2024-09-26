package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AddRepoDto {
    public long projectId;
    public String url;
    public boolean isSonarqube;
    public String accountColonPassword;
    public String sonarqubeUrl;
    public String projectKey;
}
