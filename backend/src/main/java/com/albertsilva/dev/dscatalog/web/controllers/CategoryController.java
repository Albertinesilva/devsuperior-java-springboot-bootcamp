package com.albertsilva.dev.dscatalog.web.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.services.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  public ResponseEntity<List<Category>> findAll() {
    List<Category> list = categoryService.findAll();
    return ResponseEntity.ok().body(list);

  }
}
