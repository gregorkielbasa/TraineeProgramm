package org.lager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchBasketException;
import org.lager.exception.NoSuchCustomerException;
import org.lager.exception.NoSuchProductException;
import org.lager.model.dto.BasketDto;
import org.lager.service.BasketService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;

import static org.lager.BasketFixtures.defaultBasket;
import static org.lager.CustomerFixtures.*;
import static org.lager.ProductFixtures.defaultProductId;
import static org.lager.ProductFixtures.incorrectProductId;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasketController.class)
@DisplayName("Basket Controller")
class BasketControllerTest implements WithAssertions {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasketService service;

    @Nested
    @DisplayName("calls getAllIds")
    class getAllIds {

        @Test
        @DisplayName("and gets an empty list, when DB is empty")
        void emptyDB() throws Exception {
            //Given
            Mockito.when(service.getAllIds()).thenReturn(List.of());
            String expected = "[]";

            //When
            String result = mockMvc.perform(get("/basket"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            //Then
            Mockito.verify(service).getAllIds();
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and gets a list of few IDs")
        void nonEmptyDEB() throws Exception {
            //Given
            Mockito.when(service.getAllIds())
                    .thenReturn(List.of(defaultCustomerId(), anotherCustomerId()));
            String expected = "[" + defaultCustomerId() + "," + anotherCustomerId() + "]";

            //When
            String result = mockMvc.perform(get("/basket"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expected))
                    .andReturn().getResponse().getContentAsString();

            //Then
            Mockito.verify(service).getAllIds();
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("calls get")
    class getBasket {

        @Test
        @DisplayName("and gets a basket")
        void properCase() throws Exception {
            //Given
            Mockito.when(service.get(anyLong()))
                    .thenReturn(new BasketDto(defaultBasket()));
            BasketDto expected = new BasketDto(defaultBasket());

            //When
            BasketDto result = basketDtoOf(
                    mockMvc.perform(get("/basket/{customerId}", defaultCustomerId()))
                            .andExpect(status().isOk())
                            .andReturn());

            //Then
            Mockito.verify(service).get(defaultCustomerId());
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and throws an Exception when basket doesn't exists")
        void nonExisting() throws Exception {
            //Given
            Mockito.when(service.get(anyLong()))
                    .thenReturn(new BasketDto(0, Set.of()));
            BasketDto expected = new BasketDto(0, Set.of());

            //When
            BasketDto result = basketDtoOf(
                    mockMvc.perform(get("/basket/{customerId}", defaultCustomerId()))
                            .andExpect(status().isOk())
                            .andReturn());

            //Then
            Mockito.verify(service).get(defaultCustomerId());
            assertThat(result).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("deletes Basket")
    void deleteBasket() throws Exception {
        //Given
        Mockito.doNothing().when(service).dropBasket(anyLong());

        //When
        mockMvc.perform(delete("/basket/{customerId}", defaultCustomerId()))
                .andExpect(status().isAccepted())
                .andReturn();

        //Then
        Mockito.verify(service).dropBasket(defaultCustomerId());
    }

    @Test
    @DisplayName("deletes Item")
    void deleteItem() throws Exception {
        //Given
        Mockito.when(service.removeFromBasket(anyLong(), anyLong()))
                .thenReturn(new BasketDto(defaultBasket()));
        BasketDto expected = new BasketDto(defaultBasket());

        //When
        BasketDto result = basketDtoOf(
        mockMvc.perform(delete("/basket/{customerId}/{productId}", defaultCustomerId(), defaultProductId()))
                .andExpect(status().isAccepted())
                .andReturn());

        //Then
        Mockito.verify(service).removeFromBasket(defaultCustomerId(), defaultProductId());
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("deletes Item of non-existing Basket")
    void deleteItemNonExistingBasket() throws Exception {
        //Given
        Mockito.when(service.removeFromBasket(anyLong(), anyLong()))
                .thenThrow(NoSuchBasketException.class);

        //When
        mockMvc.perform(delete("/basket/{customerId}/{productId}", incorrectCustomerId(), defaultProductId()))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        Mockito.verify(service).removeFromBasket(incorrectCustomerId(), defaultProductId());
    }

    private BasketDto basketDtoOf(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, BasketDto.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e);
        }
    }

    @Nested
    @DisplayName("posts addToBasket")
    class createCustomer {

        @Test
        @DisplayName("and adds one product to a basket")
        void oneProduct() throws Exception {
            //Given
            Mockito.when(service.addToBasket(anyLong(), anyLong(), anyInt()))
                    .thenReturn(new BasketDto(defaultBasket()));
            BasketDto expected = new BasketDto(defaultBasket());

            //When
            BasketDto result = basketDtoOf(
                    mockMvc.perform(post("/basket/{customerId}/{productId}", defaultCustomerId(), defaultProductId()))
                            .andExpect(status().isAccepted())
                            .andReturn());

            //Then
            Mockito.verify(service).addToBasket(defaultCustomerId(), defaultProductId(), 1);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and adds more products to a basket")
        void moreProducts() throws Exception {
            //Given
            Mockito.when(service.addToBasket(anyLong(), anyLong(), anyInt()))
                    .thenReturn(new BasketDto(defaultBasket()));
            BasketDto expected = new BasketDto(defaultBasket());

            //When
            BasketDto result = basketDtoOf(
                    mockMvc.perform(post("/basket/{customerId}/{productId}/{amount}", defaultCustomerId(), defaultProductId(), 123))
                            .andExpect(status().isAccepted())
                            .andReturn());

            //Then
            Mockito.verify(service).addToBasket(defaultCustomerId(), defaultProductId(), 123);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and adds non-existing product to a basket")
        void nonExistingProduct() throws Exception {
            //Given
            Mockito.when(service.addToBasket(anyLong(), anyLong(), anyInt()))
                    .thenThrow(NoSuchProductException.class);

            //When
            mockMvc.perform(post("/basket/{customerId}/{productId}/{amount}", defaultCustomerId(), incorrectProductId(), 123))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).addToBasket(defaultCustomerId(), incorrectProductId(), 123);
        }

        @Test
        @DisplayName("and adds a product to a non-existing-customer's basket")
        void nonExistingCustomer() throws Exception {
            //Given
            Mockito.when(service.addToBasket(anyLong(), anyLong(), anyInt()))
                    .thenThrow(NoSuchCustomerException.class);

            //When
            mockMvc.perform(post("/basket/{customerId}/{productId}/{amount}", incorrectCustomerId(), defaultProductId(), 123))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).addToBasket(incorrectCustomerId(), defaultProductId(), 123);
        }
    }
}