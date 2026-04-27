package com.albertsilva.dev.dscatalog.services;

import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.EXISTING_ID;
import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.NON_EXISTING_ID;

import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.albertsilva.dev.dscatalog.dto.category.mapper.CategoryMapper;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.factory.CategoryFactory;
import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@DisplayName("Tests for CategoryService")
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  @InjectMocks
  private CategoryService service;

  @Mock
  private CategoryRepository repository;

  @Mock
  private CategoryMapper categoryMapper;

  private Pageable pageable;
  private PageImpl<Category> page;

  @BeforeEach
  void setUp() throws Exception {

    pageable = PageRequest.of(0, 10);
    page = new PageImpl<>(List.of(CategoryFactory.createCategory()));
  }

  @Test
  @DisplayName("Delete should remove category when id exists")
  void deleteShouldRemoveCategoryWhenIdExists() {

    // Arrange
    Category category = CategoryFactory.createCategory();
    category.setId(EXISTING_ID);

    Mockito.when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(category));

    // Act
    Assertions.assertDoesNotThrow(() -> {
      service.delete(EXISTING_ID);
    });

    // Assert (state)
    // Método void → valida ausência de exceção.

    // Verify (behavior)
    Mockito.verify(repository).findById(EXISTING_ID);
    Mockito.verify(repository).delete(category);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when deleting non existing id")
  void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Arrange
    Mockito.when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

    // Act
    ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      service.delete(NON_EXISTING_ID);
    });

    // Assert (state)
    Assertions.assertEquals("Entity not found id: " + NON_EXISTING_ID, exception.getMessage());

    // Verify (behavior)
    Mockito.verify(repository).findById(NON_EXISTING_ID);
    Mockito.verify(repository, Mockito.never()).delete(Mockito.any());
  }

  @Test
  @DisplayName("Should return category when id exists")
  void findByIdShouldReturnCategoryWhenIdExists() {

    // Arrange
    Category category = CategoryFactory.createCategory();
    category.setId(EXISTING_ID);

    CategoryResponse expectedResponse = Mockito.mock(CategoryResponse.class);

    Mockito.when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(category));

    Mockito.when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

    // Act
    CategoryResponse result = service.findById(EXISTING_ID);

    // Assert (state)
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResponse, result);

    // Verify (behavior)
    Mockito.verify(repository).findById(EXISTING_ID);
    Mockito.verify(categoryMapper).toResponse(category);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when finding non existing id")
  void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Arrange
    Mockito.when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

    // Act
    ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      service.findById(NON_EXISTING_ID);
    });

    // Assert (state)
    Assertions.assertEquals("Entity not found id: " + NON_EXISTING_ID, exception.getMessage());

    // Verify (behavior)
    Mockito.verify(repository).findById(NON_EXISTING_ID);
    Mockito.verify(categoryMapper, Mockito.never()).toResponse(Mockito.any());
  }

  @Test
  @DisplayName("Should return paged categories")
  void findAllPagedShouldReturnPage() {

    // Arrange
    Page<CategoryResponse> expectedPage = new PageImpl<>(List.of());

    Mockito.when(repository.findAll(pageable)).thenReturn(page);

    Mockito.when(categoryMapper.toResponsePage(page)).thenReturn(expectedPage);

    // Act
    Page<CategoryResponse> result = service.findAllPaged(pageable);

    // Assert (state)
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedPage, result);

    // Verify (behavior)
    Mockito.verify(repository).findAll(pageable);
    Mockito.verify(categoryMapper).toResponsePage(page);
  }

  @Test
  @DisplayName("Should insert category")
  void insertShouldSaveCategory() {

    // Arrange
    CategoryCreateRequest request = Mockito.mock(CategoryCreateRequest.class);

    Category category = CategoryFactory.createCategory();
    category.setId(EXISTING_ID);

    CategoryResponse expectedResponse = Mockito.mock(CategoryResponse.class);

    Mockito.when(categoryMapper.toEntity(request)).thenReturn(category);

    Mockito.when(repository.save(category)).thenReturn(category);

    Mockito.when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

    // Act
    CategoryResponse result = service.insert(request);

    // Assert (state)
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResponse, result);

    // Verify (behavior)
    Mockito.verify(categoryMapper).toEntity(request);
    Mockito.verify(repository).save(category);
    Mockito.verify(categoryMapper).toResponse(category);
  }

  @Test
  @DisplayName("Should update category when id exists")
  void updateShouldUpdateCategoryWhenIdExists() {

    // Arrange
    Category category = CategoryFactory.createCategory();
    category.setId(EXISTING_ID);

    CategoryUpdateRequest request = Mockito.mock(CategoryUpdateRequest.class);

    CategoryResponse expectedResponse = Mockito.mock(CategoryResponse.class);

    Mockito.when(repository.getReferenceById(EXISTING_ID)).thenReturn(category);

    Mockito.when(repository.save(category)).thenReturn(category);

    Mockito.when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

    // Act
    CategoryResponse result = service.update(EXISTING_ID, request);

    // Assert (state)
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResponse, result);

    // Verify (behavior)
    Mockito.verify(repository).getReferenceById(EXISTING_ID);
    Mockito.verify(categoryMapper).updateEntity(request, category);
    Mockito.verify(repository).save(category);
    Mockito.verify(categoryMapper).toResponse(category);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when updating non existing id")
  void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Arrange
    CategoryUpdateRequest request = Mockito.mock(CategoryUpdateRequest.class);

    Mockito.when(repository.getReferenceById(NON_EXISTING_ID)).thenThrow(EntityNotFoundException.class);

    // Act
    ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      service.update(NON_EXISTING_ID, request);
    });

    // Assert (state)
    Assertions.assertEquals("Entity not found id: " + NON_EXISTING_ID, exception.getMessage());

    // Verify (behavior)
    Mockito.verify(repository).getReferenceById(NON_EXISTING_ID);
    Mockito.verify(repository, Mockito.never()).save(Mockito.any());
  }

  @Test
  @DisplayName("Should search categories by name")
  void searchByNameShouldReturnPagedCategories() {

    // Arrange
    String name = "Books";

    Page<CategoryResponse> expectedPage = new PageImpl<>(List.of());

    Mockito.when(repository.findByNameContainingIgnoreCase(name, pageable)).thenReturn(page);

    Mockito.when(categoryMapper.toResponsePage(page)).thenReturn(expectedPage);

    // Act
    Page<CategoryResponse> result = service.searchByName(name, pageable);

    // Assert (state)
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedPage, result);

    // Verify (behavior)
    Mockito.verify(repository).findByNameContainingIgnoreCase(name, pageable);

    Mockito.verify(categoryMapper).toResponsePage(page);
  }
}
