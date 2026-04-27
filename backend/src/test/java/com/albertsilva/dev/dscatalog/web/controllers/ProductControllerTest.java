package com.albertsilva.dev.dscatalog.web.controllers;


import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import com.albertsilva.dev.dscatalog.web.exceptions.advice.ControllerExceptionHandler;

@WebMvcTest(ProductController.class)
@Import(ControllerExceptionHandler.class)
@DisplayName("Tests for ProductController")
class ProductControllerTest {

 
}