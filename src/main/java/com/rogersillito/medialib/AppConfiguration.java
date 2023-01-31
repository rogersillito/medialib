package com.rogersillito.medialib;

import com.rogersillito.medialib.common.InstantiationTracingBeanPostProcessor;
import org.jaudiotagger.audio.AudioFileFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.io.FileFilter;

@Configuration
@SuppressWarnings("unused")
public class AppConfiguration {
    @Bean()
    public FileFilter audioFileFilter() {
        return new AudioFileFilter();
    }

    @Bean()
    public InstantiationTracingBeanPostProcessor instantiationTracingBeanPostProcessor() {
        return new InstantiationTracingBeanPostProcessor();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/api/v1/directories", "/api/v1/directories/*")
                .hasRole("USER")
////              // allow any authenticated users
//                .authenticated()
//                // define a separate rule chain that would given open occess to URL /public/*
//                    .and()
//                .authorizeHttpRequests()
//                .requestMatchers("/api/v1/public/*")
//                .permitAll()
                    .and()
                .httpBasic()
                    .and()
                .csrf().disable()
        ;
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // basic user config for testing
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("medialibuser")
                        .password("password")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
}
