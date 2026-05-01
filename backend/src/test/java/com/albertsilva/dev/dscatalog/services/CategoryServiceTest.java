package com.albertsilva.dev.dscatalog.services;

import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.EXISTING_ID;
import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.NON_EXISTING_ID;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@DisplayName("CategoryService Unit Tests")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @InjectMocks
  private CategoryService service;

  @Mock
  private CategoryRepository repository;

  @Mock
  private CategoryMapper categoryMapper;

  private Pageable pageable;
  private PageImpl<Category> page;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(0, 10);
    page = new PageImpl<>(List.of(CategoryFactory.createCategory()));
  }

  @Nested
  @DisplayName("Insert Operations")
  class InsertOperations {

    @Test
    @DisplayName("insert should save category successfully")
    void insertShouldSaveCategorySuccessfully() {

      CategoryCreateRequest request = Mockito.mock(CategoryCreateRequest.class);

      Category category = CategoryFactory.createCategory();
      category.setId(EXISTING_ID);

      CategoryResponse expectedResponse = Mockito.mock(CategoryResponse.class);

      Mockito.when(categoryMapper.toEntity(request)).thenReturn(category);
      Mockito.when(repository.save(category)).thenReturn(category);
      Mockito.when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

      CategoryResponse result = service.insert(request);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(expectedResponse, result);

      Mockito.verify(categoryMapper).toEntity(request);
      Mockito.verify(repository).save(category);
      Mockito.verify(categoryMapper).toResponse(category);
    }
  }

  @Nested
  @DisplayName("FindById Operations")
  class FindByIdOperations {

    @Test
    @DisplayName("findById should return category when id exists")
    void findByIdShouldReturnCategoryWhenIdExists() {

      Category category = CategoryFactory.createCategory();
      category.setId(EXISTING_ID);

      CategoryResponse expectedResponse = Mockito.mock(CategoryResponse.class);

      Mockito.when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(category));
      Mockito.when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

      CategoryResponse result = service.findById(EXISTING_ID);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(expectedResponse, result);

      Mockito.verify(repository).findById(EXISTING_ID);
      Mockito.verify(categoryMapper).toResponse(category);
    }

    @Test
    @DisplayName("findById should throw ResourceNotFoundException when id does not exist")
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

      Mockito.when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

      ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
          () -> service.findById(NON_EXISTING_ID));

      Assertions.assertEquals("Entity not found id: " + NON_EXISTING_ID, exception.getMessage());

      Mockito.verify(repository).findById(NON_EXISTING_ID);
      Mockito.verify(categoryMapper, Mockito.never()).toResponse(Mockito.any());
    }
  }

  @Nested
  @DisplayName("FindAllPaged Operations")
  class FindAllPagedOperations {

    @Test
    @DisplayName("findAllPaged should return paged categories")
    void findAllPagedShouldReturnPagedCategories() {

      Page<CategoryResponse> expectedPage = new PageImpl<>(List.of());

      Mockito.when(repository.findAll(pageable)).thenReturn(page);
      Mockito.when(categoryMapper.toResponsePage(page)).thenReturn(expectedPage);

      Page<CategoryResponse> result = service.findAllPaged(pageable);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(expectedPage, result);

      Mockito.verify(repository).findAll(pageable);
      Mockito.verify(categoryMapper).toResponsePage(page);
    }
  }

  @Nested
  @DisplayName("Search Operations")
  class SearchOperations {

    @Test
    @DisplayName("searchByName should return paged categories")
    void searchByNameShouldReturnPagedCategories() {

      String name = "Books";

      Page<CategoryResponse> expectedPage = new PageImpl<>(List.of());

      Mockito.when(repository.findByNameContainingIgnoreCase(name, pageable)).thenReturn(page);
      Mockito.when(categoryMapper.toResponsePage(page)).thenReturn(expectedPage);

      Page<CategoryResponse> result = service.searchByName(name, pageable);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(expectedPage, result);

      Mockito.verify(repository).findByNameContainingIgnoreCase(name, pageable);
      Mockito.verify(categoryMapper).toResponsePage(page);
    }
  }

  @Nested
  @DisplayName("Update Operations")
  class UpdateOperations {

    @Test
    @DisplayName("update should update category when id exists")
    void updateShouldUpdateCategoryWhenIdExists() {

      Category category = CategoryFactory.createCategory();
      category.setId(EXISTING_ID);

      CategoryUpdateRequest request = Mockito.mock(CategoryUpdateRequest.class);

      CategoryResponse expectedResponse = Mockito.mock(CategoryResponse.class);

      Mockito.when(repository.getReferenceById(EXISTING_ID)).thenReturn(category);
      Mockito.when(repository.save(category)).thenReturn(category);
      Mockito.when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

      CategoryResponse result = service.update(EXISTING_ID, request);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(expectedResponse, result);

      Mockito.verify(repository).getReferenceById(EXISTING_ID);
      Mockito.verify(categoryMapper).updateEntity(request, category);
      Mockito.verify(repository).save(category);
      Mockito.verify(categoryMapper).toResponse(category);
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when id does not exist")
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

      CategoryUpdateRequest request = Mockito.mock(CategoryUpdateRequest.class);

      Mockito.when(repository.getReferenceById(NON_EXISTING_ID)).thenThrow(EntityNotFoundException.class);

      ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
          () -> service.update(NON_EXISTING_ID, request));

      Assertions.assertEquals("Entity not found id: " + NON_EXISTING_ID, exception.getMessage());

      Mockito.verify(repository).getReferenceById(NON_EXISTING_ID);
      Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }
  }

  @Nested
  @DisplayName("Delete Operations")
  class DeleteOperations {

    @Test
    @DisplayName("delete should remove category when id exists")
    void deleteShouldRemoveCategoryWhenIdExists() {

      Category category = CategoryFactory.createCategory();
      category.setId(EXISTING_ID);

      Mockito.when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(category));

      Assertions.assertDoesNotThrow(() -> service.delete(EXISTING_ID));

      Mockito.verify(repository).findById(EXISTING_ID);
      Mockito.verify(repository).delete(category);
    }

    @Test
    @DisplayName("delete should throw ResourceNotFoundException when id does not exist")
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

      Mockito.when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

      ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
          () -> service.delete(NON_EXISTING_ID));

      Assertions.assertEquals("Entity not found id: " + NON_EXISTING_ID, exception.getMessage());

      Mockito.verify(repository).findById(NON_EXISTING_ID);
      Mockito.verify(repository, Mockito.never()).delete(Mockito.any());
    }
  }
}