package com.controller;

import com.JwtHelper;
import com.bean.Repo;
import com.dto.AddRepoDto;
import com.dto.ResponseDto;
import com.impl.RepoServiceImpl;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RepoController {

    @Autowired
    private RepoServiceImpl repoService;

    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping("/repo/{projectId}")
    public ResponseEntity<List<Repo>> getRepoByProjectId(@PathVariable long projectId) {
        try {
            return ResponseEntity.ok().body(repoService.getRepoByProjectId(projectId));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @PostMapping("/repo")
    public ResponseEntity<ResponseDto> addRepo(@RequestBody AddRepoDto addRepoDto, @RequestHeader("Authorization") String authToken){
        try {
            JSONObject jsonObject = jwtHelper.validateToken(authToken);
            repoService.addRepo(addRepoDto, jsonObject.getString("sub"));
            return ResponseEntity.ok().body(
                    new ResponseDto(true, "Added Success")
            );
        } catch (Exception ex) {
            return ResponseEntity.ok().body(
                    new ResponseDto(false, ex.getMessage())
            );
        }
    }

    @DeleteMapping("/repo/{projectId}/{repoId}")
    public ResponseEntity<ResponseDto> deleteRepo(@PathVariable long projectId, @PathVariable long repoId, @RequestHeader("Authorization") String authToken){
        try {
            JSONObject jsonObject = jwtHelper.validateToken(authToken);
            repoService.deleteRepo(projectId, repoId, jsonObject.getString("sub"));
            return ResponseEntity.ok().body(
                    new ResponseDto(true, "Delete Success")
            );
        } catch (Exception ex) {
            return ResponseEntity.ok().body(
                    new ResponseDto(false, ex.getMessage())
            );
        }
    }
}