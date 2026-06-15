package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.model.Food;
import com.fiap.begin_projetct.model.Meal;
import com.fiap.begin_projetct.model.MealItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NutritionService {
    
    /**
     * Calcula calorias totais de uma refeição
     */
    public Integer calculateTotalCalories(Meal meal) {
        if (meal.getItems() == null) {
            return 0;
        }
        
        return meal.getItems().stream()
                .mapToInt(MealItem::getCalories)
                .sum();
    }
    
    /**
     * Calcula proteínas totais de uma refeição
     */
    public Double calculateTotalProteins(Meal meal) {
        if (meal.getItems() == null) {
            return 0.0;
        }
        
        return meal.getItems().stream()
                .mapToDouble(MealItem::getProteins)
                .sum();
    }
    
    /**
     * Calcula carboidratos totais de uma refeição
     */
    public Double calculateTotalCarbs(Meal meal) {
        if (meal.getItems() == null) {
            return 0.0;
        }
        
        return meal.getItems().stream()
                .mapToDouble(MealItem::getCarbs)
                .sum();
    }
    
    /**
     * Calcula gorduras totais de uma refeição
     */
    public Double calculateTotalFats(Meal meal) {
        if (meal.getItems() == null) {
            return 0.0;
        }
        
        return meal.getItems().stream()
                .mapToDouble(MealItem::getFats)
                .sum();
    }
    
    /**
     * Calcula fibras totais de uma refeição
     */
    public Double calculateTotalFiber(Meal meal) {
        if (meal.getItems() == null) {
            return 0.0;
        }
        
        return meal.getItems().stream()
                .mapToDouble(MealItem::getFiber)
                .sum();
    }
    
    /**
     * Calcula sódio total de uma refeição
     */
    public Double calculateTotalSodium(Meal meal) {
        if (meal.getItems() == null) {
            return 0.0;
        }
        
        return meal.getItems().stream()
                .mapToDouble(MealItem::getSodium)
                .sum();
    }
    
    /**
     * Calcula açúcar total de uma refeição
     */
    public Double calculateTotalSugar(Meal meal) {
        if (meal.getItems() == null) {
            return 0.0;
        }
        
        return meal.getItems().stream()
                .mapToDouble(MealItem::getSugar)
                .sum();
    }
    
    /**
     * Valida se uma refeição tem alimentos
     */
    public void validateMealHasItems(Meal meal) {
        if (meal.getItems() == null || meal.getItems().isEmpty()) {
            throw new IllegalArgumentException("Refeição deve conter pelo menos um alimento");
        }
    }
    
    /**
     * Valida se a quantidade de um alimento é válida
     */
    public void validateQuantity(Double quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
    }
    
    /**
     * Valida se os dados nutricionais de um alimento são válidos
     */
    public void validateFoodNutrients(Food food) {
        if (food.getCaloriesPer100g() == null || food.getCaloriesPer100g() < 0) {
            throw new IllegalArgumentException("Calorias por 100g devem ser maiores que zero");
        }
        
        if (food.getProteins() == null || food.getProteins() < 0) {
            throw new IllegalArgumentException("Proteínas devem ser maiores ou iguais a zero");
        }
        
        if (food.getCarbs() == null || food.getCarbs() < 0) {
            throw new IllegalArgumentException("Carboidratos devem ser maiores ou iguais a zero");
        }
        
        if (food.getFats() == null || food.getFats() < 0) {
            throw new IllegalArgumentException("Gorduras devem ser maiores ou iguais a zero");
        }
    }
}
