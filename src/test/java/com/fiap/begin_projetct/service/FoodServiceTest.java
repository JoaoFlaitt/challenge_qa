package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.model.Food;
import com.fiap.begin_projetct.repository.FoodRepository;
import com.careplus.external.fooddata.FoodDataClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private NutritionService nutritionService;

    @Mock
    private FoodDataClient foodDataClient;

    @InjectMocks
    private FoodService foodService;

    private Food foodLocal;
    private Food foodApi;

    @BeforeEach
    void setUp() {
        foodLocal = new Food();
        foodLocal.setId(1L);
        foodLocal.setName("Maçã");
        foodLocal.setCaloriesPer100g(52);
        foodLocal.setProteins(0.3);
        foodLocal.setCarbs(14.0);
        foodLocal.setFats(0.2);
        foodLocal.setFdcId(111L);
        foodLocal.setSource("LOCAL");

        foodApi = new Food();
        foodApi.setId(2L);
        foodApi.setName("Maçã Verde");
        foodApi.setCaloriesPer100g(48);
        foodApi.setProteins(0.4);
        foodApi.setCarbs(12.0);
        foodApi.setFats(0.1);
        foodApi.setFdcId(222L);
        foodApi.setSource("API");
    }

    @Test
    void deveSalvarAlimentoValido() {
        when(foodRepository.findByFdcId(111L)).thenReturn(Optional.empty());
        when(foodRepository.save(any(Food.class))).thenReturn(foodLocal);

        Food result = foodService.salvar(foodLocal);

        assertNotNull(result);
        assertEquals("Maçã", result.getName());
        verify(nutritionService, times(1)).validateFoodNutrients(foodLocal);
        verify(foodRepository, times(1)).save(foodLocal);
    }

    @Test
    void deveBuscarPorNomeContainingSemChamarAPIExternaSeExistiremResultadosSuficientes() {
        List<Food> localResults = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            localResults.add(foodLocal);
        }

        when(foodRepository.findByNameContainingIgnoreCase("Maçã")).thenReturn(localResults);

        List<Food> result = foodService.buscarPorNomeContaining("Maçã");

        assertNotNull(result);
        assertEquals(5, result.size());
        verifyNoInteractions(foodDataClient);
    }

    @Test
    void deveBuscarPorNomeContainingChamandoAPIExternaSeResultadosLocaisForemInsuficientes() {
        List<Food> localResults = List.of(foodLocal);
        List<Food> apiResults = List.of(foodApi);

        when(foodRepository.findByNameContainingIgnoreCase("Maçã")).thenReturn(localResults);
        when(foodDataClient.searchFoods("Maçã")).thenReturn(apiResults);
        when(foodRepository.existsByFdcId(222L)).thenReturn(false);
        when(foodRepository.save(foodApi)).thenReturn(foodApi);

        List<Food> result = foodService.buscarPorNomeContaining("Maçã");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(foodDataClient, times(1)).searchFoods("Maçã");
        verify(foodRepository, times(1)).save(foodApi);
    }

    @Test
    void deveLancarExcecaoAoSalvarNomeDuplicado() {
        foodLocal.setFdcId(null);
        when(foodRepository.existsByName(foodLocal.getName())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> foodService.salvar(foodLocal));
        verify(foodRepository, never()).save(any(Food.class));
    }
}
