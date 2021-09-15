package com.nimuairy.auth.service.impl;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.nimuairy.auth.constant.EmailConstant.*;

@Service
public class EmailServiceImpl implements com.nimuairy.auth.service.EmailService {

    @Value("#{environment.cc_email}")
    private String ccEmail;

    @Value("#{environment.from_email}")
    private String fromEmail;

    @Value("#{environment.email_address_password_sender}")
    private String emailAddressPasswordSender;

    @Value("#{environment.email_address_password_sender_password}")
    private String emailAddressPasswordSenderPassword;

    @Override
    public void sendNewPasswordEmail(String firstname, String password, String email) throws MessagingException {
        Message message = createEmail(firstname, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, emailAddressPasswordSender, emailAddressPasswordSenderPassword);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createEmail(String firstname, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstname + ", \n \n Your new account password is: " + password + "\n \n The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }

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
