package com.yacovets.projectmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailService {
    @Value("${mail.default-from}")
    private String defaultMailFrom;

    @Value("${main.url}")
    private String mainUrl;

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(defaultMailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendMailSuccessRegistrationAndEmailVerification(String email, UUID token) {
        String link = String.format("%s/security/email-verification/%s", mainUrl, token);
        String message = String.format("You have successfully registered your account. To confirm this email, please follow the link - %s", link);
        sendMessage(email, "Account registration", message);
    }

    public void sendMailAddNewTask(String email, long boardId, String creatorUsername) {
        String link = String.format("%s/board/%d", mainUrl, boardId);
        String message = String.format("A new task has been added for you. The creator of the %s task. Link to the board - %s", creatorUsername, link);
        sendMessage(email, "Add new task", message);
    }

    public void sendMailAddToTeam(String email, String alias, String username, String projectName) {
        String link = String.format("%s/project/%s", mainUrl, alias);
        String message = String.format("You have been added to the %s project. You were invited by %s. Link to the project - %s", projectName, username, link);
        sendMessage(email, "Add to team project", message);
    }

    public void sendMailPasswordRecovery(String email, UUID token) {
        String link = String.format("%s/security/password-recovery/%s", mainUrl, token);
        String message = String.format("You have requested password recovery. To set a new password, follow the link - %s", link);
        sendMessage(email, "Password recovery", message);
    }
}
