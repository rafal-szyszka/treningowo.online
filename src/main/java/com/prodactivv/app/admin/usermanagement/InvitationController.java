package com.prodactivv.app.admin.usermanagement;

import com.prodactivv.app.admin.mails.MailNotificationService;
import com.prodactivv.app.admin.usermanagement.model.UserInvite;
import com.prodactivv.app.core.exceptions.InvitationExpiredException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class InvitationController {

    private final MailNotificationService mailNotificationService;

    private final InvitationService service;

    @GetMapping(value = "/admin/invite/dietitian")
    public ResponseEntity<List<UserInvite>> getDietitiansInvites() {
        return ResponseEntity.ok(service.getDietitiansInvites());
    }

    @PostMapping(value = "/admin/invite")
    public ResponseEntity<UserInvite> inviteUser(@RequestBody User.Dto.Invitation invitation) {
        try {
            return ResponseEntity.ok(service.inviteUser(invitation));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/admin/invite/dietitian")
    public ResponseEntity<UserInvite> inviteDietitian(@RequestBody User.Dto.Invitation invitation) {
        try {
            return ResponseEntity.ok(service.inviteDietitian(invitation));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/public/invite/{hash}")
    public ResponseEntity<UserInvite> getInviteByHash(@PathVariable String hash) {
        try {
            return ResponseEntity.ok(service.getInviteByHash(hash));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.OK, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/public/invite/accept/{hash}")
    public ResponseEntity<User> acceptInvite(@PathVariable String hash, @RequestBody User.Dto.UserInvitationData user) {
        try {
            return ResponseEntity.ok(service.acceptInvite(hash, user));
        } catch (NotFoundException | InvitationExpiredException | NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
