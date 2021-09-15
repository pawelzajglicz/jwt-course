package com.nimuairy.auth.service;

public interface EmailService {

    void sendNewPasswordEmail(String firstname, String password, String email);
}
