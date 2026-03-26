package com.albertsilva.dev.dscatalog.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albertsilva.dev.dscatalog.dto.category.mapper.CategoryMapper;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;
import com.albertsilva.dev.dscatalog.services.exceptions.EntityNotFoundException;

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
    Category entity = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found id: " + id));
    return categoryMapper.toResponse(entity);
  }

  @Transactional
  public CategoryResponse insert(CategoryCreateRequest categoryCreateRequest) {
    Category entity = categoryMapper.toEntity(categoryCreateRequest);
    entity = categoryRepository.save(entity);
    return categoryMapper.toResponse(entity);
  }
}
