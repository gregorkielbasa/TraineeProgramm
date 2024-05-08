package org.lager;

import org.lager.repository.BasketRepository;
import org.lager.repository.CustomerRepository;
import org.lager.repository.OrderRepository;
import org.lager.repository.ProductRepository;
import org.lager.repository.sql.*;
import org.lager.repository.sql.functionalInterface.ConnectionSupplier;
import org.lager.repository.sql.functionalInterface.ConnectionSupplierImpl;
import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.OrderService;
import org.lager.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("postDB")
@PropertySource("classpath:application.properties")
public class PostgresAppConfig {

    @Value("${postgresql.url}")
    private String postgresqlUrl;
    @Value("${postgresql.user}")
    private String postgresqlUser;
    @Value("${postgresql.password}")
    private String postgresqlPassword;

    @Bean
    public BasketService basketService() {
        return new BasketService(basketRepository(), customerService(), productService());
    }

    @Bean
    public CustomerService customerService() {
        return new CustomerService(customerRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderService(orderRepository(), basketService());
    }

    @Bean
    public ProductService productService() {
        return new ProductService(productRepository());
    }


    @Bean
    public ConnectionSupplier connectionSupplier() {
        return new ConnectionSupplierImpl(postgresqlUrl, postgresqlUser, postgresqlPassword);
    }

    @Bean
    public SqlConnector sqlConnector() {
        return new SqlConnector(connectionSupplier());
    }


    @Bean
    public BasketRepository basketRepository() {
        return new BasketSqlRepository(basketMapper(), sqlConnector());
    }

    @Bean
    public BasketSqlMapper basketMapper() {
        return new BasketSqlMapper();
    }


    @Bean
    public CustomerRepository customerRepository() {
        return new CustomerSqlRepository(customerMapper(), sqlConnector());
    }

    @Bean
    public CustomerSqlMapper customerMapper() {
        return new CustomerSqlMapper();
    }


    @Bean
    public OrderRepository orderRepository() {
        return new OrderSqlRepository(orderMapper(), sqlConnector());
    }

    @Bean
    public OrderSqlMapper orderMapper() {
        return new OrderSqlMapper();
    }


    @Bean
    public ProductRepository productRepository() {
        return new ProductSqlRepository(productMapper(), sqlConnector());
    }

    @Bean
    public ProductSqlMapper productMapper() {
        return new ProductSqlMapper();
    }
}
