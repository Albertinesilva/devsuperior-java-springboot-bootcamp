package com.albertsilva.dev.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.albertsilva.dev.dscatalog.entities.Product;
import com.albertsilva.dev.dscatalog.factory.ProductFactory;
import com.albertsilva.dev.dscatalog.repositories.ProductRepository;
import com.albertsilva.dev.dscatalog.services.exceptions.DatabaseException;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

@DisplayName("Tests for ProductService")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

  @InjectMocks
  private ProductService service;

  @Mock
  private ProductRepository repository;

  private Long existingId;
  private Long nonExistingId;
  private Long dependentId;

  @BeforeEach
  void setUp() throws Exception {
    existingId = 1L;
    nonExistingId = 1000L;
    dependentId = 4L;
  }

  @Test
  @DisplayName("Delete should remove product when id exists")
  void deleteShouldRemoveProductWhenIdExists() {

    // Arrange
    Product product = ProductFactory.createProduct();
    product.setId(existingId);

    Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

    // Act & Assert
    Assertions.assertDoesNotThrow(() -> {
      service.delete(existingId);
    });

    // Assert (Behavior Verification
    Mockito.verify(repository).delete(product);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when id does not exist")
  void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Arrange
    Mockito.when(repository.findById(nonExistingId))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> {
      service.delete(nonExistingId);
    });

    // Assert (Behavior Verification
    Mockito.verify(repository, Mockito.never()).delete(Mockito.any());
  }

  @Test
  @DisplayName("Should throw DatabaseException when integrity violation occurs")
  void deleteShouldThrowDatabaseExceptionWhenDependentId() {

    // Arrange
    Product product = ProductFactory.createProduct();
    product.setId(dependentId);

    Mockito.when(repository.findById(dependentId)).thenReturn(Optional.of(product));

    Mockito.doThrow(DataIntegrityViolationException.class).when(repository).delete(product);

    // Act & Assert
    assertThrows(DatabaseException.class, () -> {
      service.delete(dependentId);
    });

    // Assert (Behavior Verification
    Mockito.verify(repository).delete(product);
  }
}
