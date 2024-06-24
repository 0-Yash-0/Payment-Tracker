package com.expensetracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.expensetracker.service.UserService;

@Configuration
public class SecurityConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserService userService){
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http
            , AuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception{
        http.authorizeHttpRequests(config ->
                config
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/showRegistrationForm").permitAll()
                        .requestMatchers("/processRegistration").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form ->
                        form
                                .loginPage("/showLoginPage")
                                .loginProcessingUrl("/authenticateTheUser")
                                .successHandler(customAuthenticationSuccessHandler)
                                .permitAll())
                .logout(logout ->
                        logout
                                .permitAll()
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/showLoginPage"));
        return http.build();
    }

}