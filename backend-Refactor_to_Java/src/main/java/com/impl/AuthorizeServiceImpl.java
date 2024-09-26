package com.impl;

import com.JwtHelper;
import com.bean.User;
import com.dao.UserRepository;
import com.dto.RequestGithubLoginDto;
import com.dto.ResponseAuthorizeDto;
import com.service.AuthorizeService;
import com.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("AuthorizeService")
public class AuthorizeServiceImpl extends BaseServiceImpl implements AuthorizeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public ResponseAuthorizeDto authenticateGithub (RequestGithubLoginDto loginDto) throws Exception {
//      code的內容由請求Github授權時生成，每次都會不一樣，這邊由前端Callback取得的Code進行後續AccessToken的取得請求
        String accessToken = this.requestGithubAccessToken(loginDto.getCode());

        if(!accessToken.isEmpty()){
            User user = requestGithubUserInfo(accessToken);
            if(!userService.checkUserExist(user.getAccount()))
                userService.createUser(user);
            ResponseAuthorizeDto result = new ResponseAuthorizeDto();
            result.setUserAccount(user.getAccount());
            result.setAuthority(user.getAuthority());
            result.setToken(jwtHelper.generateToken(user.getAccount(), accessToken, user.getAuthority()));

            return result;
        } else {
            throw new Exception("Error Code!");
        }
    }

    /**
     * Get GitHub's AccessToken for request User's Information
     */
    @Override
    public String requestGithubAccessToken(String code){
        String uri = "https://github.com/login/oauth/access_token";

//        TODO : C#此處以configuration撰寫，由於技術力不足，目前先寫死
        String clientId = "56979f30af5067f72ac4";
        String clientSecret = "8f837fb970ab4baf71b849900d6c9c0301a07e13";

        uri = uri + "?client_id=" + clientId + "&client_secret=" +
                clientSecret + "&code=" + code;

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>("body", headers);

        ResponseEntity<Map> response = restTemplate.
                exchange(uri, HttpMethod.POST, request, Map.class);

        String accessToken = response.getBody().get("access_token").toString();
        // String accessToken = response.getBody().getString("access_token");

        return accessToken;
    }

    /**
     * Get GitHub's UserInfo by accessToken
     */
    @Override
    public User requestGithubUserInfo(String accessToken) {
        String uri = "https://api.github.com/user";

//        Github 請求需要把參數安置在Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "request");
        HttpEntity<String> request = new HttpEntity<>("body", headers);

        ResponseEntity<String> responseByString = restTemplate.
                exchange(uri, HttpMethod.GET,request, String.class);
        JSONObject response = new JSONObject(responseByString.getBody());

        String account = "github_" + response.getString("login");
        String name = response.getString("login");
        String avatarUrl = response.getString("avatar_url");
        String authority = "User";

        User result = new User();
        result.setAccount(account);
        result.setName(name);
        result.setAvatarUrl(avatarUrl);
        result.setAuthority(authority);

        return result;
    }

}
