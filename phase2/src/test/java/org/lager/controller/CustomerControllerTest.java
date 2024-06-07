package org.lager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchCustomerException;
import org.lager.exception.CustomerIllegalIdException;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.model.dto.CustomerDto;
import org.lager.service.CustomerService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.lager.CustomerFixtures.*;
import static org.lager.CustomerFixtures.defaultCustomerId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@DisplayName("CustomerController")
class CustomerControllerTest implements WithAssertions {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CustomerService service;

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
            String result = mockMvc.perform(get("/customer"))
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
            String result = mockMvc.perform(get("/customer"))
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
    class createCustomer {

        @Test
        @DisplayName("and creates a new customer")
        void properCase() throws Exception {
            //Given
            Mockito.when(service.create(any()))
                    .thenReturn(new CustomerDto(defaultCustomer()));
            CustomerDto expected = new CustomerDto(defaultCustomer());

            //When
            CustomerDto result = customerDtoOf(
                    mockMvc.perform(post("/customer/{newCustomerName}", defaultCustomerName()))
                            .andExpect(status().isCreated())
                            .andReturn());

            //Then
            Mockito.verify(service).create(defaultCustomerName());
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and throws an Exception when name is incorrect")
        void incorrectName() throws Exception {
            //Given
            Mockito.when(service.create(any()))
                    .thenThrow(CustomerIllegalNameException.class);

            //When
//            Exception result =
            mockMvc.perform(post("/customer/{newCustomerName}", defaultCustomerName()))
                    .andExpect(status().isBadRequest())
                    .andReturn();
//                    .getResolvedException();

            //Then
            Mockito.verify(service).create(defaultCustomerName());
//            assertThat(result).isEqualTo(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("and throws an Exception when ID is incorrect")
        void incorrectId() throws Exception {
            //Given
            Mockito.when(service.create(any()))
                    .thenThrow(CustomerIllegalIdException.class);

            //When
            mockMvc.perform(post("/customer/{newCustomerName}", defaultCustomerName()))
                    .andExpect(status().isInternalServerError())
                    .andReturn();

            //Then
            Mockito.verify(service).create(defaultCustomerName());
        }
    }

    @Nested
    @DisplayName("calls get")
    class getCustomer {

        @Test
        @DisplayName("and gets a customer")
        void properCase() throws Exception {
            //Given
            Mockito.when(service.get(anyLong()))
                    .thenReturn(new CustomerDto(defaultCustomer()));
            CustomerDto expected = new CustomerDto(defaultCustomer());

            //When
            CustomerDto result = customerDtoOf(
                    mockMvc.perform(get("/customer/{customerId}", defaultCustomerId()))
                            .andExpect(status().isOk())
                            .andReturn());

            //Then
            Mockito.verify(service).get(defaultCustomerId());
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and throws an Exception when customer doesn't exists")
        void nonExisting() throws Exception {
            //Given
            Mockito.when(service.get(anyLong()))
                    .thenThrow(NoSuchCustomerException.class);

            //When
            mockMvc.perform(get("/customer/{customerId}", defaultCustomerId()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).get(defaultCustomerId());
        }
    }

    @Nested
    @DisplayName("posts rename")
    class renameCustomer {

        @Test
        @DisplayName("and renames a customer")
        void properCase() throws Exception {
            //Given
            Mockito.when(service.rename(anyLong(), any()))
                    .thenReturn(new CustomerDto(defaultCustomer()));
            CustomerDto expected = new CustomerDto(defaultCustomer());

            //When
            CustomerDto result = customerDtoOf(
                    mockMvc.perform(post("/customer/{customerId}/{customerNewName}", defaultCustomerId(), defaultCustomerName()))
                            .andExpect(status().isAccepted())
                            .andReturn());

            //Then
            Mockito.verify(service).rename(defaultCustomerId(), defaultCustomerName());
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("and throws an Exception when customer doesn't exists")
        void nonExisting() throws Exception {
            //Given
            Mockito.when(service.rename(anyLong(), any()))
                    .thenThrow(NoSuchCustomerException.class);

            //When
            mockMvc.perform(post("/customer/{customerId}/{customerNewName}", defaultCustomerId(), defaultCustomerName()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).rename(defaultCustomerId(), defaultCustomerName());
        }

        @Test
        @DisplayName("and throws an Exception when new Name is incorrect")
        void incorrectName() throws Exception {
            //Given
            Mockito.when(service.rename(anyLong(), any()))
                    .thenThrow(CustomerIllegalNameException.class);

            //When
            mockMvc.perform(post("/customer/{customerId}/{customerNewName}", defaultCustomerId(), defaultCustomerName()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //Then
            Mockito.verify(service).rename(defaultCustomerId(), defaultCustomerName());
        }
    }

    @Test
    @DisplayName("deletes")
    void deleteCustomer() throws Exception {
        //Given
        Mockito.doNothing().when(service).delete(anyLong());

        //When
        mockMvc.perform(delete("/customer/{customerId}", defaultCustomerId()))
                .andExpect(status().isAccepted())
                .andReturn();

        //Then
        Mockito.verify(service).delete(defaultCustomerId());
    }

    private static CustomerDto customerDtoOf(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, CustomerDto.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e);
        }
    }
}