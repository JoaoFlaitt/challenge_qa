package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.model.*;
import com.fiap.begin_projetct.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DietPlanService {
    
    private final DietPlanRepository dietPlanRepository;
    private final MealRepository mealRepository;
    private final PlannedMealRepository plannedMealRepository;
    
    /**
     * Caso 7: Criar plano alimentar
     */
    public DietPlan createPlan(Long patientId, CreateDietPlanRequest request) {
        // Validar datas
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Data final deve ser posterior à data inicial");
        }
        
        DietPlan plan = new DietPlan();
        plan.setPatient(new Paciente(patientId));
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setTargetCalories(request.getTargetCalories());
        plan.setTargetProteins(request.getTargetProteins());
        plan.setTargetCarbs(request.getTargetCarbs());
        plan.setTargetFats(request.getTargetFats());
        
        return dietPlanRepository.save(plan);
    }
    
    /**
     * Caso 8: Planejar refeição com preset
     */
    public PlannedMeal planMealWithPreset(Long patientId, Long dietPlanId, LocalDate plannedDate, String mealType, Long presetMealId) {
        DietPlan plan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Plano alimentar não encontrado"));
        
        Meal presetMeal = mealRepository.findByIdAndPatientId(presetMealId, patientId)
                .orElseThrow(() -> new IllegalArgumentException("Preset não encontrado"));
        
        if (!presetMeal.getIsPreset()) {
            throw new IllegalArgumentException("Refeição não é um preset");
        }
        
        PlannedMeal plannedMeal = new PlannedMeal();
        plannedMeal.setDietPlan(plan);
        plannedMeal.setMeal(presetMeal);
        plannedMeal.setPlannedDate(plannedDate);
        plannedMeal.setMealType(mealType);
        plannedMeal.setIsPresetUsed(true);
        plannedMeal.setPresetName(presetMeal.getPresetName());
        
        return plannedMealRepository.save(plannedMeal);
    }
    
    /**
     * Caso 9: Planejar refeição customizada
     */
    public PlannedMeal planCustomMeal(Long patientId, Long dietPlanId, LocalDate plannedDate, String mealType, CreateMealRequest mealRequest) {
        DietPlan plan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Plano alimentar não encontrado"));
        
        // Criar refeição customizada
        Meal customMeal = new Meal();
        customMeal.setPatient(new Paciente(patientId));
        customMeal.setName(mealRequest.getName());
        customMeal.setMealDate(plannedDate.atStartOfDay());
        customMeal.setIsPreset(false);
        
        // Salvar a refeição antes de associá-la ao PlannedMeal
        customMeal = mealRepository.save(customMeal);
        
        PlannedMeal plannedMeal = new PlannedMeal();
        plannedMeal.setDietPlan(plan);
        plannedMeal.setMeal(customMeal);
        plannedMeal.setPlannedDate(plannedDate);
        plannedMeal.setMealType(mealType);
        plannedMeal.setIsPresetUsed(false);
        
        return plannedMealRepository.save(plannedMeal);
    }
    
    /**
     * Listar todos os planos alimentares
     */
    public List<DietPlan> listarTodos() {
        return dietPlanRepository.findAll();
    }
    
    /**
     * Buscar plano por ID
     */
    public Optional<DietPlan> buscarPorId(Long id) {
        return dietPlanRepository.findById(id);
    }
    
    /**
     * Buscar planos por paciente
     */
    public List<DietPlan> buscarPorPaciente(Long patientId) {
        return dietPlanRepository.findByPatientIdOrderByStartDateDesc(patientId);
    }
    
    /**
     * Buscar planos por paciente e período
     */
    public List<DietPlan> buscarPorPacienteEPeriodo(Long patientId, LocalDate startDate, LocalDate endDate) {
        return dietPlanRepository.findByPatientIdAndPeriodBetween(patientId, startDate, endDate);
    }
    
    /**
     * Buscar planos ativos de um paciente em uma data específica
     */
    public List<DietPlan> buscarPlanosAtivosPorPacienteEData(Long patientId, LocalDate date) {
        // Get all diet plans for patient and filter by date
        List<DietPlan> allDietPlans = dietPlanRepository.findByPatientIdOrderByStartDateDesc(patientId);
        return allDietPlans.stream()
                .filter(plan -> !date.isBefore(plan.getStartDate()) && !date.isAfter(plan.getEndDate()))
                .toList();
    }
    
    /**
     * Obter total de calorias consumidas por paciente e data
     */
    public Integer getTotalCaloriesByPatientAndDate(Long patientId, LocalDate date) {
        // Get all diet plans for patient and filter by date
        List<DietPlan> allDietPlans = dietPlanRepository.findByPatientIdOrderByStartDateDesc(patientId);
        return allDietPlans.stream()
                .filter(plan -> !date.isBefore(plan.getStartDate()) && !date.isAfter(plan.getEndDate()))
                .mapToInt(DietPlan::getTargetCalories)
                .sum();
    }
    
    /**
     * Atualizar plano alimentar
     */
    public DietPlan atualizar(Long id, DietPlan dietPlan) {
        Optional<DietPlan> planoExistente = dietPlanRepository.findById(id);
        if (planoExistente.isEmpty()) {
            throw new IllegalArgumentException("Plano alimentar não encontrado com ID: " + id);
        }
        
        // Validar datas
        if (dietPlan.getEndDate().isBefore(dietPlan.getStartDate())) {
            throw new IllegalArgumentException("Data final deve ser posterior à data inicial");
        }
        
        DietPlan planoAtualizado = planoExistente.get();
        // Manter o paciente original — não permitir troca de paciente
        planoAtualizado.setStartDate(dietPlan.getStartDate());
        planoAtualizado.setEndDate(dietPlan.getEndDate());
        planoAtualizado.setTargetCalories(dietPlan.getTargetCalories());
        planoAtualizado.setTargetProteins(dietPlan.getTargetProteins());
        planoAtualizado.setTargetCarbs(dietPlan.getTargetCarbs());
        planoAtualizado.setTargetFats(dietPlan.getTargetFats());
        
        return dietPlanRepository.save(planoAtualizado);
    }
    
    /**
     * Deletar plano alimentar
     */
    public void deletar(Long id) {
        if (!dietPlanRepository.existsById(id)) {
            throw new IllegalArgumentException("Plano alimentar não encontrado com ID: " + id);
        }
        dietPlanRepository.deleteById(id);
    }
}
