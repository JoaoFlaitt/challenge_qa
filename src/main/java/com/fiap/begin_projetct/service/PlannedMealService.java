package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.dto.CriarPlannedMealRequest;
import com.fiap.begin_projetct.model.DietPlan;
import com.fiap.begin_projetct.model.Meal;
import com.fiap.begin_projetct.model.PlannedMeal;
import com.fiap.begin_projetct.repository.DietPlanRepository;
import com.fiap.begin_projetct.repository.MealRepository;
import com.fiap.begin_projetct.repository.PlannedMealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlannedMealService {
    
    private final PlannedMealRepository plannedMealRepository;
    private final DietPlanRepository dietPlanRepository;
    private final MealRepository mealRepository;
    
    /**
     * Listar todas as refeições planejadas
     */
    public List<PlannedMeal> listarTodos() {
        return plannedMealRepository.findAll();
    }
    
    /**
     * Buscar refeição planejada por ID
     */
    public Optional<PlannedMeal> buscarPorId(Long id) {
        return plannedMealRepository.findById(id);
    }
    
    /**
     * Buscar refeições planejadas por plano alimentar
     */
    public List<PlannedMeal> buscarPorPlanoAlimentar(Long dietPlanId) {
        return plannedMealRepository.findByDietPlanIdOrderByPlannedDateAsc(dietPlanId);
    }
    
    /**
     * Buscar refeições planejadas por paciente
     */
    public List<PlannedMeal> buscarPorPaciente(Long patientId) {
        // Get all diet plans for the patient and then get their planned meals
        List<DietPlan> dietPlans = dietPlanRepository.findByPatientIdOrderByStartDateDesc(patientId);
        return dietPlans.stream()
                .flatMap(plan -> plannedMealRepository.findByDietPlanIdOrderByPlannedDateAsc(plan.getId()).stream())
                .distinct()
                .sorted((pm1, pm2) -> pm1.getPlannedDate().compareTo(pm2.getPlannedDate()))
                .toList();
    }
    
    /**
     * Buscar refeições planejadas por paciente e data
     */
    public List<PlannedMeal> buscarPorPacienteEData(Long patientId, LocalDate date) {
        // Get all diet plans for patient and filter by date
        List<DietPlan> allDietPlans = dietPlanRepository.findByPatientIdOrderByStartDateDesc(patientId);
        return allDietPlans.stream()
                .filter(plan -> !date.isBefore(plan.getStartDate()) && !date.isAfter(plan.getEndDate()))
                .flatMap(plan -> plannedMealRepository.findByDietPlanIdAndPlannedDate(plan.getId(), date).stream())
                .toList();
    }
    
    /**
     * Buscar refeições planejadas por paciente e período
     */
    public List<PlannedMeal> buscarPorPacienteEPeriodo(Long patientId, LocalDate startDate, LocalDate endDate) {
        // Get all diet plans for patient and filter by date range
        List<DietPlan> allDietPlans = dietPlanRepository.findByPatientIdOrderByStartDateDesc(patientId);
        return allDietPlans.stream()
                .flatMap(plan -> plannedMealRepository.findByDietPlanIdAndPlannedDateBetween(plan.getId(), startDate, endDate).stream())
                .toList();
    }
    
    /**
     * Buscar refeições planejadas por tipo de refeição
     */
    public List<PlannedMeal> buscarPorTipoRefeicao(String mealType) {
        return plannedMealRepository.findByMealTypeOrderByPlannedDateAsc(mealType);
    }
    
    /**
     * Criar nova refeição planejada
     */
    public PlannedMeal salvar(PlannedMeal plannedMeal) {
        // Validar se o plano alimentar existe
        if (plannedMeal.getDietPlan() != null && plannedMeal.getDietPlan().getId() != null) {
            if (!dietPlanRepository.existsById(plannedMeal.getDietPlan().getId())) {
                throw new IllegalArgumentException("Plano alimentar não encontrado com ID: " + plannedMeal.getDietPlan().getId());
            }
        }
        
        // Validar se a refeição existe
        if (plannedMeal.getMeal() != null && plannedMeal.getMeal().getId() != null) {
            if (!mealRepository.existsById(plannedMeal.getMeal().getId())) {
                throw new IllegalArgumentException("Refeição não encontrada com ID: " + plannedMeal.getMeal().getId());
            }
        }
        
        // Validar se a data planejada está dentro do período do plano alimentar
        if (plannedMeal.getDietPlan() != null && plannedMeal.getPlannedDate() != null) {
            if (plannedMeal.getPlannedDate().isBefore(plannedMeal.getDietPlan().getStartDate()) ||
                plannedMeal.getPlannedDate().isAfter(plannedMeal.getDietPlan().getEndDate())) {
                throw new IllegalArgumentException("Data planejada está fora do período do plano alimentar");
            }
        }
        
        return plannedMealRepository.save(plannedMeal);
    }
    
    /**
     * Criar refeição planejada com IDs
     */
    public PlannedMeal criarRefeicaoPlanejada(Long dietPlanId, Long mealId, LocalDate plannedDate, String mealType) {
        DietPlan dietPlan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Plano alimentar não encontrado com ID: " + dietPlanId));
        
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada com ID: " + mealId));
        
        // Validar se a data planejada está dentro do período do plano alimentar
        if (plannedDate.isBefore(dietPlan.getStartDate()) || plannedDate.isAfter(dietPlan.getEndDate())) {
            throw new IllegalArgumentException("Data planejada está fora do período do plano alimentar");
        }
        
        PlannedMeal plannedMeal = new PlannedMeal();
        plannedMeal.setDietPlan(dietPlan);
        plannedMeal.setMeal(meal);
        plannedMeal.setPlannedDate(plannedDate);
        plannedMeal.setMealType(mealType);
        plannedMeal.setIsPresetUsed(meal.getIsPreset());
        
        if (meal.getIsPreset() && meal.getPresetName() != null) {
            plannedMeal.setPresetName(meal.getPresetName());
        }
        
        return plannedMealRepository.save(plannedMeal);
    }
    
    /**
     * Atualizar refeição planejada
     */
    public PlannedMeal atualizar(Long id, PlannedMeal plannedMeal) {
        Optional<PlannedMeal> itemExistente = plannedMealRepository.findById(id);
        if (itemExistente.isEmpty()) {
            throw new IllegalArgumentException("Refeição planejada não encontrada com ID: " + id);
        }
        
        // Validar se o plano alimentar existe
        if (plannedMeal.getDietPlan() != null && plannedMeal.getDietPlan().getId() != null) {
            if (!dietPlanRepository.existsById(plannedMeal.getDietPlan().getId())) {
                throw new IllegalArgumentException("Plano alimentar não encontrado com ID: " + plannedMeal.getDietPlan().getId());
            }
        }
        
        // Validar se a refeição existe
        if (plannedMeal.getMeal() != null && plannedMeal.getMeal().getId() != null) {
            if (!mealRepository.existsById(plannedMeal.getMeal().getId())) {
                throw new IllegalArgumentException("Refeição não encontrada com ID: " + plannedMeal.getMeal().getId());
            }
        }
        
        // Validar se a data planejada está dentro do período do plano alimentar
        if (plannedMeal.getDietPlan() != null && plannedMeal.getPlannedDate() != null) {
            if (plannedMeal.getPlannedDate().isBefore(plannedMeal.getDietPlan().getStartDate()) ||
                plannedMeal.getPlannedDate().isAfter(plannedMeal.getDietPlan().getEndDate())) {
                throw new IllegalArgumentException("Data planejada está fora do período do plano alimentar");
            }
        }
        
        PlannedMeal itemAtualizado = itemExistente.get();
        itemAtualizado.setDietPlan(plannedMeal.getDietPlan());
        itemAtualizado.setMeal(plannedMeal.getMeal());
        itemAtualizado.setPlannedDate(plannedMeal.getPlannedDate());
        itemAtualizado.setMealType(plannedMeal.getMealType());
        itemAtualizado.setIsPresetUsed(plannedMeal.getIsPresetUsed());
        itemAtualizado.setPresetName(plannedMeal.getPresetName());
        
        return plannedMealRepository.save(itemAtualizado);
    }
    
    /**
     * Deletar refeição planejada
     */
    public void deletar(Long id) {
        if (!plannedMealRepository.existsById(id)) {
            throw new IllegalArgumentException("Refeição planejada não encontrada com ID: " + id);
        }
        plannedMealRepository.deleteById(id);
    }
    
    /**
     * Deletar refeições planejadas por plano alimentar
     */
    public void deletarPorPlanoAlimentar(Long dietPlanId) {
        if (!dietPlanRepository.existsById(dietPlanId)) {
            throw new IllegalArgumentException("Plano alimentar não encontrado com ID: " + dietPlanId);
        }
        List<PlannedMeal> plannedMeals = plannedMealRepository.findByDietPlanIdOrderByPlannedDateAsc(dietPlanId);
        plannedMealRepository.deleteAll(plannedMeals);
    }
    
    /**
     * Contar refeições planejadas por plano alimentar
     */
    public Long contarPorPlanoAlimentar(Long dietPlanId) {
        return plannedMealRepository.countByDietPlanId(dietPlanId);
    }
    
    /**
     * Contar refeições planejadas por paciente
     */
    public Long contarPorPaciente(Long patientId) {
        // Get all diet plans for patient and count their planned meals
        List<DietPlan> dietPlans = dietPlanRepository.findByPatientIdOrderByStartDateDesc(patientId);
        return dietPlans.stream()
                .mapToLong(plan -> plannedMealRepository.countByDietPlanId(plan.getId()))
                .sum();
    }

    /**
     * Atualizar refeição planejada usando DTO com IDs (usado pelo controller refatorado).
     */
    public PlannedMeal atualizarPorRequest(Long id, CriarPlannedMealRequest request) {
        PlannedMeal itemAtualizado = plannedMealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Refeição planejada não encontrada com ID: " + id));

        DietPlan dietPlan = dietPlanRepository.findById(request.getPlanoId())
                .orElseThrow(() -> new IllegalArgumentException("Plano alimentar não encontrado com ID: " + request.getPlanoId()));

        Meal meal = mealRepository.findById(request.getRefeicaoId())
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada com ID: " + request.getRefeicaoId()));

        LocalDate plannedDate = request.getDataPlanejada();
        if (plannedDate.isBefore(dietPlan.getStartDate()) || plannedDate.isAfter(dietPlan.getEndDate())) {
            throw new IllegalArgumentException("Data planejada está fora do período do plano alimentar");
        }

        itemAtualizado.setDietPlan(dietPlan);
        itemAtualizado.setMeal(meal);
        itemAtualizado.setPlannedDate(plannedDate);
        itemAtualizado.setMealType(request.getTipoRefeicao());
        itemAtualizado.setIsPresetUsed(meal.getIsPreset());
        itemAtualizado.setPresetName(meal.getIsPreset() ? meal.getPresetName() : null);

        return plannedMealRepository.save(itemAtualizado);
    }
}
