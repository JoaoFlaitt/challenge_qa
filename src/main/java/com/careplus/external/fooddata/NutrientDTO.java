package com.careplus.external.fooddata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NutrientDTO {
    private Integer nutrientId;
    private String nutrientName;
    private Double value;
    private String unitName;
}
