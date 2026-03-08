package com.example.supermarket.service;

import com.example.supermarket.dto.ProductDto;
import com.example.supermarket.entity.Product;
import com.example.supermarket.exception.ResourceNotFoundException;
import com.example.supermarket.repository.ProductRepository;
import com.example.supermarket.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDto dto;
    private Product product;

    @BeforeEach
    void setUp() {
        dto = ProductDto.builder()
                .name("Test Product")
                .sku("TP-001")
                .price(new BigDecimal("9.99"))
                .stock(10)
                .categoryId(null)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .sku("TP-001")
                .price(new BigDecimal("9.99"))
                .stock(10)
                .build();
    }

    @Test
    void create_shouldReturnCreatedDto() {
        when(mapper.map(dto, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(mapper.map(product, ProductDto.class)).thenReturn(ProductDto.builder().id(1L).name("Test Product").sku("TP-001").price(new BigDecimal("9.99")).stock(10).build());

        ProductDto result = productService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void getById_whenNotFound_shouldThrow() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(1L));
    }
}
