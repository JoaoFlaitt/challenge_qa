package com.fiap.begin_projetct.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CriarPlannedMealRequest {
    
    @NotNull(message = "ID do plano alimentar é obrigatório")
    private Long planoId;
    
    @NotNull(message = "ID da refeição é obrigatório")
    private Long refeicaoId;
    
    @NotNull(message = "Data planejada é obrigatória")
    private LocalDate dataPlanejada;
    
    @NotNull(message = "Tipo de refeição é obrigatório")
    private String tipoRefeicao;
}
