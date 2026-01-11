package com.ems.estatemanagementsystem.service.emailservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import com.ems.estatemanagementsystem.pattern.Observer;
import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.entity.ExternalAgency;

@Service
public class EmailService implements Observer {

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

    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("muhammadrizdwan@graduate.utm.my");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        emailSender.send(message);
    }

    @Override
    public void update(Object data) {
        String targetEmail = null;
        String subject = "Blockchain Update Notification";
        String body = "";

        if (data instanceof PIC) {
            PIC pic = (PIC) data;
            targetEmail = pic.getPicEmail();
            body = "Hello " + pic.getPicName() + ",\n\nYour profile has been updated on the blockchain.\nTxHash: "
                    + pic.getTxHash();
        } else if (data instanceof ExternalAgency) {
            ExternalAgency agency = (ExternalAgency) data;
            targetEmail = agency.getEmail();
            body = "Hello " + agency.getAgencyName()
                    + ",\n\nYour agency details have been updated on the blockchain.\nTxHash: " + agency.getTxHash();
        }

        if (targetEmail != null) {
            try {
                sendSimpleMessage(targetEmail, subject, body);
                System.out.println("EmailService: Sent notification to " + targetEmail);
            } catch (MessagingException e) {
                System.err.println("EmailService: Failed to send email: " + e.getMessage());
            }
        }
    }
}
