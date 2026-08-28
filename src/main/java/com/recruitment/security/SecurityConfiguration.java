package com.recruitment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth


                        .requestMatchers(
                                "/api/auth/login",
                                "/error",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()


                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register"
                        ).hasRole("ADMIN")


                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/applications/*/feedback"
                        ).hasAnyRole("INTERVIEWER", "ADMIN")


                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/candidates/**"
                        ).hasAnyRole("HR", "INTERVIEWER", "ADMIN")


                        .requestMatchers(
                                "/api/candidates/**"
                        ).hasAnyRole("HR", "ADMIN")


                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/**"
                        ).hasAnyRole("HR", "INTERVIEWER", "ADMIN")


                        .requestMatchers(
                                "/api/applications/**"
                        ).hasAnyRole("HR", "ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form.disable())

                .httpBasic(basic -> basic.disable())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}