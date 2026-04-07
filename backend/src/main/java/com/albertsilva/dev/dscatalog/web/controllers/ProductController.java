package com.albertsilva.dev.dscatalog.web.controllers;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.albertsilva.dev.dscatalog.dto.product.request.ProductCreateRequest;
import com.albertsilva.dev.dscatalog.dto.product.request.ProductUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductDetailsResponse;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductResponse;
import com.albertsilva.dev.dscatalog.services.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  public ResponseEntity<ProductResponse> insert(@RequestBody ProductCreateRequest productCreateRequest) {
    logger.debug("Recebendo requisição para criar produto: {}", productCreateRequest);
    ProductResponse productResponse = productService.insert(productCreateRequest);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(productResponse.id())
        .toUri();
    logger.info("Produto criado com sucesso. id={}", productResponse.id());
    return ResponseEntity.created(uri).body(productResponse);
  }

  @GetMapping
  public ResponseEntity<Page<ProductResponse>> findAll(Pageable pageable) {
    logger.debug("Buscando produtos paginados - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
    Page<ProductResponse> response = productService.findAllPaged(pageable);
    logger.debug("Produtos retornados: {}", response.getTotalElements());
    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ProductDetailsResponse> findById(@PathVariable Long id) {
    logger.debug("Buscando produto por id: {}", id);
    ProductDetailsResponse response = productService.findById(id);
    logger.debug("Produto encontrado: id={}", id);
    return ResponseEntity.ok(response);
  }

  @PatchMapping(value = "/{id}")
  public ResponseEntity<ProductResponse> update(@PathVariable Long id,
      @RequestBody ProductUpdateRequest productUpdateRequest) {

    logger.debug("Atualizando produto id={} com dados: {}", id, productUpdateRequest);
    ProductResponse response = productService.update(id, productUpdateRequest);
    logger.info("Produto atualizado com sucesso. id={}", id);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    logger.debug("Deletando produto id={}", id);
    productService.delete(id);
    logger.info("Produto deletado com sucesso. id={}", id);
    return ResponseEntity.noContent().build();
  }
}