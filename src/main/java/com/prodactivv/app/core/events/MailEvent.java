package com.prodactivv.app.core.events;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MailEvent {

    @Id
    @GeneratedValue
    private Long id;

    private String recipientEmail;

    private String subject;

    private String content;

    private String replyTo;

    private String sendAs;
}
