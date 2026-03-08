package com.example.supermarket.service;

import com.example.supermarket.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto dto);

    CategoryDto getById(Long id);

    List<CategoryDto> getAll();

    CategoryDto update(Long id, CategoryDto dto);

    void delete(Long id);
}

