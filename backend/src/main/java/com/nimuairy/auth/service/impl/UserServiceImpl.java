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
            log.error("User not found by username: " + username);
            throw new UsernameNotFoundException("User not found by username: " + username);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info("Returning found user by username: " + username);

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
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public User findUSerByEmail(String email) {
        return null;
    }

    private User validateNewUsernameAndEmail(String currentUserName, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        if (StringUtils.isNotBlank(currentUserName)) {
            User currentUser = findUserByUsername(currentUserName);
            if (currentUser == null) {
                throw new UserNotFoundException("No user found by username " + currentUserName);
            }
            User userByNewUsername = findUserByUsername(newUsername);
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException("Username already exists");
            }
            User userByNewEmail = findUSerByEmail(newEmail);
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException("Username already exists");
            }
            return currentUser;

        } else {
            User userByUsername = findUserByUsername(newUsername);
            if (userByUsername != null) {
                throw new UsernameExistException("Username already exists");
            }
            User userByEmail = findUSerByEmail(newEmail);
            if (userByUsername != null) {
                throw new EmailExistException("Username already exists");
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
