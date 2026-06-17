package com.example.tasktracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendTaskAssignmentEmail(String toEmail, String assigneeUsername,
                                        String taskTitle, String projectName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Task i ri u asinjua — " + projectName);
        message.setText(
                "Përshëndetje " + assigneeUsername + "!\n\n" +
                        "Ju është asinjuar një task i ri:\n" +
                        "Task: " + taskTitle + "\n" +
                        "Projekt: " + projectName + "\n\n" +
                        "Hyni në Task Tracker për të parë detajet.\n\n" +
                        "Task Tracker Team"
        );
        mailSender.send(message);
    }
}