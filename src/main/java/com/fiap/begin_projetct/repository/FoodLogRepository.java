package com.fiap.begin_projetct.repository;

import com.fiap.begin_projetct.model.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {
    
    List<FoodLog> findByPatientIdOrderByConsumedAtDesc(Long patientId);
    
    List<FoodLog> findByPatientIdAndConsumedAtBetween(
        Long patientId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    List<FoodLog> findPlannedConsumptionByPatientId(@Param("patientId") Long patientId);
}
