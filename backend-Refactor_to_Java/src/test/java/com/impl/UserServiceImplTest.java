package com.impl;

import com.Application;
import com.bean.User;
import com.dao.UserRepository;
import com.dto.LocalAccountDto;
import com.exception.UserAlreadyExistException;
import com.exception.UserNotFoundException;
import com.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testCreateUserFromUserModelWithNotExistUser() {
        User expect = new User("test", null, null, null, "password", new ArrayList<>(), new ArrayList<>());
        when(userRepository.existsById(expect.getAccount())).thenReturn(false);
        when(userRepository.save(expect)).thenReturn(expect);

        User actual = userService.createUser(expect);

        assertEquals(expect, actual);
    }

    @Test(expected = UserAlreadyExistException.class)
    public void testCreateUserFromUserModelWithExistUser() {
        User expect = new User("test", null, null, null, "password", new ArrayList<>(), new ArrayList<>());
        when(userRepository.existsById(expect.getAccount())).thenReturn(true);

        userService.createUser(expect);
    }

    @Test
    public void testCreateAdminUserFromLocalAccountDto() {
        String expectAuthority = "Admin";
        LocalAccountDto localAccount = new LocalAccountDto("test", "password");
        User expect = new User(localAccount.getAccount(), null, null, expectAuthority, localAccount.getPassword(), new ArrayList<>(), new ArrayList<>());
        when(userRepository.existsById(localAccount.getAccount())).thenReturn(false);
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        User actual = userService.createAdminUser(localAccount);

        assertEquals(expect, actual);
        assertEquals(expectAuthority, actual.getAuthority());
    }

    @Test(expected = UserAlreadyExistException.class)
    public void testCreateAdminUserFromLocalAccountDtoWithExistAccount() {
        LocalAccountDto localAccount = new LocalAccountDto("test", "password");
        when(userRepository.existsById(anyString())).thenReturn(true);

        userService.createAdminUser(localAccount);
    }

    @Test
    public void testCheckUserExistWithExistUser() {
        String account = "test";
        when(userRepository.existsById(anyString())).thenReturn(true);

        boolean result = userService.checkUserExist(account);

        assertTrue(result);
    }

    @Test
    public void testCheckUserExistWithNotExistUser() {
        String account = "test";
        when(userRepository.existsById(anyString())).thenReturn(false);

        boolean result = userService.checkUserExist(account);

        assertFalse(result);
    }

    @Test()
    public void testDeleteUserWithExistUser() {
        String account = "test";
        when(userRepository.existsById(anyString())).thenReturn(true);

        userService.deleteUserByAccount(account);

        verify(userRepository).deleteById(account);
    }

    @Test(expected = UserNotFoundException.class)
    public void testDeleteUserWithNotExistUser() {
        String account = "test";
        when(userRepository.existsById(anyString())).thenReturn(false);

        userService.deleteUserByAccount(account);
    }

    @Test
    public void testGetUserByAccountWithExistUser() {
        User expect = new User("test", null, null, null, null, new ArrayList<>(), new ArrayList<>());
        when(userRepository.getById(expect.getAccount())).thenReturn(expect);

        User actual = userService.getUserByAccount(expect.getAccount());

        assertEquals(expect, actual);
    }

    @Test()
    public void testGetUserByAccountWithNotExistUser() {
        String account = "test";
        when(userRepository.getById(account)).thenThrow(EntityNotFoundException.class);

        User actual = userService.getUserByAccount(account);

        assertNull(actual);
    }

    @Test()
    public void testGetAllUsers() {
        List<User> expectUsers = new ArrayList<>();
        expectUsers.add(new User());
        when(userRepository.findAll()).thenReturn(expectUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectUsers, actualUsers);
    }

    @Test()
    public void testEditUserNameByAccountWithExistUserAndLegalName() {
        String expectName = "newName";
        User oldNameUser = new User("test", "oldName", null, null, null, new ArrayList<>(), new ArrayList<>());
        when(userRepository.existsById(oldNameUser.getAccount())).thenReturn(true);
        when(userRepository.getById(oldNameUser.getAccount())).thenReturn(oldNameUser);
        User newNameUser = new User(oldNameUser.getAccount(), expectName, oldNameUser.getAvatarUrl(), oldNameUser.getAuthority(), oldNameUser.getPassword(), oldNameUser.getOwnedProjects(), oldNameUser.getProjects());
        when(userRepository.save(any(User.class))).thenReturn(newNameUser);

        User actual = userService.editUserName(oldNameUser.getAccount(), expectName);

        assertEquals(expectName, actual.getName());
    }

    @Test(expected = UserNotFoundException.class)
    public void testEditUserNameByAccountWithNotExistUser() {
        String expectName = "newName";
        String account = "test";
        when(userRepository.existsById(account)).thenReturn(false);

        User actual = userService.editUserName(account, expectName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEditUserNameByAccountWithIllegalName() {
        String expectName = "abc!)*@ ";
        String account = "test";
        when(userRepository.existsById(account)).thenReturn(true);

        User actual = userService.editUserName(account, expectName);
    }
}
