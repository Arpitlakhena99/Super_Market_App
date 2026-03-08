package com.example.supermarket.service;

import com.example.supermarket.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto create(OrderDto dto);

    OrderDto getById(Long id);

    List<OrderDto> getAll();

    OrderDto update(Long id, OrderDto dto);

    void delete(Long id);
}

