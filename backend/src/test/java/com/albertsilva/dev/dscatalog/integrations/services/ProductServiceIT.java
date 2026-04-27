package com.albertsilva.dev.dscatalog.integrations.services;

import static com.albertsilva.dev.dscatalog.factory.ProductFactory.COUNT_TOTAL_PRODUCTS;
import static com.albertsilva.dev.dscatalog.factory.ProductFactory.EXISTING_ID;
import static com.albertsilva.dev.dscatalog.factory.ProductFactory.NON_EXISTING_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.albertsilva.dev.dscatalog.dto.product.request.ProductCreateRequest;
import com.albertsilva.dev.dscatalog.dto.product.request.ProductUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductDetailsResponse;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductResponse;
import com.albertsilva.dev.dscatalog.factory.ProductFactory;
import com.albertsilva.dev.dscatalog.repositories.ProductRepository;
import com.albertsilva.dev.dscatalog.services.ProductService;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@DisplayName("ProductService Integration Tests")
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

  @Test
  @DisplayName("findAllPaged should return paged products when page 0 size 10")
  void findAllPagedShouldReturnPagedProductsWhenPage0Size10() {

    // Arrange
    PageRequest pageRequest = PageRequest.of(0, 10);

    // Act
    Page<ProductResponse> result = service.findAllPaged(pageRequest);

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(0, result.getNumber());
    assertEquals(10, result.getSize());
    assertEquals(COUNT_TOTAL_PRODUCTS, result.getTotalElements());
  }

  @Test
  @DisplayName("findAllPaged should return empty page when page does not exist")
  void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {

    // Arrange
    PageRequest pageRequest = PageRequest.of(50, 10);

    // Act
    Page<ProductResponse> result = service.findAllPaged(pageRequest);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    assertEquals(50, result.getNumber());
    assertEquals(10, result.getSize());
    assertEquals(COUNT_TOTAL_PRODUCTS, result.getTotalElements());
  }

  @Test
  @DisplayName("findAllPaged should return ordered page when sorting by name")
  void findAllPagedShouldReturnOrderedPageWhenSortingByName() {

    // Arrange
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

    // Act
    Page<ProductResponse> result = service.findAllPaged(pageRequest);

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals("Macbook Pro", result.getContent().get(0).name());
    assertEquals("PC Gamer", result.getContent().get(1).name());
    assertEquals("PC Gamer Alfa", result.getContent().get(2).name());
  }

  @Test
  @DisplayName("findById should return product details when id exists")
  void findByIdShouldReturnProductDetailsWhenIdExists() {

    // Act
    ProductDetailsResponse result = service.findById(EXISTING_ID);

    // Assert
    assertNotNull(result);
    assertEquals(EXISTING_ID, result.id());
  }

  @Test
  @DisplayName("findById should throw ResourceNotFoundException when id does not exist")
  void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Act + Assert
    assertThrows(ResourceNotFoundException.class, () -> service.findById(NON_EXISTING_ID));
  }

  @Test
  @DisplayName("insert should persist product when valid data")
  void insertShouldPersistProductWhenValidData() {

    // Arrange
    ProductCreateRequest request = ProductFactory.createProductCreateRequest();

    // Act
    ProductResponse result = service.insert(request);

    // Assert
    assertNotNull(result);
    assertNotNull(result.id());
    assertEquals(COUNT_TOTAL_PRODUCTS + 1, repository.count());
  }

  @Test
  @DisplayName("update should update product when id exists")
  void updateShouldUpdateProductWhenIdExists() {

    // Arrange
    ProductUpdateRequest request = ProductFactory.createProductUpdateRequest();

    // Act
    ProductResponse result = service.update(EXISTING_ID, request);

    // Assert
    assertNotNull(result);
    assertEquals(EXISTING_ID, result.id());
    assertEquals(request.name(), result.name());
  }

  @Test
  @DisplayName("update should throw ResourceNotFoundException when id does not exist")
  void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Arrange
    ProductUpdateRequest request = ProductFactory.createProductUpdateRequest();

    // Act + Assert
    assertThrows(ResourceNotFoundException.class, () -> service.update(NON_EXISTING_ID, request));
  }

}
