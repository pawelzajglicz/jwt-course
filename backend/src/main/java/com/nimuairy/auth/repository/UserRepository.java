package com.nimuairy.auth.repository;

import com.nimuairy.auth.domain.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    List<User> deleteByUserId(String userId);

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
