package com.fiap.begin_projetct.repository;

import com.fiap.begin_projetct.model.MealItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealItemRepository extends JpaRepository<MealItem, Long> {
    
    List<MealItem> findByMealId(Long mealId);
    
    List<MealItem> findByFoodId(Long foodId);
    
    Optional<MealItem> findByMealIdAndFoodId(Long mealId, Long foodId);
    
    @Query("SELECT mi FROM MealItem mi WHERE mi.meal.id = :mealId ORDER BY mi.id")
    List<MealItem> findByMealIdOrderById(@Param("mealId") Long mealId);
    
    @Query("SELECT mi FROM MealItem mi WHERE mi.food.id = :foodId ORDER BY mi.id")
    List<MealItem> findByFoodIdOrderById(@Param("foodId") Long foodId);
    
    @Query("SELECT COUNT(mi) FROM MealItem mi WHERE mi.meal.id = :mealId")
    Long countByMealId(@Param("mealId") Long mealId);
    
    @Query("SELECT COUNT(mi) FROM MealItem mi WHERE mi.food.id = :foodId")
    Long countByFoodId(@Param("foodId") Long foodId);
}
