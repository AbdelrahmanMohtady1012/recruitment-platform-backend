package com.recruitment.tracking.repository;

import com.recruitment.tracking.entity.StageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageHistoryRepository extends JpaRepository<StageHistory, Long> {

    List<StageHistory> findByApplicationId(Long applicationId);
}