package com.impl;

import com.Application;
import com.JwtHelper;
import com.bean.User;
import com.dto.RequestGithubLoginDto;
import com.dto.ResponseAuthorizeDto;
import com.service.AuthorizeService;
import com.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
    public class AuthorizeServiceImplTest {

    @SpyBean
    private AuthorizeService authorizeService;

    @SpyBean
    private UserService userService;

    @SpyBean
    private JwtHelper jwtHelper;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testAuthenticateGithubSuccess() throws Exception {
        RequestGithubLoginDto loginDto = new RequestGithubLoginDto("test code");
        String accessToken = "test";
        doReturn(accessToken)
                .when(authorizeService).requestGithubAccessToken(loginDto.getCode());

        User user = new User();
        user.setAccount("account");
        user.setAuthority("authority");
        doReturn(user)
                .when(authorizeService).requestGithubUserInfo(accessToken);
        doReturn(true)
                .when(userService).checkUserExist(user.getAccount());
        doReturn(user).when(userService).createUser(user);

        ResponseAuthorizeDto responseAuthorizeDto = authorizeService.authenticateGithub(loginDto);

        Assert.assertEquals("account", responseAuthorizeDto.getUserAccount());
        Assert.assertEquals("authority", responseAuthorizeDto.getAuthority());
        Assert.assertEquals(215, responseAuthorizeDto.getToken().length());
    }

    @Test
    public void testAuthenticateGithubSuccessAndCreateUser() throws Exception {
        RequestGithubLoginDto loginDto = new RequestGithubLoginDto("test code");
        String accessToken = "test";
        doReturn(accessToken)
                .when(authorizeService).requestGithubAccessToken(loginDto.getCode());

        User user = new User();
        user.setAccount("account");
        user.setAuthority("authority");
        doReturn(user)
                .when(authorizeService).requestGithubUserInfo(accessToken);
        doReturn(false)
                .when(userService).checkUserExist(user.getAccount());
        doReturn(user).when(userService).createUser(user);

        ResponseAuthorizeDto responseAuthorizeDto = authorizeService.authenticateGithub(loginDto);

        Assert.assertEquals("account", responseAuthorizeDto.getUserAccount());
        Assert.assertEquals("authority", responseAuthorizeDto.getAuthority());
        Assert.assertEquals(215, responseAuthorizeDto.getToken().length());
    }

    @Test(expected = Exception.class)
    public void testAuthenticateGithubFailed() throws Exception {
        RequestGithubLoginDto loginDto = new RequestGithubLoginDto("test code");
        String accessToken = "";
        doReturn(accessToken)
                .when(authorizeService).requestGithubAccessToken(loginDto.getCode());

        ResponseAuthorizeDto responseAuthorizeDto = authorizeService.authenticateGithub(loginDto);
    }

    @Test
    public void testRequestGithubAccessTokenSuccess() {
        String uri = "https://github.com/login/oauth/access_token";
        String clientId = "56979f30af5067f72ac4";
        String clientSecret = "8f837fb970ab4baf71b849900d6c9c0301a07e13";
        String code = "47c251ea051ea0a0d0be";
        uri = uri + "?client_id=" + clientId + "&client_secret=" +
                clientSecret + "&code=" + code;

        Map<String, String> mp = new HashMap<>();
        mp.put("scope","");
        mp.put("token_type","bearer");

        when(restTemplate.exchange(eq(uri),
                eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok().body(mp));
        String actualAccessToken = authorizeService.requestGithubAccessToken(code);
    }

    @Test(expected = NullPointerException.class)
    public void testRequestGithubAccessTokenFailed() {

        when(restTemplate.exchange(eq("https://github.com/login/oauth/access_token?client_id=56979f30af5067f72ac4&" +
                        "client_secret=8f837fb970ab4baf71b849900d6c9c0301a07e13&code=f5fd281a044f1914a501"),
                eq(HttpMethod.POST), any(HttpEntity.class), eq(JSONObject.class)))
                .thenReturn(ResponseEntity.badRequest().body(null));
        authorizeService.requestGithubAccessToken("test code");
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(JSONObject.class));
    }

    @Test
    public void testRequestGithubUserInfoSuccess() {
        String stubUserInfo = "{\"login\": \"shiftwave00\"," +
                "\"avatar_url\": \"https://avatars.githubusercontent.com/u/43847242?v=4\"}";

        User expectedResult = new User();
        expectedResult.setAccount("github_shiftwave00");
        expectedResult.setName("shiftwave00");
        expectedResult.setAvatarUrl("https://avatars.githubusercontent.com/u/43847242?v=4");
        expectedResult.setAuthority("User");

        when(restTemplate.exchange(eq("https://api.github.com/user"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(stubUserInfo));

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test(expected = JSONException.class)
    public void testRequestGithubUserInfoFailed() {
        when(restTemplate.exchange(eq("https://api.github.com/user"),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().body("{}"));

        authorizeService.requestGithubUserInfo("testCode");
    }
}
