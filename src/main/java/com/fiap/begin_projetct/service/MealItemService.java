package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.model.Food;
import com.fiap.begin_projetct.model.Meal;
import com.fiap.begin_projetct.model.MealItem;
import com.fiap.begin_projetct.repository.FoodRepository;
import com.fiap.begin_projetct.repository.MealItemRepository;
import com.fiap.begin_projetct.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealItemService {
    
    private final MealItemRepository mealItemRepository;
    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;
    private final NutritionService nutritionService;
    
    /**
     * Listar todos os itens de refeição
     */
    public List<MealItem> listarTodos() {
        return mealItemRepository.findAll();
    }
    
    /**
     * Buscar item de refeição por ID
     */
    public Optional<MealItem> buscarPorId(Long id) {
        return mealItemRepository.findById(id);
    }
    
    /**
     * Buscar itens por refeição
     */
    public List<MealItem> buscarPorRefeicao(Long mealId) {
        return mealItemRepository.findByMealIdOrderById(mealId);
    }
    
    /**
     * Buscar itens por alimento
     */
    public List<MealItem> buscarPorAlimento(Long foodId) {
        return mealItemRepository.findByFoodIdOrderById(foodId);
    }
    
    /**
     * Buscar item específico por refeição e alimento
     */
    public Optional<MealItem> buscarPorRefeicaoEAlimento(Long mealId, Long foodId) {
        return mealItemRepository.findByMealIdAndFoodId(mealId, foodId);
    }
    
    /**
     * Criar novo item de refeição
     */
    public MealItem salvar(MealItem mealItem) {
        // Validar se a refeição existe
        if (mealItem.getMeal() != null && mealItem.getMeal().getId() != null) {
            if (!mealRepository.existsById(mealItem.getMeal().getId())) {
                throw new IllegalArgumentException("Refeição não encontrada com ID: " + mealItem.getMeal().getId());
            }
        }
        
        // Validar se o alimento existe
        if (mealItem.getFood() != null && mealItem.getFood().getId() != null) {
            if (!foodRepository.existsById(mealItem.getFood().getId())) {
                throw new IllegalArgumentException("Alimento não encontrado com ID: " + mealItem.getFood().getId());
            }
        }
        
        // Validar quantidade
        nutritionService.validateQuantity(mealItem.getQuantity());
        
        // Validar nutrientes do alimento
        if (mealItem.getFood() != null) {
            nutritionService.validateFoodNutrients(mealItem.getFood());
        }
        
        return mealItemRepository.save(mealItem);
    }
    
    /**
     * Criar item de refeição com IDs
     */
    public MealItem criarItem(Long mealId, Long foodId, Double quantity) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada com ID: " + mealId));
        
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Alimento não encontrado com ID: " + foodId));
        
        // Validar quantidade
        nutritionService.validateQuantity(quantity);
        
        MealItem mealItem = new MealItem();
        mealItem.setMeal(meal);
        mealItem.setFood(food);
        mealItem.setQuantity(quantity);
        
        // Os nutrientes serão calculados automaticamente via @PrePersist
        return mealItemRepository.save(mealItem);
    }
    
    /**
     * Atualizar item de refeição por entidade (uso interno/legado).
     */
    public MealItem atualizar(Long id, MealItem mealItem) {
        return atualizarItem(id,
            mealItem.getMeal() != null ? mealItem.getMeal().getId() : null,
            mealItem.getFood() != null ? mealItem.getFood().getId() : null,
            mealItem.getQuantity());
    }

    /**
     * Atualizar item de refeição usando IDs (usado pelo controller refatorado com CriarMealItemRequest).
     */
    public MealItem atualizarItem(Long id, Long mealId, Long foodId, Double quantity) {
        MealItem itemAtualizado = mealItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item de refeição não encontrado com ID: " + id));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada com ID: " + mealId));

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Alimento não encontrado com ID: " + foodId));

        nutritionService.validateQuantity(quantity);

        itemAtualizado.setMeal(meal);
        itemAtualizado.setFood(food);
        itemAtualizado.setQuantity(quantity);

        // Os nutrientes serão recalculados automaticamente via @PreUpdate
        return mealItemRepository.save(itemAtualizado);
    }
    
    /**
     * Atualizar quantidade do item
     */
    public MealItem atualizarQuantidade(Long id, Double novaQuantidade) {
        Optional<MealItem> itemExistente = mealItemRepository.findById(id);
        if (itemExistente.isEmpty()) {
            throw new IllegalArgumentException("Item de refeição não encontrado com ID: " + id);
        }
        
        // Validar quantidade
        nutritionService.validateQuantity(novaQuantidade);
        
        MealItem itemAtualizado = itemExistente.get();
        itemAtualizado.setQuantity(novaQuantidade);
        
        // Os nutrientes serão recalculados automaticamente via @PreUpdate
        return mealItemRepository.save(itemAtualizado);
    }
    
    /**
     * Deletar item de refeição
     */
    public void deletar(Long id) {
        if (!mealItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item de refeição não encontrado com ID: " + id);
        }
        mealItemRepository.deleteById(id);
    }
    
    /**
     * Deletar todos os itens de uma refeição
     */
    public void deletarPorRefeicao(Long mealId) {
        if (!mealRepository.existsById(mealId)) {
            throw new IllegalArgumentException("Refeição não encontrada com ID: " + mealId);
        }
        List<MealItem> itens = mealItemRepository.findByMealId(mealId);
        mealItemRepository.deleteAll(itens);
    }
    
    /**
     * Contar itens por refeição
     */
    public Long contarPorRefeicao(Long mealId) {
        return mealItemRepository.countByMealId(mealId);
    }
    
    /**
     * Contar itens por alimento
     */
    public Long contarPorAlimento(Long foodId) {
        return mealItemRepository.countByFoodId(foodId);
    }
}
