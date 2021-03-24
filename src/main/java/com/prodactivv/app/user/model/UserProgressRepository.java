package com.prodactivv.app.user.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    @Query("SELECT up FROM UserProgress up where up.user.id = ?1")
    List<UserProgress> getAllUserProgress(long userId);
}
