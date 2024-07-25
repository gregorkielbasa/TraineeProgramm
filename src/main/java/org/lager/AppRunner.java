package org.lager;

import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.OrderService;
import org.lager.service.ProductService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;

//@Component
@Profile("!test")
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
        customerService.create("testUser");
        productService.create("testProduct1");
        productService.create("testProduct2");
        basketService.addToBasket(100_000_000, 100_000_000, 11);
        basketService.addToBasket(100_000_000, 100_000_001, 5);

        orderService.order(100000000L);
    }
}
