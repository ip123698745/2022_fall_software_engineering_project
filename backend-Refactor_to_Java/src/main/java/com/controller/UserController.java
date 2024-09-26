package com.controller;

import com.JwtHelper;
import com.bean.User;
import com.dao.UserRepository;
import com.dto.ResponseDto;
import com.dto.RequestUserInfoDto;
import com.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping("/user")
    public ResponseEntity<RequestUserInfoDto> getUserInfo(@RequestHeader("Authorization") String authToken) {
        JSONObject jsonObject = jwtHelper.validateToken(authToken);
        User user = userService.getUserByAccount(jsonObject.getString("sub"));
        RequestUserInfoDto userInfoDto = new RequestUserInfoDto(user.getAccount(), user.getName(), user.getAvatarUrl());
        try {
            return ResponseEntity.ok().body(userInfoDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @PostMapping("/user/edit")
    public ResponseEntity<ResponseDto> editUserName(@RequestHeader("Authorization") String authToken, @RequestBody RequestUserInfoDto requestUserInfoDto) {
        try {
            JSONObject jsonObject = jwtHelper.validateToken(authToken);
            userService.editUserName(jsonObject.getString("sub"), requestUserInfoDto.getName());

            return ResponseEntity.ok().body(
                    new ResponseDto(true, "Edited Success")
            );
        } catch (Exception ex) {
            return ResponseEntity.ok().body(
                    new ResponseDto(false, ex.getMessage())
            );
        }
    }

//    @GetMapping("/init")
//    public String init(){
//        User user = null;
//        for(int i=0;i<10;i++){
//            user = new User();
//            user.setName("test"+i);
//            userService.save(user);
//        }
//        return "初始化完成。";
//    }
//
//    @GetMapping("/userByName/{username}")
//    public User getUserByName(@PathVariable("username") String username){
//        return userService.getByName(username);
//    }
//
//    @GetMapping("/userById/{userid}")
//    public User getUserById(@PathVariable("userid") Long userid){
//        return userService.getUserByID(userid);
//    }
//
//    @GetMapping("/page")
//    public Page<User> getPage(){
//        return userService.findPage();
//    }
//
//    @RequestMapping("/update/{id}/{name}")
//    public User update(@PathVariable Long id, @PathVariable String name){
//        return userService.update(id,name);
//    }

//    @RequestMapping("/{userId}")
//    public Boolean update(@PathVariable int userId){
//        return userService.IsProjectOwner(userId);
//    }

//    @GetMapping("/test/{account}") // 待修改
//    public String test(@PathVariable String account){
//        try {
//            User user = new User("aa", "b", "c", "d", "e", null, null);
//            userService.createUser(user);
//            return "添加成功";
//        } catch (Exception ex) {
//            return "添加失敗";
//        }
//    }

//    @RequestMapping("/test/{account}") //測過了，等table更新過後就可以用
//    public Boolean test(@PathVariable String account){
//        return userService.CheckUserExist(account);
//    }
}