package com.nimuairy.auth.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nimuairy.auth.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    @Value("${nimuairy.maximum-number-of-attempts}")
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 3;
    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptServiceImpl() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    @Override
    public void addUserToLoginAttemptCache(String username) throws ExecutionException {
        int attempts = 0;
        attempts = loginAttemptCache.get(username) + 1;
        loginAttemptCache.put(username, attempts);
    }

    @Override
    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    @Override
    public boolean hasExceededMaxAttempts(String username) throws ExecutionException {
        return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
    }
}
