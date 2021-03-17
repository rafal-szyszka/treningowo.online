package com.prodactivv.app.admin.usermanagement;

import com.prodactivv.app.admin.mails.MailNotificationService;
import com.prodactivv.app.admin.usermanagement.model.UserInvite;
import com.prodactivv.app.admin.usermanagement.model.UserInviteRepository;
import com.prodactivv.app.core.exceptions.InvitationExpiredException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.utils.HashGenerator;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserRepository;
import com.prodactivv.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationService {

    @Value("${app.invitations.activate.account.url}")
    private String acceptInviteUrl;

    private final HashGenerator hashGenerator;
    private final MailNotificationService mailNotificationService;

    private final UserService userService;

    private final UserRepository userRepository;
    private final UserInviteRepository userInviteRepository;

    public UserInvite inviteDietitian(User.Dto.Invitation invitation) {
        return invite(invitation, "DIETITIAN");
    }

    public UserInvite inviteUser(User.Dto.Invitation invitation) {
        return invite(invitation, "USER");
    }

    public UserInvite getInviteByHash(String hash) throws NotFoundException, InvitationExpiredException {
        UserInvite userInvite = userInviteRepository.findByHash(hash).orElseThrow(new NotFoundException(String.format("Invitation %s not found.", hash)));

        if (LocalDate.now().isBefore(userInvite.getValidUntil())) {
            return userInvite;
        }

        throw new InvitationExpiredException(String.format("%s expired", hash));
    }

    public User acceptInvite(String hash, User.Dto.UserInvitationData userData) throws NotFoundException, InvitationExpiredException, NoSuchAlgorithmException {
        UserInvite userInvite = getInviteByHash(hash);
        User user = userData.toUser();
        user.setRole(userInvite.getRole());
        user.setSignedUpDate(LocalDate.now());
        user.setAge(userService.calculateUserAge(user));
        user = userRepository.save(user);

        userInviteRepository.delete(userInvite);

        return user;
    }

    public List<UserInvite> getDietitiansInvites() {
        return getInvitesByUserRole("DIETITIAN");
    }

    public List<UserInvite> getInvitesByUserRole(String role) {
        return userInviteRepository.findAllByUserRole(role);
    }

    private UserInvite invite(User.Dto.Invitation invitation, String type) throws IllegalArgumentException {
        User user = new User();
        user.setEmail(invitation.getEmail());
        user.setPassword(hashGenerator.generateRandom(16));

        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already taken!");
        }

        user = userRepository.save(user);

        UserInvite invite = UserInvite.builder()
                .user(user)
                .validUntil(LocalDate.now().plusDays(7L))
                .hash(hashGenerator.generateSha256Hash(Arrays.asList(user.getEmail(), Long.toString(user.getId()))))
                .role(type)
                .build();

        invite = userInviteRepository.save(invite);

        String content = invitation.getMessage();
        content += "\n\nLink aktywacyjny: " + acceptInviteUrl + invite.getHash();

        mailNotificationService.sendInvitationEmail(invite, invitation.getSubject(), content);

        return invite;
    }
}
