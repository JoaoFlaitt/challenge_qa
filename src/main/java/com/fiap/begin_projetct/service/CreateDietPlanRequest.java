package com.fiap.begin_projetct.service;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
public class CreateDietPlanRequest {
    
    @NotNull(message = "ID do paciente é obrigatório")
    private Long patientId;
    
    @NotNull(message = "Data inicial é obrigatória")
    @FutureOrPresent(message = "Data inicial não pode ser no passado")
    private LocalDate startDate;
    
    @NotNull(message = "Data final é obrigatória")
    private LocalDate endDate;
    
    @Min(value = 800, message = "Calorias diárias devem ser no mínimo 800")
    @Max(value = 5000, message = "Calorias diárias não devem ultrapassar 5000")
    private Integer targetCalories;
    
    @Min(value = 20, message = "Proteínas diárias devem ser no mínimo 20g")
    @Max(value = 300, message = "Proteínas diárias não devem ultrapassar 300g")
    private Double targetProteins;
    
    @Min(value = 100, message = "Carboidratos diários devem ser no mínimo 100g")
    @Max(value = 500, message = "Carboidratos diários não devem ultrapassar 500g")
    private Double targetCarbs;
    
    @Min(value = 20, message = "Gorduras diárias devem ser no mínimo 20g")
    @Max(value = 150, message = "Gorduras diárias não devem ultrapassar 150g")
    private Double targetFats;

    public com.fiap.begin_projetct.model.DietPlan toEntity() {
        com.fiap.begin_projetct.model.DietPlan dp = new com.fiap.begin_projetct.model.DietPlan();
        dp.setStartDate(this.startDate);
        dp.setEndDate(this.endDate);
        dp.setTargetCalories(this.targetCalories);
        dp.setTargetProteins(this.targetProteins);
        dp.setTargetCarbs(this.targetCarbs);
        dp.setTargetFats(this.targetFats);
        return dp;
    }
}
