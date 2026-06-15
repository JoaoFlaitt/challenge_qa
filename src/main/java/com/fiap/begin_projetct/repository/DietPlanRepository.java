package com.fiap.begin_projetct.repository;

import com.fiap.begin_projetct.model.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    
    List<DietPlan> findByPatientIdOrderByStartDateDesc(Long patientId);
    
    @Query(nativeQuery = true, value = "SELECT * FROM diet_plans WHERE patient_id = :patientId AND start_date >= :startDate AND end_date <= :endDate ORDER BY start_date DESC")
    List<DietPlan> findByPatientIdAndPeriodBetween(
        @Param("patientId") Long patientId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
}
