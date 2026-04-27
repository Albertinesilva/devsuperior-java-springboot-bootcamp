package com.albertsilva.dev.dscatalog.web.controllers;

import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.EXISTING_ID;
import static com.albertsilva.dev.dscatalog.factory.CategoryFactory.NON_EXISTING_ID;
import static org.mockito.ArgumentMatchers.any;
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

import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.factory.CategoryFactory;
import com.albertsilva.dev.dscatalog.services.CategoryService;
import com.albertsilva.dev.dscatalog.web.exceptions.advice.ControllerExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CategoryController.class)
@Import(ControllerExceptionHandler.class)
@DisplayName("Tests for CategoryController")
public class CategoryControllerTest {

  private static final String BASE_URL = "/api/v1/categories";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CategoryService categoryService;

  private Page<CategoryResponse> page;
  private CategoryResponse categoryResponse;

  @BeforeEach
  void setUp() {

    categoryResponse = CategoryFactory.createCategoryResponse();

    page = new PageImpl<>(List.of(categoryResponse), PageRequest.of(0, 10), 1);
  }

  @Test
  @DisplayName("GET /categories should return paged categories")
  void findAllShouldReturnPage() throws Exception {

    when(categoryService.findAllPaged(any(Pageable.class))).thenReturn(page);

    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON));

    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].id").value(EXISTING_ID))
        .andExpect(jsonPath("$.content[0].name").value(categoryResponse.name()))
        .andExpect(jsonPath("$.totalElements").value(1));

    verify(categoryService).findAllPaged(any(Pageable.class));
  }

  @Test
  @DisplayName("GET /categories/{id} should return category when id exists")
  void findByIdShouldReturnCategoryWhenIdExists() throws Exception {

    when(categoryService.findById(EXISTING_ID)).thenReturn(categoryResponse);

    ResultActions resultActions = mockMvc
        .perform(get(BASE_URL + "/{id}", EXISTING_ID).accept(MediaType.APPLICATION_JSON));

    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(EXISTING_ID))
        .andExpect(jsonPath("$.name").value(categoryResponse.name()));

    verify(categoryService).findById(EXISTING_ID);
  }

}
