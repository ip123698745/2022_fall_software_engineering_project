package com.controller;

import com.JwtHelper;
import com.dto.ResponseProjectResultDto;
import com.dto.RequestProjectDto;
import com.dto.ResponseDto;
import com.dto.ResponseUserInfoDto;
import com.exception.ProjectDeleteException;
import com.service.ProjectService;
import com.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {

    @Autowired
    @Qualifier("ProjectService")
    private ProjectService projectService;

    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping("/project/")
    public ResponseEntity<List<ResponseProjectResultDto>> getProjects(@RequestHeader("Authorization") String authToken) {
        JSONObject jsonObject = jwtHelper.validateToken(authToken);
        try {
            return ResponseEntity.ok().body(
                    projectService.getProjectByOwnerAccount(jsonObject.getString("sub"))
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ResponseProjectResultDto> getProjects(@PathVariable long projectId) {
        try {
            return ResponseEntity.ok().body(
                    projectService.getProjectByIdForProjectResult(projectId)
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/project/add")
    public ResponseEntity<ResponseDto> addProject(@RequestHeader("Authorization") String authToken, @RequestBody RequestProjectDto requestProjectDto) {
        try {
            JSONObject jsonObject = jwtHelper.validateToken(authToken);
            projectService.createProject(requestProjectDto, jsonObject.getString("sub"));

            return ResponseEntity.ok().body(
                    new ResponseDto(true, "Added Success")
            );
        } catch (Exception ex) {
            return ResponseEntity.ok().body(
                    new ResponseDto(false, ex.getMessage())
            );
        }
    }

    @DeleteMapping("/project/{projectId}/{userId}")
    public ResponseEntity<ResponseDto> deleteProject(@RequestHeader("Authorization") String authToken, @PathVariable long projectId, @PathVariable String userId) {
        try {
            // TODO : 檢查非專案擁有者刪除是否報錯
            if(projectService.isProjectOwner(projectId, userId)) {
                projectService.deleteProject(projectId);
                return ResponseEntity.ok().body(
                        new ResponseDto(true, "Deleted Success")
                );
            } else {
                throw new ProjectDeleteException("You're not owner");
            }
        } catch (Exception ex) {
            return ResponseEntity.ok().body(
                    new ResponseDto(false, ex.getMessage())
            );
        }
    }

    @GetMapping("/project/member/{projectId}")
    public ResponseEntity<List<ResponseUserInfoDto>> getProjectMember(@PathVariable long projectId) {
        try {
            return ResponseEntity.ok().body(projectService.getProjectMember(projectId));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @DeleteMapping("/project/member/{projectId}/{userId}")
    public ResponseEntity<ResponseDto> getProjectMember(@PathVariable long projectId, @PathVariable String userId) {
        try {
            if (projectService.deleteProjectMember(userId, projectId)) {
                return ResponseEntity.ok().body(
                        new ResponseDto(true, "Deleted success")
                );
            }
            throw new Exception("User didn't exist");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    new ResponseDto(false, ex.getMessage())
            );
        }
    }

    // TODO : Edit Project Name, Admin
}
