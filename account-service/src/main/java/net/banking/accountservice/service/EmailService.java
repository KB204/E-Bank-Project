package net.banking.accountservice.service;

import lombok.extern.slf4j.Slf4j;
import net.banking.accountservice.dto.EmailDetails;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = "email_queue")
    public void sendEmail(EmailDetails emailDetails){
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailSender);
            mailMessage.setTo(emailDetails.to());
            mailMessage.setText(emailDetails.body());
            mailMessage.setSubject(emailDetails.subject());
            mailSender.send(mailMessage);
            log.info("Email was sent successfully");
        } catch (MailException e){
            log.debug("Failed to send email");
        }
    }
}
