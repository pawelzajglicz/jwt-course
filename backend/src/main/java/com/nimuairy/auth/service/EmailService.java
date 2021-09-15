package com.nimuairy.auth.service;

import javax.mail.MessagingException;

public interface EmailService {

    void sendNewPasswordEmail(String firstname, String password, String email) throws MessagingException;
}
