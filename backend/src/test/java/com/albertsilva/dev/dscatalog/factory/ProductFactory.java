package com.albertsilva.dev.dscatalog.factory;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.albertsilva.dev.dscatalog.dto.product.response.ProductDetailsResponse;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductResponse;
import com.albertsilva.dev.dscatalog.entities.Product;

public class ProductFactory {

  public static Product createProduct() {
    return new Product("Smart TV",
        "Smart TV com alta resolução, acesso a streaming e conectividade Wi-Fi.", 2190.0,
        "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg",
        Instant.parse("2023-01-01T00:00:00Z"), true);
  }

  public static ProductResponse createProductResponse() {
    return new ProductResponse(1L, "Smart TV", "Smart TV com alta resolução, acesso a streaming e conectividade Wi-Fi.",
        2190.0, "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg",
        Instant.parse("2023-01-01T00:00:00Z"), List.of());
  }

  public static ProductDetailsResponse createProductDetailsResponse() {
    return new ProductDetailsResponse(1L, "Smart TV", "TV 50 polegadas", 2500.0,
        "https://img.com/tv.png", Instant.parse("2026-01-10T10:00:00Z"), List.of());
  }

}
