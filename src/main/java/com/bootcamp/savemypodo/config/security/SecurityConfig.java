package com.bootcamp.savemypodo.config.security;

import com.bootcamp.savemypodo.config.jwt.JwtAuthenticationFilter;
import com.bootcamp.savemypodo.service.auth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Value("${frontend.url}")
    private String frontendUrl;

    private final String[] allowUrls = {
            "/", "/favicon.ico", "/actuator/**", "/api/auth/**", "/login/**", "/error", "/oauth/**"
    };

    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring().requestMatchers(allowUrls);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           OAuth2SuccessHandler oAuth2SuccessHandler,
                                           OAuth2FailureHandler oAuth2FailureHandler,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        log.info("[SecurityConfig]: 진입");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/api/musicals", "/actuator/prometheus", "/api/user/me").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**", "/api/reservations/**", "/api/musicals/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .logout(logout -> logout.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ Cors 설정 분리 - Credentials + Exposed Headers 등 명시
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "social_access_token"
        ));
        configuration.setExposedHeaders(List.of(
                "Authorization",
                "Set-Cookie"
        ));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}