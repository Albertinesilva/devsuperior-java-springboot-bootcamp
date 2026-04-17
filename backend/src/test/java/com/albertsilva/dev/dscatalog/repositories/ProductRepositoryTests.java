package com.albertsilva.dev.dscatalog.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.albertsilva.dev.dscatalog.factory.ProductFactory;

@DataJpaTest
public class ProductRepositoryTests {

  @Autowired
  private ProductRepository repository;

  @Test
  @DisplayName("Delete should delete object when id exists")
  public void deleteShouldDeleteObjectWhenIdExists() {
    // Arrange
    Long existingId = repository.save(ProductFactory.createProduct()).getId();

    // Act
    repository.deleteById(existingId);
    repository.flush();

    // Assert
    Assertions.assertThat(repository.existsById(existingId)).as("Product should be deleted when id exists").isFalse();

  }
}
