package com.fiap.begin_projetct.controller;

import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface genérica que define o contrato CRUD básico para todos os controllers REST.
 *
 * @param <T>   Tipo da entidade ou DTO de resposta
 * @param <ID>  Tipo do identificador (normalmente Long)
 * @param <CR>  DTO de criação (Create Request)
 * @param <UR>  DTO de atualização (Update Request)
 */
public interface CrudController<T, ID, CR, UR> {

    /**
     * Lista todos os registros.
     * GET /
     */
    ResponseEntity<List<T>> listarTodos();

    /**
     * Busca um registro pelo seu ID.
     * GET /{id}
     */
    ResponseEntity<T> buscarPorId(ID id);

    /**
     * Cria um novo registro.
     * POST /
     */
    ResponseEntity<T> criar(CR request);

    /**
     * Atualiza um registro existente pelo ID.
     * PUT /{id}
     */
    ResponseEntity<T> atualizar(ID id, UR request);

    /**
     * Remove um registro pelo ID.
     * DELETE /{id}
     */
    ResponseEntity<Void> deletar(ID id);
}
