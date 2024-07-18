package org.lager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.UserExistsAlreadyException;
import org.lager.exception.UserIllegalLoginException;
import org.lager.exception.UserIllegalPasswordException;
import org.lager.model.dto.UserDto;
import org.lager.security.JwtTokenProvider;
import org.lager.security.SecurityFilterConfig;
import org.lager.service.UserService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityFilterConfig.class)
@DisplayName("User Controller")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;
    @MockBean
    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void init () {
        Mockito.when(tokenProvider.getUser(anyString()))
                .thenReturn(Optional.empty());
    }

    @Nested
    @DisplayName("creates a user")
    class CreateTest {

        @Test
        @DisplayName("but is unauthorised")
        void unauthorised() throws Exception {
            //Given
            UserDto userDto = new UserDto("validLogin", "validPassword");

            //When
            mockMvc.perform(post("/user"))
                    .andExpect(status().isUnauthorized());

            //Then
        }

        @Test
        @DisplayName("but is forbidden")
        @WithMockUser
        void forbidden() throws Exception {
            //Given
            UserDto userDto = new UserDto("validLogin", "validPassword");

            //When
            mockMvc.perform(post("/user"))
                    .andExpect(status().isForbidden());

            //Then
        }

        @Test
        @DisplayName("and creates a new user")
        @WithMockUser(roles = {"ADMIN"})
        void properCase() throws Exception {
            //Given
            Mockito.doNothing().when(service).createUser(any(), any());
            UserDto userDto = new UserDto("validLogin", "validPassword");

            //When
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"login\":\"" + userDto.login() + "\", \"password\":\"" + userDto.password() + "\"}"))
                    .andExpect(status().isCreated());

            //Then
            Mockito.verify(service).createUser(userDto.login(), userDto.password());
        }

        @Test
        @DisplayName("and throws exception when login is incorrect")
        @WithMockUser(roles = {"ADMIN"})
        void incorrectLogin() throws Exception {
            //Given
            Mockito.doThrow(UserIllegalLoginException.class).
                    when(service).createUser(any(), any());
            UserDto userDto = new UserDto("invalidLogin", "validPassword");

            //When
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"login\":\"" + userDto.login() + "\", \"password\":\"" + userDto.password() + "\"}"))
                    .andExpect(status().isBadRequest());

            //Then
            Mockito.verify(service).createUser(userDto.login(), userDto.password());
        }

        @Test
        @DisplayName("and throws exception when password is incorrect")
        @WithMockUser(roles = {"ADMIN"})
        void incorrectPassword() throws Exception {
            //Given
            Mockito.doThrow(UserIllegalPasswordException.class).
                    when(service).createUser(any(), any());
            UserDto userDto = new UserDto("validLogin", "invalidPassword");

            //When
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"login\":\"" + userDto.login() + "\", \"password\":\"" + userDto.password() + "\"}"))
                    .andExpect(status().isBadRequest());

            //Then
            Mockito.verify(service).createUser(userDto.login(), userDto.password());
        }

        @Test
        @DisplayName("and throws exception when user already exists")
        @WithMockUser(roles = {"ADMIN"})
        void userAlreadyExists() throws Exception {
            //Given
            Mockito.doThrow(UserExistsAlreadyException.class).
                    when(service).createUser(any(), any());
            UserDto userDto = new UserDto("validLogin", "validPassword");

            //When
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"login\":\"" + userDto.login() + "\", \"password\":\"" + userDto.password() + "\"}"))
                    .andExpect(status().isConflict());

            //Then
            Mockito.verify(service).createUser(userDto.login(), userDto.password());
        }
    }

    @Nested
    @DisplayName("deletes a user")
    class DeleteTest {

        @Test
        @DisplayName("but is unauthorised")
        void unauthorised() throws Exception {
            //Given
            UserDto userDto = new UserDto("validLogin", "");

            //When
            mockMvc.perform(delete("/user"))
                    .andExpect(status().isUnauthorized());

            //Then
        }

        @Test
        @DisplayName("but is forbidden")
        @WithMockUser
        void forbidden() throws Exception {
            //Given
            UserDto userDto = new UserDto("validLogin", "");

            //When
            mockMvc.perform(delete("/user"))
                    .andExpect(status().isForbidden());

            //Then
        }

        @Test
        @DisplayName("and deletes an user")
        @WithMockUser(roles = {"ADMIN"})
        void properCase() throws Exception {
            //Given
            Mockito.doNothing().when(service).deleteUser(any());
            UserDto userDto = new UserDto("validLogin", "");

            //When
            mockMvc.perform(delete("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"login\":\"" + userDto.login() + "\", \"password\":\"" + userDto.password() + "\"}"))
                    .andExpect(status().isAccepted());

            //Then
            Mockito.verify(service).deleteUser(userDto.login());
        }

        @Test
        @DisplayName("and throws exception when login is incorrect")
        @WithMockUser(roles = {"ADMIN"})
        void incorrectLogin() throws Exception {
            //Given
            Mockito.doThrow(UserIllegalLoginException.class).
                    when(service).deleteUser(any());
            UserDto userDto = new UserDto("invalidLogin", "");

            //When
            mockMvc.perform(delete("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"login\":\"" + userDto.login() + "\", \"password\":\"" + userDto.password() + "\"}"))
                    .andExpect(status().isBadRequest());

            //Then
            Mockito.verify(service).deleteUser(userDto.login());
        }
    }
}