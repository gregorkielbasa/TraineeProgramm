package org.lager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchProductException;
import org.lager.exception.ProductIllegalIdException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.model.dto.ProductDto;
import org.lager.service.ProductService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.lager.ProductFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController")
class ProductControllerTest implements WithAssertions {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

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
            String result = mockMvc.perform(get("/product"))
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
                    .thenReturn(List.of(defaultProductId(), anotherProductId()));
            String expected = "[" + defaultProductId() + "," + anotherProductId() + "]";

            //When
            String result = mockMvc.perform(get("/product"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expected))
                    .andReturn().getResponse().getContentAsString();

            //Then
            Mockito.verify(service).getAllIds();
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("posts create")
    class createProduct {

        @Test
        @DisplayName("and creates a new product")
        void properCase() throws Exception {
            //Given
            Mockito.when(service.create(any()))
                    .thenReturn(new ProductDto(defaultProduct()));
            ProductDto expected = new ProductDto(defaultProduct());

            //When
            ProductDto result = productDtoOf(
                    mockMvc.perform(post("/product/{newProductName}", defaultProductName()))
                            .andExpect(status().isCreated())
                            .andReturn());

            //Then
            Mockito.verify(service).create(defaultProductName());
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and throws an Exception when name is incorrect")
        void incorrectName() throws Exception {
            //Given
            Mockito.when(service.create(any()))
                    .thenThrow(ProductIllegalNameException.class);

            //When
//            Exception result =
            mockMvc.perform(post("/product/{newProductName}", defaultProductName()))
                    .andExpect(status().isBadRequest())
                    .andReturn();
//                    .getResolvedException();

            //Then
            Mockito.verify(service).create(defaultProductName());
//            assertThat(result).isEqualTo(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("and throws an Exception when ID is incorrect")
        void incorrectId() throws Exception {
            //Given
            Mockito.when(service.create(any()))
                    .thenThrow(ProductIllegalIdException.class);

            //When
            mockMvc.perform(post("/product/{newProductName}", defaultProductName()))
                    .andExpect(status().isInternalServerError())
                    .andReturn();

            //Then
            Mockito.verify(service).create(defaultProductName());
        }
    }

    @Nested
    @DisplayName("calls get")
    class getProduct {

        @Test
        @DisplayName("and gets a product")
        void properCase() throws Exception {
            //Given
            Mockito.when(service.get(anyLong()))
                    .thenReturn(new ProductDto(defaultProduct()));
            ProductDto expected = new ProductDto(defaultProduct());

            //When
            ProductDto result = productDtoOf(
                    mockMvc.perform(get("/product/{productId}", defaultProductId()))
                            .andExpect(status().isOk())
                            .andReturn());

            //Then
            Mockito.verify(service).get(defaultProductId());
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and throws an Exception when product doesn't exists")
        void nonExisting() throws Exception {
            //Given
            Mockito.when(service.get(anyLong()))
                    .thenThrow(NoSuchProductException.class);

            //When
            mockMvc.perform(get("/product/{productId}", defaultProductId()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).get(defaultProductId());
        }
    }

    @Nested
    @DisplayName("posts rename")
    class renameProduct {

        @Test
        @DisplayName("and renames a product")
        void properCase() throws Exception {
            //Given
            Mockito.when(service.rename(anyLong(), any()))
                    .thenReturn(new ProductDto(defaultProduct()));
            ProductDto expected = new ProductDto(defaultProduct());

            //When
            ProductDto result = productDtoOf(
                    mockMvc.perform(post("/product/{productId}/{productNewName}", defaultProductId(), defaultProductName()))
                            .andExpect(status().isAccepted())
                            .andReturn());

            //Then
            Mockito.verify(service).rename(defaultProductId(), defaultProductName());
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and throws an Exception when product doesn't exists")
        void nonExisting() throws Exception {
            //Given
            Mockito.when(service.rename(anyLong(), any()))
                    .thenThrow(NoSuchProductException.class);

            //When
            mockMvc.perform(post("/product/{productId}/{productNewName}", defaultProductId(), defaultProductName()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).rename(defaultProductId(), defaultProductName());
        }

        @Test
        @DisplayName("and throws an Exception when new Name is incorrect")
        void incorrectName() throws Exception {
            //Given
            Mockito.when(service.rename(anyLong(), any()))
                    .thenThrow(ProductIllegalNameException.class);

            //When
            mockMvc.perform(post("/product/{productId}/{productNewName}", defaultProductId(), defaultProductName()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).rename(defaultProductId(), defaultProductName());
        }
    }

    @Test
    @DisplayName("deletes")
    void deleteProduct() throws Exception {
        //Given
        Mockito.doNothing().when(service).delete(anyLong());

        //When
        mockMvc.perform(delete("/product/{productId}", defaultProductId()))
                .andExpect(status().isAccepted())
                .andReturn();

        //Then
        Mockito.verify(service).delete(defaultProductId());
    }

    private static ProductDto productDtoOf(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, ProductDto.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e);
        }
    }
}