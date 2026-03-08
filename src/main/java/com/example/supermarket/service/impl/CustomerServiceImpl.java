package com.example.supermarket.service.impl;

import com.example.supermarket.dto.CustomerDto;
import com.example.supermarket.entity.Customer;
import com.example.supermarket.exception.ResourceNotFoundException;
import com.example.supermarket.repository.CustomerRepository;
import com.example.supermarket.service.CustomerService;
import com.example.supermarket.singleton.LoggerManager;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final Logger log = LoggerManager.getInstance().getLogger();

    private final CustomerRepository customerRepository;
    private final ModelMapper mapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper mapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Override
    public CustomerDto create(CustomerDto dto) {
        log.info("Creating customer: {} {}", dto.getFirstName(), dto.getLastName());
        Customer customer = mapper.map(dto, Customer.class);
        Customer saved = customerRepository.save(customer);
        return mapper.map(saved, CustomerDto.class);
    }

    @Override
    public CustomerDto getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapper.map(customer, CustomerDto.class);
    }

    @Override
    public List<CustomerDto> getAll() {
        // Return customers ordered by first name then last name (ascending)
        return customerRepository.findAll(Sort.by(Sort.Direction.ASC, "firstName").and(Sort.by(Sort.Direction.ASC, "lastName"))).stream()
                .map(c -> mapper.map(c, CustomerDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDto update(Long id, CustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        Customer saved = customerRepository.save(customer);
        return mapper.map(saved, CustomerDto.class);
    }

    @Override
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
        log.info("Deleted customer with id: {}", id);
    }
}
