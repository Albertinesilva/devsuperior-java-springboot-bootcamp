package com.albertsilva.dev.dscatalog.dto.product.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.dto.product.request.ProductCreateRequest;
import com.albertsilva.dev.dscatalog.dto.product.request.ProductUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductDetailsResponse;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductResponse;
import com.albertsilva.dev.dscatalog.entities.Product;

@Component
public class ProductMapper {

  public Product toEntity(ProductCreateRequest request) {
    if (request == null) {
      return null;
    }

    Product entity = new Product();
    entity.setName(request.name());
    entity.setDescription(request.description());
    entity.setPrice(request.price());
    entity.setImgUrl(request.imgUrl());
    entity.setDate(request.date());

    // trata null → default false
    entity.setActive(request.active() != null ? request.active() : false);

    return entity;
  }

  public void updateEntity(ProductUpdateRequest request, Product entity) {
    if (request == null || entity == null) {
      return;
    }

    if (request.name() != null) {
      entity.setName(request.name());
    }

    if (request.description() != null) {
      entity.setDescription(request.description());
    }

    if (request.active() != null) {
      entity.setActive(request.active());
    }

    if (request.price() != null) {
      entity.setPrice(request.price());
    }

    if (request.imgUrl() != null) {
      entity.setImgUrl(request.imgUrl());
    }

    if (request.date() != null) {
      entity.setDate(request.date());
    }
  }

  public ProductResponse toResponse(Product entity) {
    if (entity == null) {
      return null;
    }

    return new ProductResponse(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getPrice(),
        entity.getImgUrl(),
        entity.getDate(),
        List.of());
  }

  public ProductDetailsResponse toDetailsResponse(Product entity) {
    if (entity == null)
      return null;

    return new ProductDetailsResponse(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getPrice(),
        entity.getImgUrl(),
        entity.getDate(),
        entity.getCategories().stream().map(cat -> new CategoryResponse(
            cat.getId(),
            cat.getName(),
            cat.getDescription(),
            cat.isActive())).toList());
  }

  public Page<ProductResponse> toResponsePage(Page<Product> entities) {
    return entities.map(this::toResponse);
  }
}