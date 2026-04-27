package com.albertsilva.dev.dscatalog.factory;

import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.entities.Category;

public class CategoryFactory {

   public static final Long EXISTING_ID = 1L;
  public static final Long NON_EXISTING_ID = 1000L;


  public static Category createCategory() {
    Category category = new Category("Eletrônicos", "Produtos eletrônicos, como TVs, smartphones e laptops.", true);
    return category;
  }

  public static CategoryResponse createCategoryResponse() {
    return new CategoryResponse(EXISTING_ID, "Eletrônicos", "Produtos eletrônicos, como TVs, smartphones e laptops.", true);
  }

  public static CategoryCreateRequest createCategoryCreateRequest() {
    return new CategoryCreateRequest("Eletrodomésticos", "Produtos eletrodomésticos, como geladeiras, fogões e máquinas de lavar.", true);
  }
}
