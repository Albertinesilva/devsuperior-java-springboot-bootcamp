package com.albertsilva.dev.dscatalog.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.albertsilva.dev.dscatalog.entities.Category;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

  @GetMapping
  public ResponseEntity<List<Category>> findAll() {
    List<Category> list = new ArrayList<>();
    list.add(new Category(1L, "Books"));
    list.add(new Category(2L, "Electronics"));
    return ResponseEntity.ok().body(list);

  }
}
