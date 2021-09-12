package com.nimuairy.auth.service;

import java.util.concurrent.ExecutionException;

public interface LoginAttemptService {
    void addUserToLoginAttemptCache(String username) throws ExecutionException;

    void evictUserFromLoginAttemptCache(String username);

    boolean hasExceededMaxAttempts(String username) throws ExecutionException;
}
