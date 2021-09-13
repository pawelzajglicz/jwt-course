package com.nimuairy.auth.service;

public interface LoginAttemptService {
    void addUserToLoginAttemptCache(String username);

    void evictUserFromLoginAttemptCache(String username);

    boolean hasExceededMaxAttempts(String username);
}
