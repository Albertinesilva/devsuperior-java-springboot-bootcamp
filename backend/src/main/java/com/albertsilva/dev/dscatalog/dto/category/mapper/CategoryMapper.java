package com.albertsilva.dev.dscatalog.dto.category.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.entities.Category;

@Component
public class CategoryMapper {

  public Category toEntity(CategoryCreateRequest request) {
    if (request == null) {
      return null;
    }

    Category entity = new Category();
    entity.setName(request.name());
    return entity;
  }

  public void updateEntity(CategoryUpdateRequest request, Category entity) {
    if (request == null || entity == null) {
      return;
    }

    entity.setName(request.name());
  }

  public CategoryResponse toResponse(Category entity) {
    if (entity == null) {
      return null;
    }

    return new CategoryResponse(
        entity.getId(),
        entity.getName());
  }

  public List<CategoryResponse> toResponseList(List<Category> entities) {
    if (entities == null) {
      return List.of();
    }

    return entities.stream()
        .map(this::toResponse)
        .toList();
  }
}