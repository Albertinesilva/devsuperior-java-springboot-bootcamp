package com.albertsilva.dev.dscatalog.integrations.services;

import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.COUNT_TOTAL_CATEGORIES;
import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.DEPENDENT_ID;
import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.EXISTING_ID;
import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.NON_DEPENDENT_ID;
import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.NON_EXISTING_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
    assertTrue(result.getContent().size() <= 10);
  }

  @Test
  @DisplayName("findAllPaged should return empty page when requested page does not exist")
  void findAllPagedShouldReturnEmptyPageWhenRequestedPageDoesNotExist() {

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
  @DisplayName("findAllPaged should return ordered categories when sorting by name")
  void findAllPagedShouldReturnOrderedCategoriesWhenSortingByName() {

    // Arrange
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

    // Act
    Page<CategoryResponse> result = service.findAllPaged(pageRequest);

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());

    List<String> names = result.getContent().stream().map(CategoryResponse::name).toList();

    List<String> sorted = new ArrayList<>(names);
    Collections.sort(sorted);

    assertEquals(sorted, names);
  }

  @Test
  @DisplayName("findById should return category when id exists")
  void findByIdShouldReturnCategoryWhenIdExists() {

    // Act
    CategoryResponse result = service.findById(EXISTING_ID);

    // Assert
    assertNotNull(result);
    assertEquals(EXISTING_ID, result.id());
    assertNotNull(result.name());
  }

  @Test
  @DisplayName("findById should throw ResourceNotFoundException when id does not exist")
  void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Act + Assert
    assertThrows(ResourceNotFoundException.class, () -> service.findById(NON_EXISTING_ID));
  }

  @Test
  @DisplayName("searchByName should return categories when name exists")
  void searchByNameShouldReturnCategoriesWhenNameExists() {

    // Arrange
    String name = "Books";
    PageRequest pageRequest = PageRequest.of(0, 10);

    // Act
    Page<CategoryResponse> result = service.searchByName(name, pageRequest);

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertTrue(result.getTotalElements() > 0);

    assertTrue(result
        .getContent()
        .stream()
        .allMatch(category -> category.name().toLowerCase().contains(name.toLowerCase())));
  }

  @Test
  @DisplayName("searchByName should return empty page when name does not exist")
  void searchByNameShouldReturnEmptyPageWhenNameDoesNotExist() {

    // Arrange
    String name = "NonExistingCategory";
    PageRequest pageRequest = PageRequest.of(0, 10);

    // Act
    Page<CategoryResponse> result = service.searchByName(name, pageRequest);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
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
    assertNotNull(result.name());
    assertEquals(COUNT_TOTAL_CATEGORIES + 1, repository.count());
    assertTrue(repository.existsById(result.id()));
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

    CategoryResponse updatedCategory = service.findById(EXISTING_ID);
    assertEquals(request.name(), updatedCategory.name());
  }

  @Test
  @DisplayName("update should throw ResourceNotFoundException when id does not exist")
  void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Arrange
    CategoryUpdateRequest request = CategoryFactory.createCategoryUpdateRequest();

    // Act + Assert
    assertThrows(ResourceNotFoundException.class, () -> service.update(NON_EXISTING_ID, request));
  }

  @Test
  @DisplayName("delete should remove category when id exists and category has no dependencies")
  void deleteShouldRemoveCategoryWhenIdExistsAndCategoryHasNoDependencies() {

    // Act
    service.delete(NON_DEPENDENT_ID);

    // Assert
    assertEquals(COUNT_TOTAL_CATEGORIES - 1, repository.count());
    assertFalse(repository.existsById(NON_DEPENDENT_ID));
  }

  @Test
  @DisplayName("delete should throw ResourceNotFoundException when id does not exist")
  void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Act + Assert
    assertThrows(ResourceNotFoundException.class, () -> service.delete(NON_EXISTING_ID));
  }

  @Test
  @DisplayName("delete should throw DataIntegrityViolationException when category has associated products")
  void deleteShouldThrowDataIntegrityViolationExceptionWhenCategoryHasAssociatedProducts() {

    // Arrange
    assertTrue(repository.existsById(DEPENDENT_ID));

    // Act + Assert
    assertThrows(DataIntegrityViolationException.class, () -> {
      service.delete(DEPENDENT_ID);
      repository.flush();
    });
  }
}