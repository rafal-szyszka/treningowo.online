package com.prodactivv.app.admin.usermanagement.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInviteRepository extends JpaRepository<UserInvite, Long> {

    Optional<UserInvite> findByHash(String hash);

    @Query("SELECT uv FROM UserInvite uv WHERE uv.role = ?1")
    List<UserInvite> findAllByUserRole(String role);
}

