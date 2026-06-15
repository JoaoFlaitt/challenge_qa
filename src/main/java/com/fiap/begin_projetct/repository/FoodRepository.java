package com.fiap.begin_projetct.repository;

import com.fiap.begin_projetct.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    
    Optional<Food> findByName(String name);
    
    boolean existsByName(String name);
    
    List<Food> findByNameContainingIgnoreCase(String name);

    Optional<Food> findByFdcId(Long fdcId);

    boolean existsByFdcId(Long fdcId);
}
