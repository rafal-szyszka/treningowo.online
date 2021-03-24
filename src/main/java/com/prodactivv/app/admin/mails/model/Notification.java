package com.prodactivv.app.admin.mails.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String textUid;

    private String subject;

    @Column(columnDefinition = "TEXT", length = 50000)
    private String content;
}
