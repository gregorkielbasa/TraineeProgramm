package org.lager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityFilterConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf. disable());
        http.authorizeHttpRequests((requests) -> {
            requests.requestMatchers("/user/**").hasRole("ADMIN");
            requests.anyRequest().hasAnyRole("USER", "ADMIN");});
        //            requests.anyRequest().permitAll();});
        http.httpBasic(withDefaults());
        return http.build();
    }
}
