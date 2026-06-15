package com.fiap.begin_projetct.repository;

import com.fiap.begin_projetct.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    
    List<Meal> findByPatientIdOrderByMealDateDesc(Long patientId);
    
    List<Meal> findByPatientIdAndMealDateBetweenOrderByMealDate(
        Long patientId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    Optional<Meal> findByIdAndPatientId(Long id, Long patientId);
    
    @Query("SELECT m FROM Meal m WHERE m.patient.id = :patientId AND m.isPreset = true ORDER BY m.createdAt DESC")
    List<Meal> findPresetsByPatientId(@Param("patientId") Long patientId);
    
    boolean existsByPresetNameAndPatientId(String presetName, Long patientId);
}
