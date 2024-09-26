package com.service;

import com.bean.User;
import com.dto.LocalAccountDto;

import java.util.List;

public interface UserService {
    User createUser(User user);

    boolean checkUserExist(String account);

    User createAdminUser(LocalAccountDto localAccount);

    void deleteUserByAccount(String account);

    User getUserByAccount(String account);

    List<User> getAllUsers();

    User editUserName(String account, String newName);
}
