package com.albertsilva.dev.dscatalog.web.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.services.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping
  public ResponseEntity<CategoryResponse> insert(@RequestBody CategoryCreateRequest categoryCreateRequest) {
    CategoryResponse categoryResponse = categoryService.insert(categoryCreateRequest);
    URI uri = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(categoryResponse.id())
        .toUri();
    return ResponseEntity.created(uri).body(categoryResponse);
  }

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> findAll() {
    return ResponseEntity.ok(categoryService.findAll());
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(categoryService.findById(id));
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
      @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
    CategoryResponse categoryResponse = categoryService.update(id, categoryUpdateRequest);
    return ResponseEntity.ok(categoryResponse);
  }
}
