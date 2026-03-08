package com.example.supermarket.service.impl;

import com.example.supermarket.dto.ProductDto;
import com.example.supermarket.entity.Category;
import com.example.supermarket.entity.Product;
import com.example.supermarket.exception.ResourceNotFoundException;
import com.example.supermarket.repository.CategoryRepository;
import com.example.supermarket.repository.ProductRepository;
import com.example.supermarket.service.ProductService;
import com.example.supermarket.singleton.LoggerManager;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final Logger log = LoggerManager.getInstance().getLogger();

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper mapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto dto) {
        log.info("Creating product: {}", dto.getName());
        Product product = mapper.map(dto, Product.class);
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        }
        Product saved = productRepository.save(product);
        return mapper.map(saved, ProductDto.class);
    }

    @Override
    public ProductDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> getAll() {
        // Return products ordered by name (ascending)
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(p -> mapper.map(p, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        Product saved = productRepository.save(product);
        return mapper.map(saved, ProductDto.class);
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
        log.info("Deleted product with id: {}", id);
    }
}
