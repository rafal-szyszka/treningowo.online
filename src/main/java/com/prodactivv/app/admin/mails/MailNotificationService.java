package com.prodactivv.app.admin.mails;

import com.prodactivv.app.admin.usermanagement.model.UserInvite;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailNotificationService {

    private final NotificationMailAccountConfig mailAccountConfig;

    public void sendNotification(String recipient, String subject, String content) {
        JavaMailSender javaMailSender = mailAccountConfig.getJavaMailSender();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setReplyTo(mailAccountConfig.getNoReplyAlias());
        message.setFrom(mailAccountConfig.getSenderAlias());
        message.setSubject(subject);
        message.setTo(recipient);
        message.setText(content);

        javaMailSender.send(message);
    }

    public void sendInvitationEmail(UserInvite userInvite, String subject, String content) {
        sendNotification(
                userInvite.getUser().getEmail(),
                subject,
                content
        );
    }

}
