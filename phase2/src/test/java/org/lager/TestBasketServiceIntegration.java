package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.ProductService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.lager.BasketFixtures.*;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@TestPropertySource(locations = "classpath:integrationtest.properties")
@ActiveProfiles("database")
class TestBasketServiceIntegration implements WithAssertions {

    @Autowired
    BasketService service;
    @MockBean
    ProductService productService;
    @MockBean
    CustomerService customerService;

    @Test
    void testBasketServiceOperations() {
        Mockito.doNothing().when(productService).validatePresence(anyLong());
        Mockito.doNothing().when(customerService).validatePresence(anyLong());

        assertThat(service.getContentOfBasket(defaultCustomerId()))
                .isEmpty();
        service.addToBasket(defaultCustomerId(), defaultProductId(), 1);
        assertThat(service.getContentOfBasket(defaultCustomerId()))
                .containsExactlyInAnyOrderEntriesOf(defaultBasket().getContent());

        service.removeFromBasket(defaultCustomerId(), defaultProductId());
        assertThat(service.getContentOfBasket(defaultCustomerId()))
                .containsExactlyInAnyOrderEntriesOf(defaultEmptyBasket().getContent());

        service.dropBasket(defaultCustomerId());
        assertThat(service.getContentOfBasket(defaultCustomerId()))
                .isEmpty();
    }
}