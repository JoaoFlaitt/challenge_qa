package com.fiap.begin_projetct.controller;

import com.fiap.begin_projetct.model.Meal;
import com.fiap.begin_projetct.service.MealService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Tag(name = "Refeições", description = "API para gerenciamento de refeições")
public class MealController implements CrudController<Meal, Long, CreateMealRequest, CreateMealRequest> {
    
    private final MealService mealService;
    
    @GetMapping
    @Operation(summary = "Listar todas as refeições", description = "Retorna uma lista com todas as refeições cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de refeições retornada com sucesso")
    public ResponseEntity<List<Meal>> listarTodos() {
        List<Meal> meals = mealService.listarTodos();
        return ResponseEntity.ok(meals);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar refeição por ID", description = "Retorna uma refeição específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refeição encontrada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Refeição não encontrada")
    })
    public ResponseEntity<Meal> buscarPorId(@PathVariable Long id) {
        Optional<Meal> meal = mealService.buscarPorId(id);
        return meal.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Buscar refeições por paciente", description = "Retorna todas as refeições de um paciente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refeições encontradas com sucesso")
    })
    public ResponseEntity<List<Meal>> buscarPorPaciente(@PathVariable Long pacienteId) {
        List<Meal> meals = mealService.getMealsByPatient(pacienteId);
        return ResponseEntity.ok(meals);
    }
    
    @GetMapping("/paciente/{pacienteId}/periodo")
    @Operation(summary = "Buscar refeições por paciente e período", description = "Retorna as refeições de um paciente em um período específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refeições encontradas com sucesso")
    })
    public ResponseEntity<List<Meal>> buscarPorPacienteEPeriodo(
            @PathVariable Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        List<Meal> meals = mealService.getMealsByPatientAndPeriod(pacienteId, dataInicio, dataFim);
        return ResponseEntity.ok(meals);
    }
    
    @GetMapping("/paciente/{pacienteId}/presets")
    @Operation(summary = "Buscar presets de refeições por paciente", description = "Retorna todos os presets de refeições de um paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Presets encontrados com sucesso")
    })
    public ResponseEntity<List<Meal>> buscarPresetsPorPaciente(@PathVariable Long pacienteId) {
        List<Meal> presets = mealService.getPresetsByPatient(pacienteId);
        return ResponseEntity.ok(presets);
    }
    
    @PostMapping("/paciente/{pacienteId}/custom")
    @Operation(summary = "Criar refeição customizada", description = "Cria uma nova refeição customizada para um paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Refeição criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Meal> criarRefeicaoCustomizada(
            @PathVariable Long pacienteId,
            @Valid @RequestBody CreateMealRequest request) {
        Meal meal = mealService.createCustomMeal(pacienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }
    
    @PostMapping("/paciente/{pacienteId}/preset/{presetName}")
    @Operation(summary = "Criar preset de refeição", description = "Cria um novo preset de refeição para um paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Preset criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou preset já existe")
    })
    public ResponseEntity<Meal> criarPresetRefeicao(
            @PathVariable Long pacienteId,
            @PathVariable String presetName,
            @Valid @RequestBody CreateMealRequest request) {
        Meal meal = mealService.createPresetMeal(pacienteId, request, presetName);
        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }
    // Implementa CrudController — criação genérica; prefira os endpoints com contexto de paciente.
    @Override
    public ResponseEntity<Meal> criar(CreateMealRequest request) {
        throw new UnsupportedOperationException("Use POST /api/meals/paciente/{id}/custom ou /preset/{name}");
    }

    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar refeição", description = "Atualiza os dados de uma refeição existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refeição atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Refeição não encontrada")
    })
    @Override
    public ResponseEntity<Meal> atualizar(@PathVariable Long id, @Valid @RequestBody CreateMealRequest request) {
        Meal mealAtualizado = mealService.atualizar(id, request.toEntity());
        return ResponseEntity.ok(mealAtualizado);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar refeição", description = "Remove uma refeição do sistema pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Refeição deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Refeição não encontrada")
    })
    @Override
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        mealService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
