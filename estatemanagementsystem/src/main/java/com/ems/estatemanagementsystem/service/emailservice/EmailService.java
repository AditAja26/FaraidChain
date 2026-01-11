package com.ems.estatemanagementsystem.service.emailservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendMessage(String to, String subject, String text, MultipartFile[] attachments)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("muhammadrizdwan@graduate.utm.my");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        for (MultipartFile attachment : attachments) {
            helper.addAttachment(attachment.getOriginalFilename(), attachment);
        }

        emailSender.send(message);

        System.out.println("Email sent successfully with attachments!");
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            sendMessage(to, subject, text, new MultipartFile[0]);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            System.err.println("Failed to send email to " + to);
        }
    }
}
