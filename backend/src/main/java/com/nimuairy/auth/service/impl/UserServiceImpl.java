package com.nimuairy.auth.service.impl;

import com.nimuairy.auth.domain.User;
import com.nimuairy.auth.domain.UserPrincipal;

import com.nimuairy.auth.exception.domain.EmailExistException;
import com.nimuairy.auth.exception.domain.UserNotFoundException;
import com.nimuairy.auth.exception.domain.UsernameExistException;
import com.nimuairy.auth.repository.UserRepository;
import com.nimuairy.auth.service.UserService;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.nimuairy.auth.enumeration.Role.*;

@Qualifier("UserDetailsService")
@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String NO_USER_FOUND_BY_USERNAME = "No user found by username ";
    public static final String RETURNING_FOUND_USER_BY_USERNAME = "Returning found user by username: ";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";

    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public UserServiceImpl(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(RETURNING_FOUND_USER_BY_USERNAME + username);

            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setActive(true);
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setJoinDate(new Date());
        user.setLastName(lastName);
        user.setNotLocked(true);
        user.setPassword(encodedPassword);
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        user.setRole(ROLE_USER.name());
        user.setUsername(username);
        userRepository.save(user);
        log.info("New user password: " + password); // temporary for testing

        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUSerByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    private User validateNewUsernameAndEmail(String currentUserName, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {

        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUSerByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUserName)) {
            User currentUser = findUserByUsername(currentUserName);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUserName);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;

        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
    }
}
