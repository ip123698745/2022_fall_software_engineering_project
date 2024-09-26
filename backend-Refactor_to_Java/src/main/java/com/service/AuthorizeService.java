package com.service;

import com.bean.User;
import com.dto.ResponseAuthorizeDto;
import com.dto.RequestGithubLoginDto;
import org.springframework.stereotype.Service;

@Service("AuthorizeService")
public interface  AuthorizeService extends BaseService{

    ResponseAuthorizeDto authenticateGithub (RequestGithubLoginDto loginDto) throws Exception;
    String requestGithubAccessToken(String code);
    User requestGithubUserInfo(String accessToken);
}