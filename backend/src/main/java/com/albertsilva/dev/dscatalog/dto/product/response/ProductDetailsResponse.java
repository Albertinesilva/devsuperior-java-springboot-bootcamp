package com.albertsilva.dev.dscatalog.dto.product.response;

import java.time.Instant;
import java.util.List;

import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;

public record ProductDetailsResponse(
    Long id,
    String name,
    String description,
    Double price,
    String imgUrl,
    Instant date,
    List<CategoryResponse> categories) {
}
