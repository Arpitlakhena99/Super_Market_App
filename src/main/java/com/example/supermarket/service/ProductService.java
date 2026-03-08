package com.example.supermarket.service;

import com.example.supermarket.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto create(ProductDto dto);

    ProductDto getById(Long id);

    List<ProductDto> getAll();

    ProductDto update(Long id, ProductDto dto);

    void delete(Long id);
}

