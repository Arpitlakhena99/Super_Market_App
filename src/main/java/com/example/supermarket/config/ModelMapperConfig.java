package com.example.supermarket.config;

import com.example.supermarket.dto.OrderDto;
import com.example.supermarket.dto.OrderItemDto;
import com.example.supermarket.dto.ProductDto;
import com.example.supermarket.entity.Order;
import com.example.supermarket.entity.OrderItem;
import com.example.supermarket.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Map Product.category.id -> ProductDto.categoryId
        mapper.typeMap(Product.class, ProductDto.class).addMappings(m ->
                m.map(src -> src.getCategory() != null ? src.getCategory().getId() : null,
                        ProductDto::setCategoryId)
        );

        // Map OrderItem.product.id -> OrderItemDto.productId
        mapper.typeMap(OrderItem.class, OrderItemDto.class).addMappings(m ->
                m.map(src -> src.getProduct() != null ? src.getProduct().getId() : null,
                        OrderItemDto::setProductId)
        );

        // Map Order.customer.id -> OrderDto.customerId
        mapper.typeMap(Order.class, OrderDto.class).addMappings(m ->
                m.map(src -> src.getCustomer() != null ? src.getCustomer().getId() : null,
                        OrderDto::setCustomerId)
        );

        return mapper;
    }
}
