package com.fiap.begin_projetct.controller;

import com.fiap.begin_projetct.model.DietPlan;
import com.fiap.begin_projetct.model.PlannedMeal;
import com.fiap.begin_projetct.service.DietPlanService;
import com.fiap.begin_projetct.service.CreateDietPlanRequest;
import com.fiap.begin_projetct.service.CreateMealRequest;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diet-plans")
@RequiredArgsConstructor
@Tag(name = "Planos Alimentares", description = "API para gerenciamento de planos alimentares")
public class DietPlanController implements CrudController<DietPlan, Long, CreateDietPlanRequest, CreateDietPlanRequest> {
    
    private final DietPlanService dietPlanService;
    
    @GetMapping
    @Operation(summary = "Listar todos os planos alimentares", description = "Retorna uma lista com todos os planos alimentares cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de planos retornada com sucesso")
    @Override
    public ResponseEntity<List<DietPlan>> listarTodos() {
        List<DietPlan> plans = dietPlanService.listarTodos();
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar plano por ID", description = "Retorna um plano específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plano encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Plano não encontrado")
    })
    @Override
    public ResponseEntity<DietPlan> buscarPorId(@PathVariable Long id) {
        Optional<DietPlan> plan = dietPlanService.buscarPorId(id);
        return plan.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Buscar planos por paciente", description = "Retorna todos os planos alimentares de um paciente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Planos encontrados com sucesso")
    })
    public ResponseEntity<List<DietPlan>> buscarPorPaciente(@PathVariable Long pacienteId) {
        List<DietPlan> plans = dietPlanService.buscarPorPaciente(pacienteId);
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/paciente/{pacienteId}/periodo")
    @Operation(summary = "Buscar planos por paciente e período", description = "Retorna os planos alimentares de um paciente em um período específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Planos encontrados com sucesso")
    })
    public ResponseEntity<List<DietPlan>> buscarPorPacienteEPeriodo(
            @PathVariable Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<DietPlan> plans = dietPlanService.buscarPorPacienteEPeriodo(pacienteId, dataInicio, dataFim);
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/paciente/{pacienteId}/ativos")
    @Operation(summary = "Buscar planos ativos por paciente e data", description = "Retorna os planos ativos de um paciente em uma data específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Planos encontrados com sucesso")
    })
    public ResponseEntity<List<DietPlan>> buscarPlanosAtivosPorPacienteEData(
            @PathVariable Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<DietPlan> plans = dietPlanService.buscarPlanosAtivosPorPacienteEData(pacienteId, data);
        return ResponseEntity.ok(plans);
    }
    
    @PostMapping("/paciente/{pacienteId}")
    @Operation(summary = "Criar plano alimentar", description = "Cria um novo plano alimentar para um paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plano criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<DietPlan> criarPlano(
            @PathVariable Long pacienteId,
            @Valid @RequestBody CreateDietPlanRequest request) {
        DietPlan plan = dietPlanService.createPlan(pacienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }

    // Implementa CrudController — prefira POST /paciente/{id} para associar o paciente.
    @Override
    public ResponseEntity<DietPlan> criar(CreateDietPlanRequest request) {
        throw new UnsupportedOperationException("Use POST /api/diet-plans/paciente/{pacienteId}");
    }
    
    @PostMapping("/{planoId}/refeicao-preset")
    @Operation(summary = "Planejar refeição com preset", description = "Planeja uma refeição usando um preset existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Refeição planejada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<PlannedMeal> planejarRefeicaoComPreset(
            @PathVariable Long planoId,
            @RequestParam Long pacienteId,
            @RequestParam Long presetMealId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPlanejada,
            @RequestParam String tipoRefeicao) {
        PlannedMeal plannedMeal = dietPlanService.planMealWithPreset(
            pacienteId, planoId, dataPlanejada, tipoRefeicao, presetMealId);
        return ResponseEntity.status(HttpStatus.CREATED).body(plannedMeal);
    }
    
    @PostMapping("/{planoId}/refeicao-custom")
    @Operation(summary = "Planejar refeição customizada", description = "Planeja uma refeição customizada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Refeição planejada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<PlannedMeal> planejarRefeicaoCustomizada(
            @PathVariable Long planoId,
            @RequestParam Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPlanejada,
            @RequestParam String tipoRefeicao,
            @Valid @RequestBody CreateMealRequest mealRequest) {
        PlannedMeal plannedMeal = dietPlanService.planCustomMeal(
            pacienteId, planoId, dataPlanejada, tipoRefeicao, mealRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(plannedMeal);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar plano alimentar", description = "Atualiza os dados de um plano alimentar existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plano atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Plano não encontrado")
    })
    @Override
    public ResponseEntity<DietPlan> atualizar(@PathVariable Long id, @Valid @RequestBody CreateDietPlanRequest request) {
        DietPlan planoAtualizado = dietPlanService.atualizar(id, request.toEntity());
        return ResponseEntity.ok(planoAtualizado);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar plano alimentar", description = "Remove um plano alimentar do sistema pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Plano deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Plano não encontrado")
    })
    @Override
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        dietPlanService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
