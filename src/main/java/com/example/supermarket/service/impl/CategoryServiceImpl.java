package com.example.supermarket.service.impl;

import com.example.supermarket.dto.CategoryDto;
import com.example.supermarket.entity.Category;
import com.example.supermarket.exception.ResourceNotFoundException;
import com.example.supermarket.repository.CategoryRepository;
import com.example.supermarket.service.CategoryService;
import com.example.supermarket.singleton.LoggerManager;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final Logger log = LoggerManager.getInstance().getLogger();

    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        log.info("Creating category: {}", dto.getName());
        Category category = mapper.map(dto, Category.class);
        Category saved = categoryRepository.save(category);
        return mapper.map(saved, CategoryDto.class);
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> getAll() {
        // Return categories ordered by name (ascending)
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(c -> mapper.map(c, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        Category saved = categoryRepository.save(category);
        return mapper.map(saved, CategoryDto.class);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
        log.info("Deleted category with id: {}", id);
    }
}
