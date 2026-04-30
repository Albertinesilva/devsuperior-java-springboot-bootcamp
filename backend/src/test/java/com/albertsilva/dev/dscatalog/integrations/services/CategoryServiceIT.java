package com.albertsilva.dev.dscatalog.integrations.services;

import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.COUNT_TOTAL_CATEGORIES;
import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.EXISTING_ID;
import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.NON_EXISTING_ID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.factory.CategoryFactory;
import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;
import com.albertsilva.dev.dscatalog.services.CategoryService;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
@DisplayName("CategoryService Integration Tests")
public class CategoryServiceIT {

  @Autowired
  private CategoryService service;

  @Autowired
  private CategoryRepository repository;

  @Test
  @DisplayName("findAllPaged should return paged categories when page 0 size 10")
  void findAllPagedShouldReturnPagedCategoriesWhenPage0Size10() {

    // Arrange
    PageRequest pageRequest = PageRequest.of(0, 10);

    // Act
    Page<CategoryResponse> result = service.findAllPaged(pageRequest);

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(0, result.getNumber());
    assertEquals(10, result.getSize());
    assertEquals(COUNT_TOTAL_CATEGORIES, result.getTotalElements());
  }

  @Test
  @DisplayName("findAllPaged should return empty page when page does not exist")
  void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {

    // Arrange
    PageRequest pageRequest = PageRequest.of(50, 10);

    // Act
    Page<CategoryResponse> result = service.findAllPaged(pageRequest);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    assertEquals(50, result.getNumber());
    assertEquals(10, result.getSize());
    assertEquals(COUNT_TOTAL_CATEGORIES, result.getTotalElements());
  }

  @Test
  @DisplayName("findAllPaged should return ordered page when sorting by name")
  void findAllPagedShouldReturnOrderedPageWhenSortingByName() {

    // Arrange
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

    // Act
    Page<CategoryResponse> result = service.findAllPaged(pageRequest);

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());

    // Ajuste conforme massa de dados real
    assertTrue(result.getContent().get(0).name().compareTo(result.getContent().get(1).name()) <= 0);
  }

  @Test
  @DisplayName("findById should return category when id exists")
  void findByIdShouldReturnCategoryWhenIdExists() {

    // Act
    CategoryResponse result = service.findById(EXISTING_ID);

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
  @DisplayName("insert should persist category when valid data")
  void insertShouldPersistCategoryWhenValidData() {

    // Arrange
    CategoryCreateRequest request = CategoryFactory.createCategoryCreateRequest();

    // Act
    CategoryResponse result = service.insert(request);

    // Assert
    assertNotNull(result);
    assertNotNull(result.id());
    assertEquals(COUNT_TOTAL_CATEGORIES + 1, repository.count());
  }

  @Test
  @DisplayName("update should update category when id exists")
  void updateShouldUpdateCategoryWhenIdExists() {

    // Arrange
    CategoryUpdateRequest request = CategoryFactory.createCategoryUpdateRequest();

    // Act
    CategoryResponse result = service.update(EXISTING_ID, request);

    // Assert
    assertNotNull(result);
    assertEquals(EXISTING_ID, result.id());
    assertEquals(request.name(), result.name());
  }

  @Test
  @DisplayName("update should throw ResourceNotFoundException when id does not exist")
  void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Arrange
    CategoryUpdateRequest request = CategoryFactory.createCategoryUpdateRequest();

    // Act + Assert
    assertThrows(ResourceNotFoundException.class, () -> service.update(NON_EXISTING_ID, request));
  }

}
