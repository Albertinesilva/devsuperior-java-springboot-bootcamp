package com.albertsilva.dev.dscatalog.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albertsilva.dev.dscatalog.dto.product.mapper.ProductMapper;
import com.albertsilva.dev.dscatalog.dto.product.request.ProductCreateRequest;
import com.albertsilva.dev.dscatalog.dto.product.request.ProductUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductDetailsResponse;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductResponse;
import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.entities.Product;
import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;
import com.albertsilva.dev.dscatalog.repositories.ProductRepository;
import com.albertsilva.dev.dscatalog.services.exceptions.DatabaseException;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
      ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.productMapper = productMapper;
  }

  @Transactional(readOnly = true)
  public Page<ProductResponse> findAllPaged(Pageable pageable) {
    return productMapper.toResponsePage(productRepository.findAll(pageable));
  }

  @Transactional(readOnly = true)
  public ProductDetailsResponse findById(Long id) {
    Product entity = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity not found id: " + id));
    return productMapper.toDetailsResponse(entity);
  }

  @Transactional
  public ProductResponse insert(ProductCreateRequest productCreateRequest) {
    Product entity = productMapper.toEntity(productCreateRequest);
    mapCategories(entity, productCreateRequest.categoryIds());
    entity = productRepository.save(entity);
    return productMapper.toResponse(entity);
  }

  @Transactional
  public ProductResponse update(Long id, ProductUpdateRequest dto) {
    try {
      Product entity = productRepository.getReferenceById(id);

      productMapper.updateEntity(dto, entity);

      if (dto.categoryIds() != null) {
        mapCategories(entity, dto.categoryIds());
      }

      return productMapper.toResponse(entity);

    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Entity not found id: " + id);
    }
  }

  @Transactional
  public void delete(Long id) {
    Product entity = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity not found id: " + id));

    try {
      productRepository.delete(entity);

    } catch (DataIntegrityViolationException e) {
      throw new DatabaseException("Integrity violation: cannot delete category with related entities");
    }
  }

  private void mapCategories(Product entity, List<Long> categoryIds) {
    entity.getCategories().clear();

    if (categoryIds == null || categoryIds.isEmpty()) {
      return;
    }

    for (Long categoryId : categoryIds) {
      Category category = categoryRepository.getReferenceById(categoryId);
      entity.getCategories().add(category);
    }
  }
}
