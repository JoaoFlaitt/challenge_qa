package com.fiap.begin_projetct.controller;

import com.fiap.begin_projetct.dto.CriarMealItemRequest;
import com.fiap.begin_projetct.model.MealItem;
import com.fiap.begin_projetct.service.MealItemService;
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
@RequestMapping("/api/meal-items")
@RequiredArgsConstructor
@Tag(name = "Itens de Refeição", description = "API para gerenciamento de itens de refeição")
public class MealItemController implements CrudController<MealItem, Long, CriarMealItemRequest, CriarMealItemRequest> {

    private final MealItemService mealItemService;

    @GetMapping
    @Operation(summary = "Listar todos os itens de refeição", description = "Retorna uma lista com todos os itens de refeição cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de itens retornada com sucesso")
    @Override
    public ResponseEntity<List<MealItem>> listarTodos() {
        return ResponseEntity.ok(mealItemService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item por ID", description = "Retorna um item específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @Override
    public ResponseEntity<MealItem> buscarPorId(@PathVariable Long id) {
        Optional<MealItem> item = mealItemService.buscarPorId(id);
        return item.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/refeicao/{refeicaoId}")
    @Operation(summary = "Buscar itens por refeição", description = "Retorna todos os itens de uma refeição específica")
    @ApiResponse(responseCode = "200", description = "Itens encontrados com sucesso")
    public ResponseEntity<List<MealItem>> buscarPorRefeicao(@PathVariable Long refeicaoId) {
        return ResponseEntity.ok(mealItemService.buscarPorRefeicao(refeicaoId));
    }

    @GetMapping("/alimento/{alimentoId}")
    @Operation(summary = "Buscar itens por alimento", description = "Retorna todos os itens que contêm um alimento específico")
    @ApiResponse(responseCode = "200", description = "Itens encontrados com sucesso")
    public ResponseEntity<List<MealItem>> buscarPorAlimento(@PathVariable Long alimentoId) {
        return ResponseEntity.ok(mealItemService.buscarPorAlimento(alimentoId));
    }

    @GetMapping("/refeicao/{refeicaoId}/alimento/{alimentoId}")
    @Operation(summary = "Buscar item específico", description = "Retorna um item específico por refeição e alimento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<MealItem> buscarPorRefeicaoEAlimento(
            @PathVariable Long refeicaoId,
            @PathVariable Long alimentoId) {
        Optional<MealItem> item = mealItemService.buscarPorRefeicaoEAlimento(refeicaoId, alimentoId);
        return item.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST único — aceita refeicaoId + alimentoId + quantidade via CriarMealItemRequest.
     * Substitui os antigos POST / (entidade raw) e POST /simples (IDs separados).
     */
    @PostMapping
    @Operation(summary = "Criar item de refeição", description = "Cadastra um novo item de refeição informando os IDs da refeição e do alimento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @Override
    public ResponseEntity<MealItem> criar(@Valid @RequestBody CriarMealItemRequest request) {
        MealItem novoItem = mealItemService.criarItem(
            request.getRefeicaoId(), request.getAlimentoId(), request.getQuantidade());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item de refeição", description = "Atualiza os dados de um item de refeição existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @Override
    public ResponseEntity<MealItem> atualizar(@PathVariable Long id, @Valid @RequestBody CriarMealItemRequest request) {
        MealItem itemAtualizado = mealItemService.atualizarItem(id, request.getRefeicaoId(), request.getAlimentoId(), request.getQuantidade());
        return ResponseEntity.ok(itemAtualizado);
    }

    @PutMapping("/{id}/quantidade")
    @Operation(summary = "Atualizar quantidade do item", description = "Atualiza apenas a quantidade de um item de refeição")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<MealItem> atualizarQuantidade(
            @PathVariable Long id,
            @RequestParam Double quantidade) {
        return ResponseEntity.ok(mealItemService.atualizarQuantidade(id, quantidade));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar item de refeição", description = "Remove um item de refeição do sistema pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Item deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @Override
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        mealItemService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/refeicao/{refeicaoId}")
    @Operation(summary = "Deletar todos os itens de uma refeição", description = "Remove todos os itens de uma refeição específica")
    @ApiResponse(responseCode = "204", description = "Itens deletados com sucesso")
    public ResponseEntity<Void> deletarPorRefeicao(@PathVariable Long refeicaoId) {
        mealItemService.deletarPorRefeicao(refeicaoId);
        return ResponseEntity.noContent().build();
    }
}
