package org.lager;

import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.OrderService;
import org.lager.service.ProductService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        ApplicationContext context = new AnnotationConfigApplicationContext(FileAppConfig.class, DatabaseAppConfig.class);

        CustomerService customerService = context.getBean(CustomerService.class);
        ProductService productService = context.getBean(ProductService.class);
        BasketService basketService = context.getBean(BasketService.class);
        OrderService orderService = context.getBean(OrderService.class);

        customerService.create("testUser");
        productService.create("testProduct");

        basketService.addToBasket(100000000L, 100000000L, 100);

        orderService.order(100000000L);
    }
}
