package com.albertsilva.dev.dscatalog.dto.product.request;

import java.time.Instant;
import java.util.List;

public record ProductUpdateRequest(
    String name,
    String description,
    Boolean active,
    Double price,
    String imgUrl,
    Instant date,
    List<Long> categoryIds) {
}
