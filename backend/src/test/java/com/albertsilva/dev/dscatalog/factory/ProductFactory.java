package com.albertsilva.dev.dscatalog.factory;

import java.time.Instant;

import com.albertsilva.dev.dscatalog.entities.Product;

public class ProductFactory {

  public static Product createProduct() {
    Product product = new Product("Smart TV",
        "Smart TV com alta resolução, acesso a streaming e conectividade Wi-Fi.", 2190.0,
        "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg",
        Instant.parse("2023-01-01T00:00:00Z"),
        true);
    return product;
  }

}
