package org.lager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityFilterConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(requests -> {
            requests.requestMatchers(HttpMethod.GET, "/login").permitAll();
            requests.requestMatchers(HttpMethod.GET, "/product/**").permitAll();
            requests.requestMatchers("/user/**").hasRole("ADMIN");
            requests.anyRequest().authenticated();});
        return http.build();
    }
}
