package com.fiap.begin_projetct.controller;

import com.fiap.begin_projetct.dto.CriarPlannedMealRequest;
import com.fiap.begin_projetct.model.PlannedMeal;
import com.fiap.begin_projetct.service.PlannedMealService;
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
@RequestMapping("/api/planned-meals")
@RequiredArgsConstructor
@Tag(name = "Refeições Planejadas", description = "API para gerenciamento de refeições planejadas")
public class PlannedMealController implements CrudController<PlannedMeal, Long, CriarPlannedMealRequest, CriarPlannedMealRequest> {

    private final PlannedMealService plannedMealService;

    @GetMapping
    @Operation(summary = "Listar todas as refeições planejadas", description = "Retorna uma lista com todas as refeições planejadas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de refeições planejadas retornada com sucesso")
    @Override
    public ResponseEntity<List<PlannedMeal>> listarTodos() {
        return ResponseEntity.ok(plannedMealService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar refeição planejada por ID", description = "Retorna uma refeição planejada específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refeição planejada encontrada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Refeição planejada não encontrada")
    })
    @Override
    public ResponseEntity<PlannedMeal> buscarPorId(@PathVariable Long id) {
        Optional<PlannedMeal> plannedMeal = plannedMealService.buscarPorId(id);
        return plannedMeal.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/plano/{planoId}")
    @Operation(summary = "Buscar refeições planejadas por plano alimentar", description = "Retorna todas as refeições planejadas de um plano alimentar específico")
    @ApiResponse(responseCode = "200", description = "Refeições planejadas encontradas com sucesso")
    public ResponseEntity<List<PlannedMeal>> buscarPorPlanoAlimentar(@PathVariable Long planoId) {
        return ResponseEntity.ok(plannedMealService.buscarPorPlanoAlimentar(planoId));
    }

    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Buscar refeições planejadas por paciente", description = "Retorna todas as refeições planejadas de um paciente específico")
    @ApiResponse(responseCode = "200", description = "Refeições planejadas encontradas com sucesso")
    public ResponseEntity<List<PlannedMeal>> buscarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(plannedMealService.buscarPorPaciente(pacienteId));
    }

    @GetMapping("/paciente/{pacienteId}/data")
    @Operation(summary = "Buscar refeições planejadas por paciente e data", description = "Retorna as refeições planejadas de um paciente em uma data específica")
    @ApiResponse(responseCode = "200", description = "Refeições planejadas encontradas com sucesso")
    public ResponseEntity<List<PlannedMeal>> buscarPorPacienteEData(
            @PathVariable Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(plannedMealService.buscarPorPacienteEData(pacienteId, data));
    }

    @GetMapping("/paciente/{pacienteId}/periodo")
    @Operation(summary = "Buscar refeições planejadas por paciente e período", description = "Retorna as refeições planejadas de um paciente em um período específico")
    @ApiResponse(responseCode = "200", description = "Refeições planejadas encontradas com sucesso")
    public ResponseEntity<List<PlannedMeal>> buscarPorPacienteEPeriodo(
            @PathVariable Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(plannedMealService.buscarPorPacienteEPeriodo(pacienteId, dataInicio, dataFim));
    }

    @GetMapping("/tipo/{tipoRefeicao}")
    @Operation(summary = "Buscar refeições planejadas por tipo", description = "Retorna todas as refeições planejadas de um tipo específico")
    @ApiResponse(responseCode = "200", description = "Refeições planejadas encontradas com sucesso")
    public ResponseEntity<List<PlannedMeal>> buscarPorTipoRefeicao(@PathVariable String tipoRefeicao) {
        return ResponseEntity.ok(plannedMealService.buscarPorTipoRefeicao(tipoRefeicao));
    }

    /**
     * POST único — aceita planoId + refeicaoId + dataPlanejada + tipoRefeicao via DTO.
     * Substitui os antigos POST / (entidade raw) e POST /simples (IDs separados).
     */
    @PostMapping
    @Operation(summary = "Criar refeição planejada", description = "Cadastra uma nova refeição planejada informando o plano, a refeição, a data e o tipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Refeição planejada criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @Override
    public ResponseEntity<PlannedMeal> criar(@Valid @RequestBody CriarPlannedMealRequest request) {
        PlannedMeal novaPlannedMeal = plannedMealService.criarRefeicaoPlanejada(
            request.getPlanoId(), request.getRefeicaoId(),
            request.getDataPlanejada(), request.getTipoRefeicao());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPlannedMeal);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar refeição planejada", description = "Atualiza os dados de uma refeição planejada existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refeição planejada atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Refeição planejada não encontrada")
    })
    @Override
    public ResponseEntity<PlannedMeal> atualizar(@PathVariable Long id, @Valid @RequestBody CriarPlannedMealRequest request) {
        PlannedMeal atualizada = plannedMealService.atualizarPorRequest(id, request);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar refeição planejada", description = "Remove uma refeição planejada do sistema pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Refeição planejada deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Refeição planejada não encontrada")
    })
    @Override
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        plannedMealService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/plano/{planoId}")
    @Operation(summary = "Deletar refeições planejadas por plano alimentar", description = "Remove todas as refeições planejadas de um plano alimentar específico")
    @ApiResponse(responseCode = "204", description = "Refeições planejadas deletadas com sucesso")
    public ResponseEntity<Void> deletarPorPlanoAlimentar(@PathVariable Long planoId) {
        plannedMealService.deletarPorPlanoAlimentar(planoId);
        return ResponseEntity.noContent().build();
    }
}
