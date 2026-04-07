package com.albertsilva.dev.dscatalog.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Transactional(readOnly = true)
  public Page<CategoryResponse> findAllPaged(Pageable pageable) {
    logger.debug("Buscando categorias paginadas - page: {}, size: {}", pageable.getPageNumber(),
        pageable.getPageSize());
    return categoryMapper.toResponsePage(categoryRepository.findAll(pageable));
  }

  @Transactional(readOnly = true)
  public CategoryResponse findById(Long id) {
    logger.debug("Buscando categoria por id: {}", id);

    Category entity = categoryRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Categoria não encontrada. id: {}", id);
          return new ResourceNotFoundException("Entity not found id: " + id);
        });

    logger.debug("Categoria encontrada. id: {}", id);
    return categoryMapper.toResponse(entity);
  }

  @Transactional
  public CategoryResponse insert(CategoryCreateRequest categoryCreateRequest) {
    logger.debug("Inserindo nova categoria - dados: {}", categoryCreateRequest);

    Category entity = categoryMapper.toEntity(categoryCreateRequest);
    entity = categoryRepository.save(entity);

    logger.info("Categoria criada com sucesso. id: {}", entity.getId());
    return categoryMapper.toResponse(entity);
  }

  @Transactional
  public CategoryResponse update(Long id, CategoryUpdateRequest categoryUpdateRequest) {
    logger.debug("Atualizando categoria. id: {}", id);

    try {
      Category entity = categoryRepository.getReferenceById(id);
      categoryMapper.updateEntity(categoryUpdateRequest, entity);
      entity = categoryRepository.save(entity);

      logger.info("Categoria atualizada com sucesso. id: {}", id);
      return categoryMapper.toResponse(entity);

    } catch (EntityNotFoundException e) {
      logger.warn("Falha ao atualizar. Categoria não encontrada. id: {}", id);
      throw new ResourceNotFoundException("Entity not found id: " + id);
    }
  }

  @Transactional
  public void delete(Long id) {
    logger.debug("Deletando categoria. id: {}", id);

    Category entity = categoryRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Falha ao deletar. Categoria não encontrada. id: {}", id);
          return new ResourceNotFoundException("Entity not found id: " + id);
        });

    try {
      categoryRepository.delete(entity);
      logger.info("Categoria deletada com sucesso. id: {}", id);

    } catch (DataIntegrityViolationException e) {
      logger.error("Erro de integridade ao deletar categoria. id: {}", id);
      throw new DatabaseException("Integrity violation: cannot delete category with related entities");
    }
  }

  @Transactional(readOnly = true)
  public Page<CategoryResponse> searchByName(String name, Pageable pageable) {
    logger.debug("Buscando categorias por nome. termo: {}", name);

    Page<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name, pageable);

    logger.debug("Resultado da busca por nome '{}' - total encontrados: {}", name, categories.getTotalElements());
    return categoryMapper.toResponsePage(categories);
  }
}