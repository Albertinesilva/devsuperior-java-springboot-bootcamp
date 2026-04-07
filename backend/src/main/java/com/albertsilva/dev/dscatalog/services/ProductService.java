package com.albertsilva.dev.dscatalog.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

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
    logger.debug("Buscando produtos paginados - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
    Page<Product> products = productRepository.findAll(pageable);
    logger.debug("Total de produtos encontrados: {}", products.getTotalElements());
    return productMapper.toResponsePage(products);
  }

  @Transactional(readOnly = true)
  public ProductDetailsResponse findById(Long id) {
    logger.debug("Buscando produto por id: {}", id);
    Product entity = productRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Produto não encontrado. id: {}", id);
          return new ResourceNotFoundException("Entity not found id: " + id);
        });
    logger.debug("Produto encontrado. id: {}", id);
    return productMapper.toDetailsResponse(entity);
  }

  @Transactional
  public ProductResponse insert(ProductCreateRequest productCreateRequest) {
    logger.debug("Inserindo novo produto - dados: {}", productCreateRequest);
    Product entity = productMapper.toEntity(productCreateRequest);
    mapCategories(entity, productCreateRequest.categoryIds());
    entity = productRepository.save(entity);
    logger.info("Produto criado com sucesso. id: {}", entity.getId());
    return productMapper.toResponse(entity);
  }

  @Transactional
  public ProductResponse update(Long id, ProductUpdateRequest dto) {
    logger.debug("Atualizando produto. id: {}", id);

    try {
      Product entity = productRepository.getReferenceById(id);
      productMapper.updateEntity(dto, entity);

      if (dto.categoryIds() != null) {
        mapCategories(entity, dto.categoryIds());
        logger.debug("Categorias do produto atualizadas. id: {}", id);
      }

      entity = productRepository.save(entity);
      logger.info("Produto atualizado com sucesso. id: {}", id);
      return productMapper.toResponse(entity);

    } catch (EntityNotFoundException e) {
      logger.warn("Falha ao atualizar produto. Produto não encontrado. id: {}", id);
      throw new ResourceNotFoundException("Entity not found id: " + id);
    }
  }

  @Transactional
  public void delete(Long id) {
    logger.debug("Deletando produto. id: {}", id);

    Product entity = productRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Falha ao deletar. Produto não encontrado. id: {}", id);
          return new ResourceNotFoundException("Entity not found id: " + id);
        });

    try {
      productRepository.delete(entity);
      logger.info("Produto deletado com sucesso. id: {}", id);

    } catch (DataIntegrityViolationException e) {
      logger.error("Erro de integridade ao deletar produto. id: {}", id);
      throw new DatabaseException("Integrity violation: cannot delete category with related entities");
    }
  }

  private void mapCategories(Product entity, List<Long> categoryIds) {
    entity.getCategories().clear();

    if (categoryIds == null || categoryIds.isEmpty()) {
      logger.debug("Nenhuma categoria fornecida para mapear ao produto. id: {}", entity.getId());
      return;
    }

    for (Long categoryId : categoryIds) {
      Category category = categoryRepository.getReferenceById(categoryId);
      entity.getCategories().add(category);
      logger.debug("Categoria mapeada ao produto. produtoId: {}, categoriaId: {}", entity.getId(), categoryId);
    }
  }
}