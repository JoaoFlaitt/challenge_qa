package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.model.*;
import com.fiap.begin_projetct.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MealService {
    
    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;
    private final NutritionService nutritionService;
    
    /**
     * Caso 1: Criar refeição customizada
     */
    public Meal createCustomMeal(Long patientId, CreateMealRequest request) {
        // Validar alimentos existentes
        request.getItems().forEach(item -> {
            if (!foodRepository.existsByName(item.getFoodName())) {
                throw new IllegalArgumentException("Alimento não encontrado: " + item.getFoodName());
            }
        });
        
        // Criar refeição
        Meal meal = new Meal();
        meal.setPatient(new Paciente(patientId));
        meal.setName(request.getName());
        meal.setMealDate(LocalDateTime.now());
        meal.setIsPreset(false);
        
        // Adicionar itens
        List<MealItem> items = request.getItems().stream()
                .map(item -> createMealItem(item, meal))
                .toList();
        meal.setItems(items);
        
        // Calcular totais automaticamente via @PrePersist
        return mealRepository.save(meal);
    }
    
    /**
     * Caso 2: Criar preset de refeição
     */
    public Meal createPresetMeal(Long patientId, CreateMealRequest request, String presetName) {
        // Validar se já existe preset com mesmo nome
        if (mealRepository.existsByPresetNameAndPatientId(presetName, patientId)) {
            throw new IllegalArgumentException("Já existe preset com nome: " + presetName);
        }
        
        Meal meal = new Meal();
        meal.setPatient(new Paciente(patientId));
        meal.setName(request.getName());
        meal.setMealDate(LocalDateTime.now());
        meal.setIsPreset(true);
        meal.setPresetName(presetName);
        
        List<MealItem> items = request.getItems().stream()
                .map(item -> createMealItem(item, meal))
                .toList();
        meal.setItems(items);
        
        return mealRepository.save(meal);
    }
    
    /**
     * Caso 4: Planejar refeição com preset
     */
    public Meal planMealWithPreset(Long patientId, Long dietPlanId, LocalDate plannedDate, String mealType, Long presetMealId) {
        Meal presetMeal = mealRepository.findByIdAndPatientId(presetMealId, patientId)
                .orElseThrow(() -> new IllegalArgumentException("Preset não encontrado"));
        
        if (!presetMeal.getIsPreset()) {
            throw new IllegalArgumentException("Refeição não é um preset");
        }
        
        // Criar nova refeição planejada baseada no preset
        Meal plannedMeal = new Meal();
        plannedMeal.setPatient(new Paciente(patientId));
        plannedMeal.setName(presetMeal.getName());
        plannedMeal.setMealDate(plannedDate.atStartOfDay());
        plannedMeal.setIsPreset(false);
        
        // Copiar itens do preset
        List<MealItem> plannedItems = presetMeal.getItems().stream()
                .map(presetItem -> {
                    MealItem newItem = new MealItem();
                    newItem.setMeal(plannedMeal);
                    newItem.setFood(presetItem.getFood());
                    newItem.setQuantity(presetItem.getQuantity());
                    // Copiar nutrientes calculados
                    newItem.setCalories(presetItem.getCalories());
                    newItem.setProteins(presetItem.getProteins());
                    newItem.setCarbs(presetItem.getCarbs());
                    newItem.setFats(presetItem.getFats());
                    newItem.setFiber(presetItem.getFiber());
                    newItem.setSodium(presetItem.getSodium());
                    newItem.setSugar(presetItem.getSugar());
                    return newItem;
                })
                .toList();
        
        plannedMeal.setItems(plannedItems);
        return mealRepository.save(plannedMeal);
    }
    
    /**
     * Caso 5: Planejar refeição customizada
     */
    public Meal planCustomMeal(Long patientId, Long dietPlanId, LocalDate plannedDate, String mealType, CreateMealRequest request) {
        Meal plannedMeal = new Meal();
        plannedMeal.setPatient(new Paciente(patientId));
        plannedMeal.setName(request.getName());
        plannedMeal.setMealDate(plannedDate.atStartOfDay());
        plannedMeal.setIsPreset(false);
        
        List<MealItem> items = request.getItems().stream()
                .map(item -> createMealItem(item, plannedMeal))
                .toList();
        plannedMeal.setItems(items);
        
        return mealRepository.save(plannedMeal);
    }
    
    /**
     * Caso 6: Evitar conflito (regra importante)
     */
    public void validateMealConflict(Meal meal, boolean isUsingPreset) {
        if (isUsingPreset && !meal.getIsPreset()) {
            throw new IllegalArgumentException("Não é possível usar preset e refeição customizada simultaneamente");
        }
    }
    
    private MealItem createMealItem(CreateMealRequest.FoodItemRequest itemRequest, Meal meal) {
        Food food = foodRepository.findByName(itemRequest.getFoodName())
                .orElseThrow(() -> new IllegalArgumentException("Alimento não encontrado: " + itemRequest.getFoodName()));
        
        MealItem item = new MealItem();
        item.setMeal(meal);
        item.setFood(food);
        item.setQuantity(itemRequest.getQuantity());
        
        // Validações
        nutritionService.validateQuantity(itemRequest.getQuantity());
        nutritionService.validateFoodNutrients(food);
        
        return item;
    }
    
    /**
     * Buscar refeições de um paciente
     */
    public List<Meal> getMealsByPatient(Long patientId) {
        return mealRepository.findByPatientIdOrderByMealDateDesc(patientId);
    }
    
    /**
     * Buscar presets de um paciente
     */
    public List<Meal> getPresetsByPatient(Long patientId) {
        return mealRepository.findPresetsByPatientId(patientId);
    }
    
    /**
     * Listar todas as refeições
     */
    public List<Meal> listarTodos() {
        return mealRepository.findAll();
    }
    
    /**
     * Buscar refeição por ID
     */
    public Optional<Meal> buscarPorId(Long id) {
        return mealRepository.findById(id);
    }
    
    /**
     * Buscar refeições de um paciente por período
     */
    public List<Meal> getMealsByPatientAndPeriod(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
        return mealRepository.findByPatientIdAndMealDateBetweenOrderByMealDate(patientId, startDate, endDate);
    }
    
    /**
     * Atualizar refeição
     */
    public Meal atualizar(Long id, Meal meal) {
        Optional<Meal> mealExistente = mealRepository.findById(id);
        if (mealExistente.isEmpty()) {
            throw new IllegalArgumentException("Refeição não encontrada com ID: " + id);
        }
        
        Meal mealAtualizado = mealExistente.get();
        mealAtualizado.setName(meal.getName());
        mealAtualizado.setMealDate(meal.getMealDate());
        mealAtualizado.setIsPreset(meal.getIsPreset());
        mealAtualizado.setPresetName(meal.getPresetName());
        mealAtualizado.setItems(meal.getItems());
        
        return mealRepository.save(mealAtualizado);
    }
    
    /**
     * Deletar refeição
     */
    public void deletar(Long id) {
        if (!mealRepository.existsById(id)) {
            throw new IllegalArgumentException("Refeição não encontrada com ID: " + id);
        }
        mealRepository.deleteById(id);
    }
}
