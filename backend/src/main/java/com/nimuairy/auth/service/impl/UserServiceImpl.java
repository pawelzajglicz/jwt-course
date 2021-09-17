package com.nimuairy.auth.service.impl;

import com.nimuairy.auth.domain.Authority;
import com.nimuairy.auth.domain.User;
import com.nimuairy.auth.domain.UserPrincipal;

import com.nimuairy.auth.enumeration.Role;
import com.nimuairy.auth.exception.domain.EmailExistException;
import com.nimuairy.auth.exception.domain.EmailNotFoundException;
import com.nimuairy.auth.exception.domain.UserNotFoundException;
import com.nimuairy.auth.exception.domain.UsernameExistException;
import com.nimuairy.auth.repository.AuthorityRepository;
import com.nimuairy.auth.repository.UserRepository;
import com.nimuairy.auth.service.EmailService;
import com.nimuairy.auth.service.LoginAttemptService;
import com.nimuairy.auth.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.nimuairy.auth.constant.FileConstant.*;
import static com.nimuairy.auth.constant.UserImplementationConstant.*;
import static com.nimuairy.auth.enumeration.Role.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Qualifier("UserDetailsService")
@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private AuthorityRepository authorityRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private EmailService emailService;
    private LoginAttemptService loginAttemptService;
    private UserRepository userRepository;

    public UserServiceImpl(AuthorityRepository authorityRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService, LoginAttemptService loginAttemptService, UserRepository userRepository) {
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.loginAttemptService = loginAttemptService;
        this.userRepository = userRepository;
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        saveProfileImage(user, profileImage);

        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(RETURNING_FOUND_USER_BY_USERNAME + username);

            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        user.setActive(true);
        user.setAuthorities(new HashSet<>(ROLE_USER.getAuthorities())
                .stream()
                .map(Authority::getName)
                .map(authorityName -> authorityRepository.findByName(authorityName).orElseThrow(() -> new RuntimeException("Error: Authority is not found.")))
                .collect(Collectors.toSet()));
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setJoinDate(new Date());
        user.setLastName(lastName);
        user.setNotLocked(true);
        user.setPassword(encodePassword(password));
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        user.setRole(ROLE_USER.name());
        user.setUsername(username);
        userRepository.save(user);
        emailService.sendNewPasswordEmail(firstName, password, email);

        return user;
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);

        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);

        return currentUser;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }

    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private User validateNewUsernameAndEmail(String currentUserName, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {

        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
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
}
