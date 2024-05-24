package org.lager;

import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.OrderService;
import org.lager.service.ProductService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {
    private final CustomerService customerService;
    private final ProductService productService;
    private final BasketService basketService;
    private final OrderService orderService;

    public AppRunner(CustomerService customerService, ProductService productService, BasketService basketService, OrderService orderService) {
        this.customerService = customerService;
        this.productService = productService;
        this.basketService = basketService;
        this.orderService = orderService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(customerService.search(100_000_000L));
        System.out.println(productService.search(100_000_000L));
        System.out.println(basketService.getContentOfBasket(100_000_000L));
        System.out.println(orderService.getOrder(1000L));

        customerService.create("testUser");
        productService.create("testProduct");
        basketService.addToBasket(100_000_000L, 100_000_000L, 11);

        System.out.println(customerService.search(100_000_000L));
        System.out.println(productService.search(100_000_000L));
        System.out.println(basketService.getContentOfBasket(100_000_000L));
        System.out.println(orderService.getOrder(1000L));

        orderService.order(100000000L);

        System.out.println(customerService.search(100_000_000L));
        System.out.println(productService.search(100_000_000L));
        System.out.println(basketService.getContentOfBasket(100_000_000L));
        System.out.println(orderService.getOrder(1000L));
    }
}
