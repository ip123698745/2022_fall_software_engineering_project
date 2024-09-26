package com.controller;

import com.bean.Repo;
import com.dto.ResponseCodebaseDto;
import com.dto.ResponseCommitInfoDto;
import com.dto.ResponseContributorsActivityDto;
import com.dto.ResponseIssuesDto;
import com.service.RepoInfoService;
import com.service.SonarqubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RepoInfoController {
    private final List<RepoInfoService> repoInfoServices;
    private SonarqubeService sonarqubeService;

    @Autowired
    public RepoInfoController(List<RepoInfoService> repoInfoServices, SonarqubeService sonarqubeService) {
        this.repoInfoServices = repoInfoServices;
        this.sonarqubeService = sonarqubeService;
    }

    @GetMapping("/repoInfo/ishavesonarqube/{repoId}")
    public ResponseEntity<Boolean> isHaveSonarqube(@PathVariable long repoId) {
        try {
            return ResponseEntity.ok().body(
                    sonarqubeService.isHaveSonarqube(repoId)
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    false
            );
        }
    }

    @GetMapping("/repoInfo/contribute/{id}")
    public ResponseEntity<List<ResponseContributorsActivityDto>> getContributorsActivity(@PathVariable long id) {
        try {
            RepoInfoService repoInfoService = getRepoInfoService(id);
            Repo repo = repoInfoService.getRepoBy(id);
            List<ResponseContributorsActivityDto> responseDTOs = repoInfoService.requestContributorsActivity(repo);
            return ResponseEntity.ok().body(responseDTOs);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/repoInfo/issue/{id}")
    public ResponseEntity<ResponseIssuesDto> getIssue(@PathVariable long id) {
        try {
            RepoInfoService repoInfoService = getRepoInfoService(id);
            Repo repo = repoInfoService.getRepoBy(id);
            ResponseIssuesDto responseDTO = repoInfoService.requestIssue(repo);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/repoInfo/commit/{id}")
    public ResponseEntity<ResponseCommitInfoDto> getCommit(@PathVariable long id) {
        try {
            RepoInfoService repoInfoService = getRepoInfoService(id);
            Repo repo = repoInfoService.getRepoBy(id);
            ResponseCommitInfoDto responseDTO = repoInfoService.requestCommit(repo);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/repoInfo/codebase/{id}")
    public ResponseEntity<List<ResponseCodebaseDto>> getCodebase(@PathVariable long id) {
        try {
            RepoInfoService repoInfoService = getRepoInfoService(id);
            Repo repo = repoInfoService.getRepoBy(id);
            List<ResponseCodebaseDto> responseDTOs = repoInfoService.requestCodebase(repo);
            return ResponseEntity.ok().body(responseDTOs);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private RepoInfoService getRepoInfoService(long id) {
        return repoInfoServices.stream()
                .filter(repoInfoService -> repoInfoService.existRepoBy(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Repo not found"));
    }

    // TODO : exchange 時有機率會發生 Error while extracting response for type [java.util.List<java.util.List<java.lang.Integer>>] and content type [application/json;charset=utf-8]
    // TODO : 與 Team 1 討論，可能是 Github 那邊的問題，在請求時需不斷提出請求直到 Github 完成資料並回傳回來為止！

}