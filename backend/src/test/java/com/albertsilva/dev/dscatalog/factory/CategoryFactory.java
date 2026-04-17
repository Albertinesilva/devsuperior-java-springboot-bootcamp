package com.albertsilva.dev.dscatalog.factory;

import com.albertsilva.dev.dscatalog.entities.Category;

public class CategoryFactory {

  public static Category createCategory() {
    Category category = new Category();
    category.setName("Books");
    category.setDescription("Lorem ipsum dolor sit amet, consectetur.");
    category.setActive(true);
    return category;
  }
}
