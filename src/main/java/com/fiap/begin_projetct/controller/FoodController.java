package com.fiap.begin_projetct.controller;

import com.fiap.begin_projetct.model.Food;
import com.fiap.begin_projetct.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Tag(name = "Alimentos", description = "API para gerenciamento de alimentos")
public class FoodController implements CrudController<com.fiap.begin_projetct.model.Food, Long, com.fiap.begin_projetct.dto.FoodRequest, com.fiap.begin_projetct.dto.FoodRequest> {
    
    private final FoodService foodService;
    
    @GetMapping
    @Operation(summary = "Listar todos os alimentos", description = "Retorna uma lista com todos os alimentos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de alimentos retornada com sucesso")
    @Override
    public ResponseEntity<List<com.fiap.begin_projetct.model.Food>> listarTodos() {
        List<com.fiap.begin_projetct.model.Food> foods = foodService.listarTodos();
        return ResponseEntity.ok(foods);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar alimento por ID", description = "Retorna um alimento específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alimento encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Alimento não encontrado")
    })
    @Override
    public ResponseEntity<com.fiap.begin_projetct.model.Food> buscarPorId(@PathVariable Long id) {
        Optional<com.fiap.begin_projetct.model.Food> food = foodService.buscarPorId(id);
        return food.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar alimento por nome", description = "Retorna um alimento específico pelo seu nome")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alimento encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Alimento não encontrado")
    })
    public ResponseEntity<com.fiap.begin_projetct.model.Food> buscarPorNome(@PathVariable String nome) {
        Optional<com.fiap.begin_projetct.model.Food> food = foodService.buscarPorNome(nome);
        return food.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar alimentos por parte do nome", description = "Retorna alimentos que contenham o texto informado no nome")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<List<com.fiap.begin_projetct.model.Food>> buscarPorNomeContaining(@RequestParam String nome) {
        List<com.fiap.begin_projetct.model.Food> foods = foodService.buscarPorNomeContaining(nome);
        return ResponseEntity.ok(foods);
    }
    
    @PostMapping
    @Operation(summary = "Criar novo alimento", description = "Cadastra um novo alimento no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Alimento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou alimento já existe")
    })
    @Override
    public ResponseEntity<com.fiap.begin_projetct.model.Food> criar(@Valid @RequestBody com.fiap.begin_projetct.dto.FoodRequest request) {
        com.fiap.begin_projetct.model.Food novoFood = foodService.salvar(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoFood);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar alimento", description = "Atualiza os dados de um alimento existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alimento atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Alimento não encontrado")
    })
    @Override
    public ResponseEntity<com.fiap.begin_projetct.model.Food> atualizar(@PathVariable Long id, @Valid @RequestBody com.fiap.begin_projetct.dto.FoodRequest request) {
        com.fiap.begin_projetct.model.Food foodAtualizado = foodService.atualizar(id, request.toEntity());
        return ResponseEntity.ok(foodAtualizado);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar alimento", description = "Remove um alimento do sistema pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Alimento deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Alimento não encontrado")
    })
    @Override
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        foodService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
