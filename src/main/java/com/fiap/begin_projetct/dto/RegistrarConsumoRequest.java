package com.fiap.begin_projetct.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO unificado para registro de consumo alimentar.
 *
 * Use o campo {@code tipo} para discriminar o fluxo:
 * <ul>
 *   <li>{@code PRESET}  — consumo baseado em refeição preset existente (requer mealId e foodId)</li>
 *   <li>{@code CUSTOM}  — consumo de refeição customizada criada na hora (requer mealRequest dentro do corpo)</li>
 *   <li>{@code DIRETO}  — registro direto de um alimento sem refeição associada (requer foodId)</li>
 * </ul>
 */
@Data
public class RegistrarConsumoRequest {

    public enum Tipo { PRESET, CUSTOM, DIRETO }

    @NotNull(message = "O tipo de consumo é obrigatório (PRESET, CUSTOM ou DIRETO)")
    private Tipo tipo;

    /** Obrigatório para PRESET e DIRETO */
    private Long foodId;

    /** Obrigatório para PRESET */
    private Long mealId;

    /** Obrigatório para PRESET e DIRETO */
    @DecimalMin(value = "0.1", message = "Quantidade deve ser maior que zero")
    private Double quantity;

    /** Opcional — defaults para now() se não informado */
    private LocalDateTime consumedAt;

    /**
     * Obrigatório para CUSTOM — descreve a refeição que será criada e imediatamente registrada.
     * Reutiliza o mesmo DTO de criação de refeição.
     */
    private CreateMealRequestBody customMeal;

    @Data
    public static class CreateMealRequestBody {
        @NotNull(message = "Nome da refeição é obrigatório")
        private String name;
        @NotNull(message = "Lista de itens é obrigatória")
        private java.util.List<FoodItemBody> items;
    }

    @Data
    public static class FoodItemBody {
        @NotNull(message = "Nome do alimento é obrigatório")
        private String foodName;
        @DecimalMin(value = "0.1", message = "Quantidade deve ser maior que zero")
        private Double quantity;
    }
}
