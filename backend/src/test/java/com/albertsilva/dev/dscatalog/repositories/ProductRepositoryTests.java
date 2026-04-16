package com.albertsilva.dev.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.albertsilva.dev.dscatalog.entities.Product;

@DataJpaTest
public class ProductRepositoryTests {

  @Autowired
  private ProductRepository repository;

  @Test
  public void deleteShouldDeleteObjectWhenIdExists() {

    // Arrange
    Long existingId = 1L;

    // Act
    repository.deleteById(existingId);
    repository.flush();

    // Assert
    Assertions.assertFalse(repository.existsById(existingId));

    // Optional<Product> result = repository.findById(existingId);
    // Assertions.assertFalse(result.isPresent());
  }
}
