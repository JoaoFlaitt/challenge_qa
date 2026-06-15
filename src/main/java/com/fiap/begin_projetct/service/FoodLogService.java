package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.model.*;
import com.fiap.begin_projetct.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodLogService {
    
    private final FoodLogRepository foodLogRepository;
    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;
    private final NutritionService nutritionService;
    
    /**
     * Caso 7: Registrar consumo baseado em preset
     */
    public FoodLog logConsumptionFromPreset(Long patientId, Long mealId, Long foodId, Double quantity) {
        Meal meal = mealRepository.findByIdAndPatientId(mealId, patientId)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada"));
        
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Alimento não encontrado"));
        
        // Validar
        nutritionService.validateQuantity(quantity);
        nutritionService.validateFoodNutrients(food);
        
        FoodLog log = new FoodLog();
        log.setPatient(new Paciente(patientId));
        log.setMeal(meal);
        log.setFood(food);
        log.setQuantity(quantity);
        log.setConsumedAt(LocalDateTime.now());
        log.setIsPlanned(true); // Consumo baseado em planejamento
        
        return foodLogRepository.save(log);
    }
    
    /**
     * Caso 8: Registrar consumo customizado
     */
    public FoodLog logCustomConsumption(Long patientId, CreateMealRequest mealRequest) {
        // Validar alimentos existentes
        mealRequest.getItems().forEach(item -> {
            if (!foodRepository.existsByName(item.getFoodName())) {
                throw new IllegalArgumentException("Alimento não encontrado: " + item.getFoodName());
            }
        });
        
        // Criar refeição customizada
        Meal meal = new Meal();
        meal.setPatient(new Paciente(patientId));
        meal.setName(mealRequest.getName());
        meal.setMealDate(LocalDateTime.now());
        meal.setIsPreset(false);
        
        // Adicionar itens
        List<MealItem> items = mealRequest.getItems().stream()
                .map(item -> createMealItem(item, meal))
                .toList();
        meal.setItems(items);
        
        // Salvar refeição
        Meal savedMeal = mealRepository.save(meal);
        
        // Criar log de consumo para cada alimento
        List<FoodLog> logs = items.stream()
                .map(item -> {
                    FoodLog log = new FoodLog();
                    log.setPatient(new Paciente(patientId));
                    log.setMeal(savedMeal);
                    log.setFood(item.getFood());
                    log.setQuantity(item.getQuantity());
                    log.setConsumedAt(LocalDateTime.now());
                    log.setIsPlanned(false); // Consumo customizado
                    return log;
                })
                .toList();
        
        return foodLogRepository.saveAll(logs).get(0); // Retorna primeiro log
    }

    /**
     * DIRETO — registra um alimento diretamente sem vincular a uma refeição existente.
     */
    public FoodLog logDiretoConsumption(Long patientId, Long foodId, Double quantity, java.time.LocalDateTime consumedAt) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Alimento não encontrado com ID: " + foodId));

        nutritionService.validateQuantity(quantity);
        nutritionService.validateFoodNutrients(food);

        FoodLog log = new FoodLog();
        log.setPatient(new Paciente(patientId));
        log.setFood(food);
        log.setQuantity(quantity);
        log.setConsumedAt(consumedAt != null ? consumedAt : java.time.LocalDateTime.now());
        log.setIsPlanned(false);

        return foodLogRepository.save(log);
    }
    
    /**
     * Caso 9: Alterar consumo real
     */
    public FoodLog updateConsumption(Long logId, Double newQuantity) {
        FoodLog log = foodLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Log não encontrado"));
        
        // Validar nova quantidade
        nutritionService.validateQuantity(newQuantity);
        
        log.setQuantity(newQuantity);
        
        // Recalcular totais automaticamente via @PreUpdate
        return foodLogRepository.save(log);
    }
    
    /**
     * Buscar histórico de consumo de um paciente
     */
    public List<FoodLog> getConsumptionHistory(Long patientId) {
        return foodLogRepository.findByPatientIdOrderByConsumedAtDesc(patientId);
    }
    
    /**
     * Buscar consumo por data
     */
    public List<FoodLog> getConsumptionByDate(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
        return foodLogRepository.findByPatientIdAndConsumedAtBetween(patientId, startDate, endDate);
    }
    
    /**
     * Listar todos os logs de consumo
     */
    public List<FoodLog> listarTodos() {
        return foodLogRepository.findAll();
    }
    
    /**
     * Buscar log por ID
     */
    public Optional<FoodLog> buscarPorId(Long id) {
        return foodLogRepository.findById(id);
    }
    
    /**
     * Buscar consumo por data específica
     */
    public List<FoodLog> getConsumptionBySpecificDate(Long patientId, LocalDateTime date) {
        // Get all consumption and filter by date
        List<FoodLog> allLogs = foodLogRepository.findByPatientIdOrderByConsumedAtDesc(patientId);
        return allLogs.stream()
                .filter(log -> log.getConsumedAt().toLocalDate().equals(date.toLocalDate()))
                .toList();
    }
    
    /**
     * Obter total de calorias consumidas por paciente e data
     */
    public Integer getTotalCaloriesByPatientAndDate(Long patientId, LocalDateTime date) {
        // Get all consumption and filter by date, then sum calories
        List<FoodLog> allLogs = foodLogRepository.findByPatientIdOrderByConsumedAtDesc(patientId);
        return allLogs.stream()
                .filter(log -> log.getConsumedAt().toLocalDate().equals(date.toLocalDate()))
                .mapToInt(FoodLog::getTotalCalories)
                .sum();
    }
    
    /**
     * Buscar consumo planejado de um paciente
     */
    public List<FoodLog> getPlannedConsumptionByPatient(Long patientId) {
        return foodLogRepository.findPlannedConsumptionByPatientId(patientId);
    }
    
    /**
     * Salvar log de consumo
     */
    public FoodLog salvar(FoodLog foodLog) {
        return foodLogRepository.save(foodLog);
    }
    
    /**
     * Deletar log de consumo
     */
    public void deletar(Long id) {
        if (!foodLogRepository.existsById(id)) {
            throw new IllegalArgumentException("Log de consumo não encontrado com ID: " + id);
        }
        foodLogRepository.deleteById(id);
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
}
