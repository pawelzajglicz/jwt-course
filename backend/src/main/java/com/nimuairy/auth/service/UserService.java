package com.nimuairy.auth.service;

import com.nimuairy.auth.domain.User;
import com.nimuairy.auth.exception.domain.EmailExistException;
import com.nimuairy.auth.exception.domain.EmailNotFoundException;
import com.nimuairy.auth.exception.domain.UserNotFoundException;
import com.nimuairy.auth.exception.domain.UsernameExistException;

import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException;

    void deleteUser(Long id);

    User findUserByEmail(String email);

    User findUserByUsername(String username);

    List<User> getUsers();

    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException;

}
