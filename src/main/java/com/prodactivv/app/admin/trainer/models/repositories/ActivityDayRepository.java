package com.prodactivv.app.admin.trainer.models.repositories;

import com.prodactivv.app.admin.trainer.models.ActivityDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityDayRepository extends JpaRepository<ActivityDay, Long> {
}
