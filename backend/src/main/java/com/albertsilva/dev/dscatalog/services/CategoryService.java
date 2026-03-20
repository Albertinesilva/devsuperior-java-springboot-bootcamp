package com.albertsilva.dev.dscatalog.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albertsilva.dev.dscatalog.dto.category.mapper.CategoryMapper;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;

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
    Category entity = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    return categoryMapper.toResponse(entity);
  }
}
