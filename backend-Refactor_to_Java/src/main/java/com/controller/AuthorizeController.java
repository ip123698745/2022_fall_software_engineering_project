package com.controller;

import com.dto.RequestGithubLoginDto;
import com.dto.ResponseAuthorizeDto;
import com.dto.ResponseSonarqubeDto;
import com.service.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizeController {

    @Autowired
    @Qualifier("AuthorizeService")
    private AuthorizeService authorizeService;

    @PostMapping("/authorize/github")
    public ResponseEntity<ResponseAuthorizeDto> authenticateGithub(@RequestBody RequestGithubLoginDto code){
        try {
            return ResponseEntity.ok().body(
                    authorizeService.authenticateGithub(code)
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

}
