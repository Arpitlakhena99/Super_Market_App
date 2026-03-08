package com.example.supermarket.service.impl;

import com.example.supermarket.dto.OrderDto;
import com.example.supermarket.entity.Customer;
import com.example.supermarket.entity.Order;
import com.example.supermarket.entity.OrderItem;
import com.example.supermarket.entity.Product;
import com.example.supermarket.exception.ResourceNotFoundException;
import com.example.supermarket.repository.CustomerRepository;
import com.example.supermarket.repository.OrderRepository;
import com.example.supermarket.repository.ProductRepository;
import com.example.supermarket.service.OrderService;
import com.example.supermarket.singleton.LoggerManager;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final Logger log = LoggerManager.getInstance().getLogger();

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper mapper;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, CustomerRepository customerRepository, ModelMapper mapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public OrderDto create(OrderDto dto) {
        log.info("Creating order for customer id: {}", dto.getCustomerId());
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(dto.getStatus() != null ? dto.getStatus() : "CREATED");

        List<OrderItem> items = dto.getItems().stream().map(i -> {
            Product product = productRepository.findById(i.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + i.getProductId()));
            if (product.getStock() < i.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product id: " + i.getProductId());
            }
            product.setStock(product.getStock() - i.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(i.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);
        BigDecimal total = items.stream().map(it -> it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return mapper.map(saved, OrderDto.class);
    }

    @Override
    public OrderDto getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapper.map(order, OrderDto.class);
    }

    @Override
    public List<OrderDto> getAll() {
        return orderRepository.findAll().stream().map(o -> mapper.map(o, OrderDto.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto update(Long id, OrderDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(dto.getStatus());
        // For simplicity, do not modify items in update
        Order saved = orderRepository.save(order);
        return mapper.map(saved, OrderDto.class);
    }

    @Override
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        orderRepository.delete(order);
        log.info("Deleted order with id: {}", id);
    }
}
