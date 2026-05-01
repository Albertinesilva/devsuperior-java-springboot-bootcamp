package com.albertsilva.dev.dscatalog.integrations.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.albertsilva.dev.dscatalog.repositories.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("ProductController Integration Tests")
public class ProductControllerIT {

  private static final String BASE_URL = "/api/v1/products";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProductRepository productRepository;

  private long totalProductsCount;

  @BeforeEach
  void setUp() {
    totalProductsCount = productRepository.count();
  }

  @Test
  @DisplayName("GET /products should return sorted paged products when sort by name")
  public void findAllShouldReturnSortedPagedWhenSortByNameProducts() throws Exception {

    // Act
    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL + "?page=0&size=12&sort=name,asc").accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").value(totalProductsCount))
        .andExpect(jsonPath("$.content[0].name").value("Macbook Pro"))
        .andExpect(jsonPath("$.content[1].name").value("PC Gamer"))
        .andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"))
        .andExpect(jsonPath("$.number").isNumber())
        .andExpect(jsonPath("$.size").isNumber());
  }

  @Test
  @DisplayName("GET /products with pagination parameters should return paged products")
  public void findAllWithPaginationShouldReturnPagedProducts() throws Exception {

    // Act
    ResultActions resultActions = mockMvc.perform(get(BASE_URL)
        .param("page", "0")
        .param("linesPerPage", "10")
        .param("direction", "ASC")
        .param("orderBy", "name")
        .accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(10));
  }

  private String asJson(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }
}
