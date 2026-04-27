package com.albertsilva.dev.dscatalog.integrations.services;

import static com.albertsilva.dev.dscatalog.factory.ProductFactory.COUNT_TOTAL_PRODUCTS;
import static com.albertsilva.dev.dscatalog.factory.ProductFactory.EXISTING_ID;
import static com.albertsilva.dev.dscatalog.factory.ProductFactory.NON_EXISTING_ID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.albertsilva.dev.dscatalog.repositories.ProductRepository;
import com.albertsilva.dev.dscatalog.services.ProductService;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
public class ProductServiceIT {

  @Autowired
  private ProductService service;

  @Autowired
  private ProductRepository repository;

  @Test
  @DisplayName("delete should remove product when id exists")
  void deleteShouldRemoveProductWhenIdExists() {

    // Act
    service.delete(EXISTING_ID);

    // Assert (state)
    Assertions.assertEquals(COUNT_TOTAL_PRODUCTS - 1, repository.count());
    assertFalse(repository.existsById(EXISTING_ID));
  }

  @Test
  @DisplayName("delete should throw ResourceNotFoundException when id does not exist")
  void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Act + Assert
    assertThrows(ResourceNotFoundException.class, () -> service.delete(NON_EXISTING_ID));
  }
}
