package io.cartin.backend.Config;

import io.cartin.backend.CartinUser.Admin.Admin;
import io.cartin.backend.CartinUser.Customer.Customer;
import io.cartin.backend.CartinUser.Customer.Wallet;
import io.cartin.backend.CartinUser.CartinUser;
import io.cartin.backend.CartinUser.CartinUserRepository;
import io.cartin.backend.CartinUser.Manager.Manager;
import io.cartin.backend.CartinUser.Role;
import io.cartin.backend.Product.Category;
import io.cartin.backend.Product.Product;
import io.cartin.backend.Product.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@Configuration
public class ProductConfig {
@Bean
CommandLineRunner commandLineRunner(ProductRepository productRepository, CartinUserRepository cartinUserRepository) {
        return args -> {
        

        Product p1 = new Product().builder()
                .name("Apple")
                .price(55)
                .description("Fresh Apple")
                .quantity(10)
                .category(Category.Groceries)
                .offer(12.6)
                .deliveryTime(1)
                .build();

        productRepository.save(p1);

        Product p2 = new Product().builder()
                .name("Lays Chips")
                .price(20)
                .description("Big packet")
                .quantity(200)
                .category(Category.Food)
                .offer(0.0)
                .deliveryTime(1)
                .build();

        productRepository.save(p2);

        Product p3 = new Product().builder()
                .name("Nataraj Pencil Box")
                .price(100)
                .description("20 pencils")
                .quantity(100)
                .category(Category.Stationery)
                .offer(10.0)
                .deliveryTime(1)
                .build();

        productRepository.save(p3);

        Product p4 = new Product().builder()
                .name("Mixer Grinder")
                .price(199)
                .description("Mixer Grinder with 3 jars")
                .quantity(10)
                .category(Category.HomeAppliances)
                .offer(30.0)
                .deliveryTime(1)
                .build();   

        productRepository.save(p4);

        Product p5 = new Product().builder()
                        .name("Sri Sri Toothpaste")
                        .price(10)
                        .description("Fresh ayuevedic toothpaste")
                        .quantity(150)
                        .category(Category.Misc)
                        .offer(0.0)
                        .deliveryTime(1)
                        .build();

        productRepository.save(p5);
        
        final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CartinUser dummyAdmin = new CartinUser().builder()
                .firstName("Cartin")
                .lastName("Admin")
                .email("admin@cartin.io")
                .password(encoder.encode("admin"))
                .role(Role.ADMIN)
                .dob(LocalDate.of(2000, 1, 1))
                .customer(new Customer(new Wallet(0)))
                .admin(new Admin(true))
                .manager(new Manager(true, null))
                .phoneNumber("0000000000")
                .address("Placeholder Address")
                .enabled(true)
                .build();

                cartinUserRepository.save(dummyAdmin);
        };





}
}
