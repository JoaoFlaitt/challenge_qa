package com.careplus.external.fooddata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodDataItem {
    private Long fdcId;
    private String description;
    private List<NutrientDTO> foodNutrients;
}
