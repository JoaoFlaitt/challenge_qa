package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.model.Food;
import com.fiap.begin_projetct.repository.FoodRepository;
import com.careplus.external.fooddata.FoodDataClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodService {
    
    private final FoodRepository foodRepository;
    private final NutritionService nutritionService;
    private final FoodDataClient foodDataClient;
    
    public List<Food> listarTodos() {
        return foodRepository.findAll();
    }
    
    public Optional<Food> buscarPorId(Long id) {
        return foodRepository.findById(id);
    }
    
    public Optional<Food> buscarPorNome(String name) {
        return foodRepository.findByName(name);
    }
    
    public Food salvar(Food food) {
        nutritionService.validateFoodNutrients(food);
        
        if (food.getFdcId() != null) {
            Optional<Food> existing = foodRepository.findByFdcId(food.getFdcId());
            if (existing.isPresent()) {
                return existing.get();
            }
        } else if (foodRepository.existsByName(food.getName())) {
            throw new IllegalArgumentException("Alimento com nome '" + food.getName() + "' já existe");
        }
        
        return foodRepository.save(food);
    }
    
    public Food atualizar(Long id, Food food) {
        Optional<Food> foodExistente = foodRepository.findById(id);
        if (foodExistente.isEmpty()) {
            throw new IllegalArgumentException("Alimento não encontrado com ID: " + id);
        }
        
        nutritionService.validateFoodNutrients(food);
        
        Food foodAtualizado = foodExistente.get();
        
        if (!foodAtualizado.getName().equals(food.getName()) && 
            foodRepository.existsByName(food.getName())) {
            throw new IllegalArgumentException("Alimento com nome '" + food.getName() + "' já existe");
        }
        
        foodAtualizado.setName(food.getName());
        foodAtualizado.setCaloriesPer100g(food.getCaloriesPer100g());
        foodAtualizado.setProteins(food.getProteins());
        foodAtualizado.setCarbs(food.getCarbs());
        foodAtualizado.setFats(food.getFats());
        foodAtualizado.setFiber(food.getFiber());
        foodAtualizado.setSodium(food.getSodium());
        foodAtualizado.setSugar(food.getSugar());
        foodAtualizado.setServingSize(food.getServingSize());
        foodAtualizado.setServingUnit(food.getServingUnit());
        foodAtualizado.setFdcId(food.getFdcId());
        foodAtualizado.setSource(food.getSource());
        
        return foodRepository.save(foodAtualizado);
    }
    
    public void deletar(Long id) {
        if (!foodRepository.existsById(id)) {
            throw new IllegalArgumentException("Alimento não encontrado com ID: " + id);
        }
        foodRepository.deleteById(id);
    }
    
    public List<Food> buscarPorNomeContaining(String nome) {
        // 1. Buscar no banco local
        List<Food> localFoods = foodRepository.findByNameContainingIgnoreCase(nome);
        
        // 2. Se encontrar resultados suficientes, retornar
        if (localFoods.size() >= 5) {
            return localFoods;
        }
        
        // 3. Caso contrário, buscar na API externa
        List<Food> apiFoods = foodDataClient.searchFoods(nome);
        
        // 4. Filtrar duplicatas e salvar no cache
        List<Food> cachedFoods = new ArrayList<>();
        for (Food apiFood : apiFoods) {
            if (apiFood.getFdcId() != null && !foodRepository.existsByFdcId(apiFood.getFdcId())) {
                try {
                    cachedFoods.add(foodRepository.save(apiFood));
                } catch (Exception e) {
                    // Ignorar erros de salvamento individual
                }
            }
        }
        
        // 5. Retornar união (evitando duplicatas na lista final)
        List<Food> result = new ArrayList<>(localFoods);
        for (Food cached : cachedFoods) {
            if (result.stream().noneMatch(f -> f.getFdcId() != null && f.getFdcId().equals(cached.getFdcId()))) {
                result.add(cached);
            }
        }
        
        return result;
    }
}
