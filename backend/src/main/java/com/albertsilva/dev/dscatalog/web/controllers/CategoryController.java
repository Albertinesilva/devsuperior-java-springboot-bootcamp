package com.albertsilva.dev.dscatalog.web.controllers;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.services.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping
  public ResponseEntity<CategoryResponse> insert(@RequestBody CategoryCreateRequest categoryCreateRequest) {
    logger.debug("Recebendo requisição para criar categoria: {}", categoryCreateRequest);
    CategoryResponse categoryResponse = categoryService.insert(categoryCreateRequest);
    URI uri = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(categoryResponse.id())
        .toUri();
    logger.info("Categoria criada com sucesso. id: {}", categoryResponse.id());
    return ResponseEntity.created(uri).body(categoryResponse);
  }

  @GetMapping
  public ResponseEntity<Page<CategoryResponse>> findAll(
      @RequestParam(value = "page", defaultValue = "0") Integer page,
      @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
      @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
      @RequestParam(value = "direction", defaultValue = "ASC") String direction) {

    logger.debug("Buscando categorias paginadas - page: {}, size: {}, orderBy: {}, direction: {}", page, linesPerPage,
        orderBy, direction);

    Pageable pageable = PageRequest.of(page, linesPerPage, Direction.fromString(direction), orderBy);
    Page<CategoryResponse> response = categoryService.findAllPaged(pageable);

    logger.debug("Categorias retornadas: {}", response.getTotalElements());
    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
    logger.debug("Buscando categoria por id: {}", id);
    CategoryResponse response = categoryService.findById(id);
    logger.debug("Categoria encontrada: id={}", id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  public ResponseEntity<Page<CategoryResponse>> search(@RequestParam String name, Pageable pageable) {
    logger.debug("Buscando categorias por nome: {}", name);
    Page<CategoryResponse> response = categoryService.searchByName(name, pageable);
    logger.debug("Categorias encontradas: {}", response.getTotalElements());
    return ResponseEntity.ok(response);
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
      @RequestBody CategoryUpdateRequest categoryUpdateRequest) {

    logger.debug("Atualizando categoria id={} com dados: {}", id, categoryUpdateRequest);
    CategoryResponse response = categoryService.update(id, categoryUpdateRequest);
    logger.info("Categoria atualizada com sucesso. id={}", id);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    logger.debug("Deletando categoria id={}", id);
    categoryService.delete(id);
    logger.info("Categoria deletada com sucesso. id={}", id);
    return ResponseEntity.noContent().build();
  }
}