package org.lager;

import org.lager.repository.BasketRepository;
import org.lager.repository.CustomerRepository;
import org.lager.repository.OrderRepository;
import org.lager.repository.ProductRepository;
import org.lager.repository.sql.*;
import org.lager.repository.sql.ConnectionSupplier;
import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.OrderService;
import org.lager.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@SpringBootConfiguration
@Profile("database")
@PropertySource("classpath:application.properties")
public class DatabaseAppConfig {

    @Value("${database.url}")
    private String databaseUrl;
    @Value("${database.user}")
    private String databaseUser;
    @Value("${database.password}")
    private String databasePassword;


    @Bean
    public ConnectionSupplier connectionSupplier() {
        return new ConnectionSupplier(databaseUrl, databaseUser, databasePassword);
    }
}
