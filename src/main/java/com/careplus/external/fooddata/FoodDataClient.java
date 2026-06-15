package com.careplus.external.fooddata;

import com.fiap.begin_projetct.model.Food;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FoodDataClient {

    private final WebClient webClient;
    private final String apiKey;
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1";

    public FoodDataClient(WebClient.Builder webClientBuilder, @Value("${fooddata.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
        this.apiKey = apiKey;
    }

    public List<Food> searchFoods(String query) {
        log.info("Searching foods in FoodData Central for query: {}", query);
        
        try {
            FoodDataResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/foods/search")
                            .queryParam("query", query)
                            .queryParam("pageSize", 10)
                            .queryParam("api_key", apiKey)
                            .build())
                    .header("User-Agent", "CarePlus/1.0")
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(FoodDataResponse.class)
                    .block();

            if (response == null || response.getFoods() == null) {
                return new ArrayList<>();
            }

            return response.getFoods().stream()
                    .map(this::mapToFoodEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Error calling FoodData Central API: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private Food mapToFoodEntity(FoodDataItem item) {
        Food food = new Food();
        food.setName(item.getDescription());
        food.setFdcId(item.getFdcId());
        food.setSource("API");
        
        // Default values
        food.setCaloriesPer100g(0);
        food.setProteins(0.0);
        food.setCarbs(0.0);
        food.setFats(0.0);
        food.setFiber(0.0);
        food.setSodium(0.0);
        food.setSugar(0.0);
        food.setServingSize(100.0);
        food.setServingUnit("g");

        if (item.getFoodNutrients() != null) {
            for (NutrientDTO nutrient : item.getFoodNutrients()) {
                switch (nutrient.getNutrientName()) {
                    case "Energy":
                        food.setCaloriesPer100g(nutrient.getValue().intValue());
                        break;
                    case "Protein":
                        food.setProteins(nutrient.getValue());
                        break;
                    case "Carbohydrate, by difference":
                        food.setCarbs(nutrient.getValue());
                        break;
                    case "Total lipid (fat)":
                        food.setFats(nutrient.getValue());
                        break;
                    case "Fiber, total dietary":
                        food.setFiber(nutrient.getValue());
                        break;
                    case "Sodium, Na":
                        food.setSodium(nutrient.getValue());
                        break;
                    case "Sugars, total including NLEA":
                        food.setSugar(nutrient.getValue());
                        break;
                }
            }
        }
        
        return food;
    }
}
