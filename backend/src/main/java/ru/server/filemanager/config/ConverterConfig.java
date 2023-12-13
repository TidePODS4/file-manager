package ru.server.filemanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class ConverterConfig {
    @Bean
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter(){
        return new JwtGrantedAuthoritiesConverter();
    }
}
