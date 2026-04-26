package com.albertsilva.dev.dscatalog.services;

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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.albertsilva.dev.dscatalog.dto.category.mapper.CategoryMapper;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.factory.CategoryFactory;
import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

@DisplayName("Tests for CategoryService")
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  @InjectMocks
  private CategoryService service;

  @Mock
  private CategoryRepository repository;

  @Mock
  private CategoryMapper categoryMapper;

  private Long existingId;
  private Long nonExistingId;
  private Pageable pageable;
  private PageImpl<Category> page;

  @BeforeEach
  void setUp() throws Exception {
    existingId = 1L;
    nonExistingId = 1000L;
    pageable = PageRequest.of(0, 10);
    page = new PageImpl<>(List.of(CategoryFactory.createCategory()));
  }

  @Test
  @DisplayName("Delete should remove category when id exists")
  void deleteShouldRemoveCategoryWhenIdExists() {

    // Arrange
    Category category = CategoryFactory.createCategory();
    category.setId(existingId);

    Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));

    // Act
    Assertions.assertDoesNotThrow(() -> {
      service.delete(existingId);
    });

    // Assert (state)
    // Método void → valida ausência de exceção.

    // Verify (behavior)
    Mockito.verify(repository).findById(existingId);
    Mockito.verify(repository).delete(category);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when deleting non existing id")
  void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Arrange
    Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

    // Act
    ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      service.delete(nonExistingId);
    });

    // Assert (state)
    Assertions.assertEquals("Entity not found id: " + nonExistingId, exception.getMessage());

    // Verify (behavior)
    Mockito.verify(repository).findById(nonExistingId);
    Mockito.verify(repository, Mockito.never()).delete(Mockito.any());
  }

  @Test
  @DisplayName("Should return category when id exists")
  void findByIdShouldReturnCategoryWhenIdExists() {

    // Arrange
    Category category = CategoryFactory.createCategory();
    category.setId(existingId);

    CategoryResponse expectedResponse = Mockito.mock(CategoryResponse.class);

    Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));

    Mockito.when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

    // Act
    CategoryResponse result = service.findById(existingId);

    // Assert (state)
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResponse, result);

    // Verify (behavior)
    Mockito.verify(repository).findById(existingId);
    Mockito.verify(categoryMapper).toResponse(category);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when finding non existing id")
  void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

    // Arrange
    Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

    // Act
    ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      service.findById(nonExistingId);
    });

    // Assert (state)
    Assertions.assertEquals("Entity not found id: " + nonExistingId, exception.getMessage());

    // Verify (behavior)
    Mockito.verify(repository).findById(nonExistingId);
    Mockito.verify(categoryMapper, Mockito.never()).toResponse(Mockito.any());
  }

}
