package com.fiap.begin_projetct.repository;

import com.fiap.begin_projetct.model.PlannedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlannedMealRepository extends JpaRepository<PlannedMeal, Long> {
    
    List<PlannedMeal> findByDietPlanIdOrderByPlannedDateAsc(Long dietPlanId);
    
    List<PlannedMeal> findByDietPlanIdAndPlannedDateBetween(
        Long dietPlanId, 
        LocalDate startDate, 
        LocalDate endDate
    );
    
    List<PlannedMeal> findByDietPlanIdAndPlannedDate(
        Long dietPlanId, 
        LocalDate plannedDate
    );
    
    List<PlannedMeal> findByMealTypeOrderByPlannedDateAsc(String mealType);
    
    Long countByDietPlanId(Long dietPlanId);
}
