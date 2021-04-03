package com.prodactivv.app.admin.mails;

import com.prodactivv.app.admin.mails.model.Notification;
import com.prodactivv.app.admin.mails.model.NotificationRepository;
import com.prodactivv.app.admin.usermanagement.model.UserInvite;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.newsletter.ContactData;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class MailNotificationService {

    private final NotificationMailAccountConfig mailAccountConfig;
    private final NotificationRepository notificationRepository;

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

    public void sendNotificationHTML(String recipient, String subject, String content) throws MessagingException {
        JavaMailSender javaMailSender = mailAccountConfig.getJavaMailSender();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setText(content, true);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setFrom(mailAccountConfig.getSenderAlias());
        helper.setReplyTo(mailAccountConfig.getNoReplyAlias());

        javaMailSender.send(mimeMessage);
    }

    public void sendInvitationEmail(UserInvite userInvite, String subject, String content) {
        sendNotification(
                userInvite.getUser().getEmail(),
                subject,
                content
        );
    }

    public void sendPurchaseEmail(String email, HashMap<String, String> variables) throws NotFoundException, MessagingException {
        sendSystemNotificationHTMLMail(email, variables, "mail.purchase");
    }

    public void sendPurchaseConfirmationEmail(String email, HashMap<String, String> variables) throws NotFoundException, MessagingException {
        sendSystemNotificationHTMLMail(email, variables, "mail.purchase.confirm");
    }

    public void sendRegistrationEmail(String email, HashMap<String, String> variables) throws MessagingException, NotFoundException {
        sendSystemNotificationHTMLMail(email, variables, "mail.registration");
    }

    public void sendSubscribedToNewsletter(String email, HashMap<String, String> variables) {
//        sendSystemNotificationHTMLMail(email, variables, "mail.newsletter.subscribed");
    }

    public void sendUnsubscribedToNewsletter(String email, HashMap<String, String> variables) {
//        sendSystemNotificationHTMLMail(email, variables, "mail.newsletter.unsubscribed");
    }

    public void sendPlanReadyNotification(String email, HashMap<String, String> variables) throws NotFoundException, MessagingException {
        sendSystemNotificationHTMLMail(email, variables, "mail.user.workoutPlan.ready");
    }

    public void sendWelcomeMessage(String email, HashMap<String, String> variables) throws NotFoundException, MessagingException {
        sendSystemNotificationHTMLMail(email, variables, "mail.user.welcome");
    }

    private void sendSystemNotificationHTMLMail(String email, HashMap<String, String> variables, String notificationUid) throws NotFoundException, MessagingException {
        Notification notification = notificationRepository.findNotificationByTextUid(notificationUid)
                .orElseThrow(new NotFoundException(String.format("Notification %s not found!", notificationUid)));
        String content = notification.getContent();

        for (String key : variables.keySet()) {
            content = content.replace(key, variables.get(key));
        }

        sendNotificationHTML(
                email,
                notification.getSubject(),
                content
        );
    }

    public void sendContactNotification(ContactData contactData) {
        String subject = contactData.getSenderName() + " : " + contactData.getSubject();
        String content = contactData.getContent() + "\n\nOdpowiedz na: " + contactData.getSenderEmail();
        sendNotification("kontakt@treningowo.online", subject, content);
    }
}
