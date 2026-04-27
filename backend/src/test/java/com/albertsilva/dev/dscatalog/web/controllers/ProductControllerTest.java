package com.albertsilva.dev.dscatalog.web.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.albertsilva.dev.dscatalog.dto.product.request.ProductUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductDetailsResponse;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductResponse;
import com.albertsilva.dev.dscatalog.factory.ProductFactory;
import com.albertsilva.dev.dscatalog.services.ProductService;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;
import com.albertsilva.dev.dscatalog.web.exceptions.advice.ControllerExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
@Import(ControllerExceptionHandler.class)
@DisplayName("Tests for ProductController")
class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ProductService productService;

  private Long existingId;
  private Long nonExistingId;

  private Page<ProductResponse> page;

  private ProductResponse productResponse;
  private ProductDetailsResponse productDetailsResponse;

  private static final String BASE_URL = "/api/v1/products";

  @BeforeEach
  void setUp() {

    existingId = 1L;
    nonExistingId = 1000L;

    productResponse = ProductFactory.createProductResponse();

    productDetailsResponse = ProductFactory.createProductDetailsResponse();

    page = new PageImpl<>(List.of(productResponse), PageRequest.of(0, 10), 1);
  }

  @Test
  @DisplayName("GET /products should return paged products")
  void findAllShouldReturnPage() throws Exception {

    // Arrange
    when(productService.findAllPaged(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

    // Act
    ResultActions resultActions = mockMvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].id").value(existingId))
        .andExpect(jsonPath("$.content[0].name").value(productResponse.name()))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.number").value(0));

  }

  @Test
  @DisplayName("GET /products/{id} should return product when id exists")
  void findByIdShouldReturnProductWhenIdExists() throws Exception {

    // Arrange
    when(productService.findById(existingId)).thenReturn(productDetailsResponse);

    // Act
    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL + "/{id}", existingId).accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existingId))
        .andExpect(jsonPath("$.name").value(productDetailsResponse.name()));
  }

  @Test
  @DisplayName("GET /products/{id} should return 404 when id does not exist")
  void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

    // Arrange
    when(productService.findById(nonExistingId))
        .thenThrow(new ResourceNotFoundException("Entity not found id: " + nonExistingId));

    // Act
    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL + "/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("PATCH /products/{id} should update product when id exists")
  void updateShouldReturnUpdatedProductWhenIdExists() throws Exception {

    // Arrange
    ProductUpdateRequest request = ProductFactory.createProductUpdateRequest();
    String jsonRequest = objectMapper.writeValueAsString(request);

    ProductResponse updatedResponse = ProductFactory.createUpdatedProductResponse();

    when(productService.update(eq(existingId), ArgumentMatchers.any()))
        .thenReturn(updatedResponse);

    // Act
    ResultActions resultActions = mockMvc.perform(
        patch(BASE_URL + "/{id}", existingId)
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedResponse.id()))
        .andExpect(jsonPath("$.name").value(updatedResponse.name()));
  }

  @Test
  @DisplayName("PATCH /products/{id} should return 404 when id does not exist")
  void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

    // Arrange
    ProductUpdateRequest request = ProductFactory.createProductUpdateRequest();

    String jsonRequest = objectMapper.writeValueAsString(request);

    when(productService.update(eq(nonExistingId), ArgumentMatchers.any()))
        .thenThrow(new ResourceNotFoundException("Entity not found id: " + nonExistingId));

    // Act + Assert
    mockMvc.perform(patch(BASE_URL + "/{id}", nonExistingId)
        .content(jsonRequest)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

}