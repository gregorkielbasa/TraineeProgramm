package org.lager;

import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.OrderService;
import org.lager.service.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

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
