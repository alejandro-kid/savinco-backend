package com.savinco.financial.web.config;

import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final HttpLoggingInterceptor httpLoggingInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor((org.springframework.web.servlet.HandlerInterceptor) httpLoggingInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/actuator/**",
                "/swagger-ui/**",
                "/api-docs/**",
                "/swagger-ui.html"
            );
    }
}
