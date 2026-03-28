package com.albertsilva.dev.dscatalog.web.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.albertsilva.dev.dscatalog.dto.product.request.ProductCreateRequest;
import com.albertsilva.dev.dscatalog.dto.product.request.ProductUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductDetailsResponse;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductResponse;
import com.albertsilva.dev.dscatalog.services.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  public ResponseEntity<ProductResponse> insert(@RequestBody ProductCreateRequest productCreateRequest) {
    ProductResponse productResponse = productService.insert(productCreateRequest);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(productResponse.id())
        .toUri();
    return ResponseEntity.created(uri).body(productResponse);
  }

  @GetMapping
  public ResponseEntity<Page<ProductResponse>> findAll(Pageable pageable) {
    return ResponseEntity.ok(productService.findAllPaged(pageable));
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ProductDetailsResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.findById(id));
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<ProductResponse> update(@PathVariable Long id,
      @RequestBody ProductUpdateRequest productUpdateRequest) {
    ProductResponse productResponse = productService.update(id, productUpdateRequest);
    return ResponseEntity.ok(productResponse);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
