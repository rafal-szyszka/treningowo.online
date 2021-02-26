package com.prodactivv.app.admin.mails;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Getter
@Component
@PropertySource(value = "classpath:notification-mailAccount.properties")
public class NotificationMailAccountConfig {

    @Value("${mail.address}")
    private String address;

    @Value("${mail.credentials}")
    private String credentials;

    @Value("${mail.imap.out.server}")
    private String smtpServer;

    @Value("${mail.imap.out.port}")
    private Integer smtpPort;

    @Value("${mail.imap.in.server}")
    private String imapServer;

    @Value("${mail.imap.in.port}")
    private Integer imapPort;

    @Value("${mail.sender.alias}")
    private String senderAlias;

    @Value("${mail.no-reply.alias}")
    private String noReplyAlias;

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpServer);
        mailSender.setPort(smtpPort);

        mailSender.setUsername(address);
        mailSender.setPassword(credentials);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "false");

        return mailSender;
    }

    public String getSenderAlias() {
        return String.format("%s <%s>", senderAlias, address);
    }

}
