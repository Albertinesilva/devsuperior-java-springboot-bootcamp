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

import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("CategoryController Integration Tests")
public class CategoryControllerIT {

  private static final String BASE_URL = "/api/v1/categories";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CategoryRepository categoryRepository;

  private long totalCategoriesCount;

  @BeforeEach
  void setUp() {
    totalCategoriesCount = categoryRepository.count();
  }

  @Test
  @DisplayName("GET /categories should return sorted paged categories when sort by name")
  public void findAllShouldReturnSortedPagedWhenSortByNameCategories() throws Exception {

    // Act
    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL + "?page=0&size=12&sort=name,asc").accept(MediaType.APPLICATION_JSON));

    // Assert
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").isNumber())
        .andExpect(jsonPath("$.totalElements").value(totalCategoriesCount))
        .andExpect(jsonPath("$.content[0].name").value("Automotive"))
        .andExpect(jsonPath("$.content[1].name").value("Beauty"))
        .andExpect(jsonPath("$.content[2].name").value("Books"))
        .andExpect(jsonPath("$.number").isNumber())
        .andExpect(jsonPath("$.size").isNumber());
  }

  @Test
  @DisplayName("GET /categories with pagination parameters should return paged categories")
  public void findAllWithPaginationShouldReturnPagedCategories() throws Exception {

    // Act
    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL)
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

}
