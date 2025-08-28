package com.eurocontrol.slots.config;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry reg) {
        reg.addMapping("/api/**")
           .allowedOrigins("http://localhost:5173")
           .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","PATCH")
           .allowedHeaders("*")
           .allowCredentials(false)
           .maxAge(3600);
      }
    };
  }
}