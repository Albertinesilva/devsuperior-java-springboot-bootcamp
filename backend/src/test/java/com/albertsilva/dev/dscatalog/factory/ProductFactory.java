package com.albertsilva.dev.dscatalog.factory;

import com.albertsilva.dev.dscatalog.entities.Product;

public class ProductFactory {

  public static Product createProduct() {
    Product product = new Product();
    product.setName("The Lord of the Rings");
    product.setDescription("Lorem ipsum dolor sit amet, consectetur.");
    product.setPrice(90.5);
    product.setImgUrl("https://img.com/img.png");
    product.setActive(false);
    return product;
  }
  
}
