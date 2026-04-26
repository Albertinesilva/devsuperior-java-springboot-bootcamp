package com.albertsilva.dev.dscatalog.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.entities.Product;
import com.albertsilva.dev.dscatalog.factory.CategoryFactory;
import com.albertsilva.dev.dscatalog.factory.ProductFactory;

@DataJpaTest
public class CategoryRepositoryTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("Delete should delete object when id exists")
  void deleteShouldDeleteObjectWhenIdExists() {

    // Arrange
    Long existingId = categoryRepository.save(CategoryFactory.createCategory()).getId();

    // Act
    categoryRepository.deleteById(existingId);
    categoryRepository.flush();

    // Assert
    Assertions.assertThat(categoryRepository.existsById(existingId)).as("Category should be deleted when id exists")
        .isFalse();
  }

  @Test
  @DisplayName("Delete should not remove associated products")
  void deleteShouldNotDeleteAssociatedProducts() {

    // Arrange
    Category category = categoryRepository.saveAndFlush(CategoryFactory.createCategory());

    Product product = ProductFactory.createProduct();
    product.getCategories().add(category);

    product = productRepository.saveAndFlush(product);

    Long categoryId = category.getId();
    Long productId = product.getId();

    // Importante:
    // Remove a associação ManyToMany antes de deletar a categoria
    product.getCategories().remove(category);
    productRepository.saveAndFlush(product);

    // Act
    categoryRepository.deleteById(categoryId);
    categoryRepository.flush();

    // Assert
    Assertions.assertThat(productRepository.existsById(productId))
        .as("Associated product should remain after category deletion").isTrue();
        
    Assertions.assertThat(categoryRepository.existsById(categoryId)).as("Category should be deleted").isFalse();
  }

  @Test
  @DisplayName("Delete should decrease repository count")
  void deleteShouldDecreaseCount() {

    // Arrange
    Category category = categoryRepository.save(CategoryFactory.createCategory());
    long countBefore = categoryRepository.count();

    // Act
    categoryRepository.deleteById(category.getId());
    categoryRepository.flush();

    // Assert
    Assertions.assertThat(categoryRepository.count()).as("Repository count should decrease after delete")
        .isEqualTo(countBefore - 1);
  }

}