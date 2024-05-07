package org.lager;

import org.lager.repository.BasketRepository;
import org.lager.repository.CustomerRepository;
import org.lager.repository.OrderRepository;
import org.lager.repository.ProductRepository;
import org.lager.repository.csv.*;
import org.lager.repository.json.JsonEditor;
import org.lager.repository.json.OrderJsonMapper;
import org.lager.repository.json.OrderJsonRepository;
import org.lager.repository.xml.BasketXmlMapper;
import org.lager.repository.xml.BasketXmlRepository;
import org.lager.repository.xml.XmlEditor;
import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.OrderService;
import org.lager.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

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
        return new BasketXmlRepository(basketXmlEditor(), basketXmlMapper());
    }

    @Bean
    public XmlEditor basketXmlEditor() {
        return new XmlEditor("filePath");
    }

    @Bean
    public BasketXmlMapper basketXmlMapper() {
        return new BasketXmlMapper();
    }


    @Bean
    public CustomerRepository customerRepository() {
        return new CustomerCsvRepository(customerCsvEditor(), customerCsvMapper());
    }

    @Bean
    public CsvEditor customerCsvEditor() {
        return new CsvEditor("filePath", "fileHeader");
    }

    @Bean
    public CustomerCsvMapper customerCsvMapper() {
        return new CustomerCsvMapper();
    }


    @Bean
    public OrderRepository orderRepository() {
        return new OrderJsonRepository(orderJsonEditor(), orderJsonMapper());
    }

    @Bean
    public JsonEditor orderJsonEditor() {
        return new JsonEditor("filePath");
    }

    @Bean
    public OrderJsonMapper orderJsonMapper() {
        return new OrderJsonMapper();
    }


    @Bean
    public ProductRepository productRepository() {
        return new ProductCsvRepository(productCsvEditor(), productCsvMapper());
    }

    @Bean
    public CsvEditor productCsvEditor() {
        return new CsvEditor("filePath", "fileHeader");
    }

    @Bean
    public ProductCsvMapper productCsvMapper() {
        return new ProductCsvMapper();
    }
}
