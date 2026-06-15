package com.fiap.begin_projetct.controller;

import com.fiap.begin_projetct.dto.RegistrarConsumoRequest;
import com.fiap.begin_projetct.model.FoodLog;
import com.fiap.begin_projetct.service.CreateMealRequest;
import com.fiap.begin_projetct.service.FoodLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/food-logs")
@RequiredArgsConstructor
@Tag(name = "Logs de Consumo", description = "API para gerenciamento de logs de consumo alimentar")
public class FoodLogController implements CrudController<FoodLog, Long, RegistrarConsumoRequest, com.fiap.begin_projetct.dto.FoodLogRequest> {

    private final FoodLogService foodLogService;

    @GetMapping
    @Operation(summary = "Listar todos os logs de consumo", description = "Retorna uma lista com todos os logs de consumo cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de logs retornada com sucesso")
    @Override
    public ResponseEntity<List<FoodLog>> listarTodos() {
        return ResponseEntity.ok(foodLogService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar log por ID", description = "Retorna um log específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Log encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Log não encontrado")
    })
    @Override
    public ResponseEntity<FoodLog> buscarPorId(@PathVariable Long id) {
        Optional<FoodLog> log = foodLogService.buscarPorId(id);
        return log.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Buscar logs por paciente", description = "Retorna todos os logs de consumo de um paciente específico")
    @ApiResponse(responseCode = "200", description = "Logs encontrados com sucesso")
    public ResponseEntity<List<FoodLog>> buscarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(foodLogService.getConsumptionHistory(pacienteId));
    }

    @GetMapping("/paciente/{pacienteId}/periodo")
    @Operation(summary = "Buscar logs por paciente e período", description = "Retorna os logs de consumo de um paciente em um período específico")
    @ApiResponse(responseCode = "200", description = "Logs encontrados com sucesso")
    public ResponseEntity<List<FoodLog>> buscarPorPacienteEPeriodo(
            @PathVariable Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        return ResponseEntity.ok(foodLogService.getConsumptionByDate(pacienteId, dataInicio, dataFim));
    }

    @GetMapping("/paciente/{pacienteId}/data")
    @Operation(summary = "Buscar logs por paciente e data específica", description = "Retorna os logs de consumo de um paciente em uma data específica")
    @ApiResponse(responseCode = "200", description = "Logs encontrados com sucesso")
    public ResponseEntity<List<FoodLog>> buscarPorPacienteEData(
            @PathVariable Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data) {
        return ResponseEntity.ok(foodLogService.getConsumptionBySpecificDate(pacienteId, data));
    }

    @GetMapping("/paciente/{pacienteId}/planejados")
    @Operation(summary = "Buscar consumo planejado por paciente", description = "Retorna todos os logs de consumo planejado de um paciente")
    @ApiResponse(responseCode = "200", description = "Logs encontrados com sucesso")
    public ResponseEntity<List<FoodLog>> buscarConsumoPlanejadoPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(foodLogService.getPlannedConsumptionByPatient(pacienteId));
    }

    @GetMapping("/paciente/{pacienteId}/calorias")
    @Operation(summary = "Obter total de calorias consumidas", description = "Retorna o total de calorias consumidas por um paciente em uma data específica")
    @ApiResponse(responseCode = "200", description = "Total de calorias calculado com sucesso")
    public ResponseEntity<Integer> getTotalCalorias(
            @PathVariable Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data) {
        return ResponseEntity.ok(foodLogService.getTotalCaloriesByPatientAndDate(pacienteId, data));
    }

    /**
     * POST único — substitui os 3 POSTs anteriores (preset, custom, raw).
     * Usa o campo {@code tipo} do DTO para discriminar o fluxo de negócio.
     */
    @PostMapping("/paciente/{pacienteId}")
    @Operation(
        summary = "Registrar consumo alimentar",
        description = "Registra o consumo de um paciente. Use o campo `tipo`:\n" +
                      "- **PRESET**: consumo baseado em refeição existente (requer mealId e foodId)\n" +
                      "- **CUSTOM**: cria refeição na hora e registra o consumo\n" +
                      "- **DIRETO**: registra alimento diretamente sem refeição associada"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Consumo registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou tipo não suportado")
    })
    public ResponseEntity<FoodLog> registrarConsumo(
            @PathVariable Long pacienteId,
            @Valid @RequestBody RegistrarConsumoRequest request) {
        FoodLog log = switch (request.getTipo()) {
            case PRESET -> foodLogService.logConsumptionFromPreset(
                pacienteId, request.getMealId(), request.getFoodId(), request.getQuantity());
            case CUSTOM -> foodLogService.logCustomConsumption(
                pacienteId, toCreateMealRequest(request.getCustomMeal()));
            case DIRETO -> foodLogService.logDiretoConsumption(
                pacienteId, request.getFoodId(), request.getQuantity(), request.getConsumedAt());
        };
        return ResponseEntity.status(HttpStatus.CREATED).body(log);
    }

    // Implementa o método da interface CrudController — não exposível diretamente;
    // use POST /paciente/{pacienteId} para registrar consumo.
    @Override
    public ResponseEntity<FoodLog> criar(RegistrarConsumoRequest request) {
        throw new UnsupportedOperationException("Use POST /api/food-logs/paciente/{pacienteId}");
    }

    @PutMapping("/{id}/quantidade")
    @Operation(summary = "Atualizar quantidade consumida", description = "Atualiza a quantidade de um log de consumo existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Log não encontrado")
    })
    public ResponseEntity<FoodLog> atualizarQuantidade(
            @PathVariable Long id,
            @RequestParam Double newQuantity) {
        return ResponseEntity.ok(foodLogService.updateConsumption(id, newQuantity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar log de consumo", description = "Atualiza os dados de um log de consumo existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Log atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Log não encontrado")
    })
    @Override
    public ResponseEntity<FoodLog> atualizar(@PathVariable Long id, @Valid @RequestBody com.fiap.begin_projetct.dto.FoodLogRequest request) {
        if (foodLogService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        FoodLog foodLog = request.toEntity();
        foodLog.setId(id);
        return ResponseEntity.ok(foodLogService.salvar(foodLog));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar log de consumo", description = "Remove um log de consumo do sistema pelo seu ID")
    @ApiResponse(responseCode = "204", description = "Log deletado com sucesso")
    @Override
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        foodLogService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private CreateMealRequest toCreateMealRequest(RegistrarConsumoRequest.CreateMealRequestBody body) {
        if (body == null) {
            throw new IllegalArgumentException("customMeal é obrigatório para tipo CUSTOM");
        }
        CreateMealRequest req = new CreateMealRequest();
        req.setName(body.getName());
        req.setItems(body.getItems().stream()
            .map(i -> {
                CreateMealRequest.FoodItemRequest fi = new CreateMealRequest.FoodItemRequest();
                fi.setFoodName(i.getFoodName());
                fi.setQuantity(i.getQuantity());
                return fi;
            }).toList());
        return req;
    }
}
