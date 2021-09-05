package com.nimuairy.auth.service;


import com.nimuairy.auth.domain.User;
import com.nimuairy.auth.exception.domain.EmailExistException;
import com.nimuairy.auth.exception.domain.UserNotFoundException;
import com.nimuairy.auth.exception.domain.UsernameExistException;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUSerByEmail(String email);
}
