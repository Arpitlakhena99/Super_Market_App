package com.example.supermarket.controller;

import com.example.supermarket.dto.ProductDto;
import com.example.supermarket.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @SuppressWarnings("unused")
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("unused")
    @MockBean
    private ProductService productService;

    private ProductDto dto;

    @BeforeEach
    void setup() {
        dto = ProductDto.builder().id(1L).name("P").sku("S").price(new BigDecimal("1.0")).stock(10).build();
    }

    @Test
    @WithMockUser
    void getAll_shouldReturnOk() throws Exception {
        when(productService.getAll()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/products")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnCreated() throws Exception {
        when(productService.create(any())).thenReturn(dto);
        String body = "{\"name\":\"P\",\"sku\":\"S\",\"price\":1.0,\"stock\":10}";
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }
}
