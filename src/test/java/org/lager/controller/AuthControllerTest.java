package org.lager.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lager.security.JwtTokenProvider;
import org.lager.security.SecurityFilterConfig;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityFilterConfig.class)
@DisplayName("Authentication Controller")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @Test
    @DisplayName("logs in when user authenticates")
    public void properLogins() throws Exception {
        //Given
        String login = "user";
        String password = "password";
        String token = "token";
        Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);


        Mockito.when(authenticationManager.authenticate(any()))
                .thenReturn(null);
        Mockito.when(tokenProvider.generateToken(any())).thenReturn(token);

        //When
        mockMvc.perform(get("/login")
                        .contentType("application/json")
                        .content("{\"login\":\"" + login + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        //Then
        Mockito.verify(authenticationManager).authenticate(authentication);
        Mockito.verify(tokenProvider).generateToken(login);
    }

    @Test
    public void login_ShouldReturnForbidden_WhenAuthenticationFails() throws Exception {
        //Given
        String login = "user";
        String password = "wrong-password";
        Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);

        Mockito.when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationException("error"){});

        //When
        mockMvc.perform(get("/login")
                        .contentType("application/json")
                        .content("{\"login\":\"" + login + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isForbidden());

        //Then
        Mockito.verify(authenticationManager).authenticate(authentication);
    }
}