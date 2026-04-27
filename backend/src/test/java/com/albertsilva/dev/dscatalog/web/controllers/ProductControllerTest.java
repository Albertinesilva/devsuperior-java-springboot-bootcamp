package com.albertsilva.dev.dscatalog.web.controllers;

import static com.albertsilva.dev.dscatalog.factory.ProductFactory.EXISTING_ID;
import static com.albertsilva.dev.dscatalog.factory.ProductFactory.NON_EXISTING_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import com.albertsilva.dev.dscatalog.dto.product.request.ProductCreateRequest;
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

  private static final String BASE_URL = "/api/v1/products";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ProductService productService;

  private Page<ProductResponse> page;

  private ProductResponse productResponse;
  private ProductDetailsResponse productDetailsResponse;

  @BeforeEach
  void setUp() {

    productResponse = ProductFactory.createProductResponse();
    productDetailsResponse = ProductFactory.createProductDetailsResponse();

    page = new PageImpl<>(List.of(productResponse), PageRequest.of(0, 10), 1);
  }

  @Test
  @DisplayName("GET /products should return paged products")
  void findAllShouldReturnPage() throws Exception {

    // Arrange
    when(productService.findAllPaged(any(Pageable.class))).thenReturn(page);

    // Act
    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].id").value(EXISTING_ID))
        .andExpect(jsonPath("$.content[0].name").value(productResponse.name()))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.number").value(0));

    verify(productService).findAllPaged(any(Pageable.class));
  }

  @Test
  @DisplayName("GET /products/{id} should return product when id exists")
  void findByIdShouldReturnProductWhenIdExists() throws Exception {

    // Arrange
    when(productService.findById(EXISTING_ID)).thenReturn(productDetailsResponse);

    // Act
    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL + "/{id}", EXISTING_ID).accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(EXISTING_ID))
        .andExpect(jsonPath("$.name").value(productDetailsResponse.name()))
        .andExpect(jsonPath("$.description").value(productDetailsResponse.description()))
        .andExpect(jsonPath("$.price").value(productDetailsResponse.price()));

    verify(productService).findById(EXISTING_ID);
  }

  @Test
  @DisplayName("GET /products/{id} should return 404 when id does not exist")
  void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

    // Arrange
    when(productService.findById(NON_EXISTING_ID))
        .thenThrow(new ResourceNotFoundException("Entity not found id: " + NON_EXISTING_ID));

    // Act
    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL + "/{id}", NON_EXISTING_ID).accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions.andExpect(status().isNotFound());

    verify(productService).findById(NON_EXISTING_ID);
  }

  @Test
  @DisplayName("POST /products should insert product")
  void insertShouldReturnCreatedProduct() throws Exception {

    // Arrange
    ProductCreateRequest request = ProductFactory.createProductCreateRequest();
    String jsonRequest = asJson(request);

    when(productService.insert(any(ProductCreateRequest.class))).thenReturn(productResponse);

    // Act
    ResultActions resultActions = mockMvc
        .perform(post(BASE_URL)
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").value(productResponse.id()))
        .andExpect(jsonPath("$.name").value(productResponse.name()))
        .andExpect(jsonPath("$.description").value(productResponse.description()))
        .andExpect(jsonPath("$.price").value(productResponse.price()));

    verify(productService).insert(any(ProductCreateRequest.class));
  }

  @Test
  @DisplayName("PATCH /products/{id} should update product when id exists")
  void updateShouldReturnUpdatedProductWhenIdExists() throws Exception {

    // Arrange
    ProductUpdateRequest request = ProductFactory.createProductUpdateRequest();
    String jsonRequest = asJson(request);

    ProductResponse updatedResponse = ProductFactory.createUpdatedProductResponse();

    when(productService.update(eq(EXISTING_ID), any(ProductUpdateRequest.class))).thenReturn(updatedResponse);

    // Act
    ResultActions resultActions = mockMvc
        .perform(patch(BASE_URL + "/{id}", EXISTING_ID)
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedResponse.id()))
        .andExpect(jsonPath("$.name").value(updatedResponse.name()))
        .andExpect(jsonPath("$.description").value(updatedResponse.description()))
        .andExpect(jsonPath("$.price").value(updatedResponse.price()));

    verify(productService).update(eq(EXISTING_ID), any(ProductUpdateRequest.class));
  }

  @Test
  @DisplayName("PATCH /products/{id} should return 404 when id does not exist")
  void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

    // Arrange
    ProductUpdateRequest request = ProductFactory.createProductUpdateRequest();
    String jsonRequest = asJson(request);

    when(productService.update(eq(NON_EXISTING_ID), any(ProductUpdateRequest.class)))
        .thenThrow(new ResourceNotFoundException("Entity not found id: " + NON_EXISTING_ID));

    // Act
    ResultActions resultActions = mockMvc
        .perform(patch(BASE_URL + "/{id}", NON_EXISTING_ID)
            .content(jsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions.andExpect(status().isNotFound());

    verify(productService).update(eq(NON_EXISTING_ID), any(ProductUpdateRequest.class));
  }

  @Test
  @DisplayName("DELETE /products/{id} should delete product when id exists")
  void deleteShouldReturnNoContentWhenIdExists() throws Exception {

    // Arrange
    doNothing().when(productService).delete(EXISTING_ID);

    // Act + Assert
    mockMvc.perform(delete(BASE_URL + "/{id}", EXISTING_ID)).andExpect(status().isNoContent());

    verify(productService).delete(EXISTING_ID);
  }

  @Test
  @DisplayName("DELETE /products/{id} should return 404 when id does not exist")
  void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

    // Arrange
    doThrow(new ResourceNotFoundException("Entity not found id: " + NON_EXISTING_ID)).when(productService)
        .delete(NON_EXISTING_ID);

    // Act + Assert
    mockMvc.perform(delete(BASE_URL + "/{id}", NON_EXISTING_ID)).andExpect(status().isNotFound());

    verify(productService).delete(NON_EXISTING_ID);
  }

  private String asJson(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }
}