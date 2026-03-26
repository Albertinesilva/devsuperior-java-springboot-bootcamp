package com.albertsilva.dev.dscatalog.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albertsilva.dev.dscatalog.dto.category.mapper.CategoryMapper;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;
import com.albertsilva.dev.dscatalog.services.exceptions.DatabaseException;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Transactional(readOnly = true)
  public List<CategoryResponse> findAll() {
    return categoryMapper.toResponseList(categoryRepository.findAll());
  }

  @Transactional(readOnly = true)
  public CategoryResponse findById(Long id) {
    Category entity = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity not found id: " + id));
    return categoryMapper.toResponse(entity);
  }

  @Transactional
  public CategoryResponse insert(CategoryCreateRequest categoryCreateRequest) {
    Category entity = categoryMapper.toEntity(categoryCreateRequest);
    entity = categoryRepository.save(entity);
    return categoryMapper.toResponse(entity);
  }

  @Transactional
  public CategoryResponse update(Long id, CategoryUpdateRequest categoryUpdateRequest) {
    try {
      Category entity = categoryRepository.getReferenceById(id);
      categoryMapper.updateEntity(categoryUpdateRequest, entity);
      entity = categoryRepository.save(entity);
      return categoryMapper.toResponse(entity);

    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Entity not found id: " + id);
    }
  }

  @Transactional
  public void delete(Long id) {
    Category entity = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity not found id: " + id));

    try {
      categoryRepository.delete(entity);

    } catch (DataIntegrityViolationException e) {
      throw new DatabaseException("Integrity violation: cannot delete category with related entities");
    }
  }
}
