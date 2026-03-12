package com.example.supermarket.config;

import com.example.supermarket.entity.Category;
import com.example.supermarket.entity.Customer;
import com.example.supermarket.entity.Product;
import com.example.supermarket.repository.CategoryRepository;
import com.example.supermarket.repository.CustomerRepository;
import com.example.supermarket.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds a minimal dataset on application startup when the database is empty.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository, CustomerRepository customerRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default categories and products only once.
        if (categoryRepository.count() == 0) {
            Category beverages = Category.builder().name("Beverages").description("Drinks and beverages").build();
            Category snacks = Category.builder().name("Snacks").description("Chips and snacks").build();
            categoryRepository.save(beverages);
            categoryRepository.save(snacks);

            Product p1 = Product.builder().name("Cola").sku("COLA-001").description("Cold drink").price(new BigDecimal("1.50")).stock(100).category(beverages).build();
            Product p2 = Product.builder().name("Potato Chips").sku("CHIP-001").description("Crispy chips").price(new BigDecimal("2.00")).stock(50).category(snacks).build();
            productRepository.save(p1);
            productRepository.save(p2);
        }

        // Add a sample customer if no customers exist.
        if (customerRepository.count() == 0) {
            Customer c1 = Customer.builder().firstName("John").lastName("Doe").email("john.doe@example.com").phone("1234567890").address("123 Main St").build();
            customerRepository.save(c1);
        }
    }
}
