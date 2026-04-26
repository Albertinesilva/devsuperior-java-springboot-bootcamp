package com.albertsilva.dev.dscatalog.factory;

import com.albertsilva.dev.dscatalog.entities.Category;

public class CategoryFactory {

  public static Category createCategory() {
    Category category = new Category( 1L, "Eletrônicos", "Produtos eletrônicos, como TVs, smartphones e laptops.", true);
    return category;
  }
}
