package com.nimuairy.auth.service.impl;

import org.springframework.stereotype.Service;

import java.util.Properties;
import javax.mail.Session;

import static com.nimuairy.auth.constant.EmailConstant.*;

@Service
public class EmailServiceImpl implements com.nimuairy.auth.service.EmailService {

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);

        return Session.getInstance(properties, null);
    }
}
