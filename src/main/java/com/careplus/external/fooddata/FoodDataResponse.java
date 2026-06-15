package com.careplus.external.fooddata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(value = {"totalHits", "currentPage", "totalPages", "pageList", "foodSearchCriteria"})
public class FoodDataResponse {
    private List<FoodDataItem> foods;
}
