package com.example.supermarket.service;

import com.example.supermarket.dto.OrderDto;
import com.example.supermarket.dto.OrderItemDto;
import com.example.supermarket.entity.Customer;
import com.example.supermarket.entity.Order;
import com.example.supermarket.entity.Product;
import com.example.supermarket.exception.ResourceNotFoundException;
import com.example.supermarket.repository.CustomerRepository;
import com.example.supermarket.repository.OrderRepository;
import com.example.supermarket.repository.ProductRepository;
import com.example.supermarket.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Product product;
    private Customer customer;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Cola")
                .price(new BigDecimal("2.50"))
                .stock(10)
                .build();

        customer = Customer.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .build();
    }

    @Test
    void createOrder_success_shouldReturnOrderDtoAndDecrementStock() {
        OrderItemDto itemDto = OrderItemDto.builder().productId(1L).quantity(2).build();
        OrderDto dto = OrderDto.builder().customerId(1L).items(List.of(itemDto)).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        // When saving product after stock decrement
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // When saving order, return the same order with id and items
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            return o;
        });
        // Map returned Order to OrderDto
        when(mapper.map(any(Order.class), eq(OrderDto.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            return OrderDto.builder().id(o.getId()).customerId(o.getCustomer().getId()).totalAmount(o.getTotalAmount()).build();
        });

        OrderDto result = orderService.create(dto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        // stock should be decremented from 10 to 8
        assertEquals(8, product.getStock());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_insufficientStock_shouldThrow() {
        OrderItemDto itemDto = OrderItemDto.builder().productId(1L).quantity(20).build();
        OrderDto dto = OrderDto.builder().customerId(1L).items(List.of(itemDto)).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> orderService.create(dto));

        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_customerNotFound_shouldThrowResourceNotFound() {
        OrderItemDto itemDto = OrderItemDto.builder().productId(1L).quantity(1).build();
        OrderDto dto = OrderDto.builder().customerId(999L).items(List.of(itemDto)).build();

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.create(dto));
    }
}
