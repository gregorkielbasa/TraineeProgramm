package org.lager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchOrderException;
import org.lager.exception.OrderIllegalIdException;
import org.lager.exception.OrderItemSetNotPresentException;
import org.lager.model.dto.OrderDto;
import org.lager.security.SecurityFilterConfig;
import org.lager.service.OrderService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.lager.CustomerFixtures.defaultCustomerId;
import static org.lager.CustomerFixtures.incorrectCustomerId;
import static org.lager.OrderFixtures.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(SecurityFilterConfig.class)
@DisplayName("Order Controller")
@WithMockUser
class OrderControllerTest implements WithAssertions {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService service;

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
            String result = mockMvc.perform(get("/order"))
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
                    .thenReturn(List.of(defaultOrderId(), anotherOrderId()));
            String expected = "[" + defaultOrderId() + "," + anotherOrderId() + "]";

            //When
            String result = mockMvc.perform(get("/order"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expected))
                    .andReturn().getResponse().getContentAsString();

            //Then
            Mockito.verify(service).getAllIds();
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("post an order")
    class OrderBasket {

        @Test
        @DisplayName("of a default Basket")
        void properCase() throws Exception {
            //Given
            Mockito.when(service.order(anyLong()))
                    .thenReturn(new OrderDto(defaultOrder()));
            OrderDto expected = new OrderDto(defaultOrder());

            //When
            OrderDto result = OrderDtoOf(
                    mockMvc.perform(post("/order/{customerId}", defaultCustomerId()))
                            .andExpect(status().isAccepted())
                            .andReturn());

            //Then
            Mockito.verify(service).order(defaultCustomerId());
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("of non-existing Basket")
        void nonExistingBasket() throws Exception {
            //Given
            Mockito.when(service.order(anyLong()))
                    .thenThrow(OrderIllegalIdException.class);

            //When
            mockMvc.perform(post("/order/{customerId}", incorrectCustomerId()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).order(incorrectCustomerId());
        }

        @Test
        @DisplayName("of empty Basket")
        void emptyExistingBasket() throws Exception {
            //Given
            Mockito.when(service.order(anyLong()))
                    .thenThrow(OrderItemSetNotPresentException.class);

            //When
            mockMvc.perform(post("/order/{customerId}", incorrectCustomerId()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).order(incorrectCustomerId());
        }
    }

    @Nested
    @DisplayName("calls getOrder")
    class GetOrder {

        @Test
        @DisplayName("and gets an order")
        void properCase() throws Exception {
            //Given
            Mockito.when(service.get(anyLong()))
                    .thenReturn(new OrderDto(defaultOrder()));
            OrderDto expected = new OrderDto(defaultOrder());

            //When
            OrderDto result = OrderDtoOf(
                    mockMvc.perform(get("/order/{orderId}", defaultOrderId()))
                            .andExpect(status().isOk())
                            .andReturn());

            //Then
            Mockito.verify(service).get(defaultOrderId());
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("of non-existing Order")
        void nonExistingCase() throws Exception {
            //Given
            Mockito.when(service.get(anyLong()))
                    .thenThrow(NoSuchOrderException.class);

            //When
            mockMvc.perform(get("/order/{orderId}", incorrectCustomerId()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).get(incorrectCustomerId());
        }
    }

    private OrderDto OrderDtoOf(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, OrderDto.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e);
        }
    }
}