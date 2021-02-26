package com.prodactivv.app.admin.usermanagement.model;

import com.prodactivv.app.user.model.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserInvite {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private LocalDate validUntil;

    private String role;

    private String hash;

}
