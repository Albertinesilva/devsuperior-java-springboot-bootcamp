package com.albertsilva.dev.dscatalog.dto.category.mapper;

import org.springframework.data.domain.Page;
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
    entity.setDescription(request.description());

    // trata null → default false
    entity.setActive(request.active() != null ? request.active() : false);

    return entity;
  }

  public void updateEntity(CategoryUpdateRequest request, Category entity) {
    if (request == null || entity == null) {
      return;
    }

    if (request.name() != null) {
      entity.setName(request.name());
    }

    if (request.description() != null) {
      entity.setDescription(request.description());
    }

    // só atualiza se vier no request
    if (request.active() != null) {
      entity.setActive(request.active());
    }
  }

  public CategoryResponse toResponse(Category entity) {
    if (entity == null) {
      return null;
    }

    return new CategoryResponse(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        entity.isActive());
  }

  public Page<CategoryResponse> toResponsePage(Page<Category> entities) {
    return entities.map(this::toResponse);
  }
}