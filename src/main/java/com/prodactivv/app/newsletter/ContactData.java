package com.prodactivv.app.newsletter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactData {

    private String senderName;
    private String senderEmail;
    private String subject;
    private String content;
}
