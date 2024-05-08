package org.lager;

import org.lager.repository.BasketRepository;
import org.lager.repository.CustomerRepository;
import org.lager.repository.OrderRepository;
import org.lager.repository.ProductRepository;
import org.lager.repository.csv.*;
import org.lager.repository.json.*;
import org.lager.repository.xml.*;
import org.lager.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("fileDB")
@PropertySource("classpath:application.properties")
public class FileAppConfig {

    @Value("${basket.xml.file.path}")
    private String basketXmlFilePath;

    @Value("${customer.csv.file.path}")
    private String customerCsvFilePath;
    @Value("${customer.csv.file.header}")
    private String customerCsvFileHeader;

    @Value("${order.json.file.path}")
    private String orderJsonFilePath;

    @Value("${product.csv.file.path}")
    private String productCsvFilePath;
    @Value("${product.csv.file.header}")
    private String productCsvFileHeader;


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
    public BasketRepository basketRepository() {
        return new BasketXmlRepository(basketXmlEditor(), basketMapper());
    }

    @Bean
    public XmlEditor basketXmlEditor() {
        return new XmlEditor(basketXmlFilePath);
    }

    @Bean
    public BasketXmlMapper basketMapper() {
        return new BasketXmlMapper();
    }


    @Bean
    public CustomerRepository customerRepository() {
        return new CustomerCsvRepository(customerCsvEditor(), customerMapper());
    }

    @Bean
    public CsvEditor customerCsvEditor() {
        return new CsvEditor(customerCsvFilePath, customerCsvFileHeader);
    }

    @Bean
    public CustomerCsvMapper customerMapper() {
        return new CustomerCsvMapper();
    }


    @Bean
    public OrderRepository orderRepository() {
        return new OrderJsonRepository(orderJsonEditor(), orderMapper());
    }

    @Bean
    public JsonEditor orderJsonEditor() {
        return new JsonEditor(orderJsonFilePath);
    }

    @Bean
    public OrderJsonMapper orderMapper() {
        return new OrderJsonMapper();
    }


    @Bean
    public ProductRepository productRepository() {
        return new ProductCsvRepository(productCsvEditor(), productMapper());
    }

    @Bean
    public CsvEditor productCsvEditor() {
        return new CsvEditor(productCsvFilePath, productCsvFileHeader);
    }

    @Bean
    public ProductCsvMapper productMapper() {
        return new ProductCsvMapper();
    }
}
