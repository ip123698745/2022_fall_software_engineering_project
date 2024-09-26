package com.impl;

import com.bean.User;
import com.dao.UserRepository;
import com.dto.LocalAccountDto;
import com.exception.UserAlreadyExistException;
import com.exception.UserNotFoundException;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("UserService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final Pattern nameFormatPattern = Pattern.compile("^\\w+$");

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create user from User model.
     *
     * @param user a User model to be used to create a user. Must not be null.
     * @return the saved User entity.
     * @throws UserAlreadyExistException if the user already exists.
     */
    @Override
    public User createUser(User user) {
        if (userRepository.existsById(user.getAccount()))
            throw new UserAlreadyExistException(user.getAccount());
        user.setProjects(new ArrayList<>());

        return userRepository.save(user);
    }

    /**
     * Check if the user account exists.
     *
     * @param account the user account to check.
     * @return true if the account exists, false otherwise.
     */
    @Override
    public boolean checkUserExist(String account) {
        return userRepository.existsById(account);
    }

    /**
     * Create an admin user.
     *
     * @param localAccount a DTO contains user's account and password. Must not be null.
     * @return the created User entity.
     * @throws UserAlreadyExistException if the user already exists.
     */
    @Override
    public User createAdminUser(LocalAccountDto localAccount) {
        if (userRepository.existsById(localAccount.getAccount()))
            throw new UserAlreadyExistException(localAccount.getAccount());
        User user = new User();
        user.setAccount(localAccount.getAccount());
        user.setPassword(localAccount.getPassword());
        user.setAuthority("Admin");
        return userRepository.save(user);
    }

    /**
     * Delete user by account.
     *
     * @param account the account of the user to delete.
     * @throws UserNotFoundException if the user is not found.
     */
    @Override
    public void deleteUserByAccount(String account) {
        if (!userRepository.existsById((account)))
            throw new UserNotFoundException(account);
        userRepository.deleteById(account);
    }

    /**
     * Get User entity by giving an account.
     *
     * @param account the account of the user to get.
     * @return the User entity if user exists, null otherwise.
     */
    @Override
    public User getUserByAccount(String account) {
        User user = null;
        try {
            user = userRepository.getById(account);
        } catch (EntityNotFoundException ignored) {
            // Do nothing
        }
        return user;
    }

    /**
     * Get all User entities.
     *
     * @return a list contains all users.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Rename the user's name by giving an account.
     *
     * @param account the account of the user to rename.
     * @param newName the new name.
     * @return the saved User entity.
     * @throws UserNotFoundException    if the user is not found.
     * @throws IllegalArgumentException if the new name does not match the regex pattern "^\w+$".
     */
    @Override
    public User editUserName(String account, String newName) {
        if (!userRepository.existsById(account))
            throw new UserNotFoundException(account);
        verifyNameFormatOrThrow(newName);
        User editedUser = userRepository.getById(account);
        editedUser.setName(newName);
        return userRepository.save(editedUser);
    }

    /**
     * Verify the new name is legal.
     *
     * @param newName the new name to be verified.
     * @throws IllegalArgumentException if the new name does not match the regex pattern "^\w+$".
     */
    private void verifyNameFormatOrThrow(String newName) {
        Matcher newNameMatcher = nameFormatPattern.matcher(newName);
        if (!newNameMatcher.matches())
            throw new IllegalArgumentException(String.format("The new name \"%s\" does not match the regex \"%s\"",
                    newName,
                    newNameMatcher.pattern().pattern()));
    }
}
