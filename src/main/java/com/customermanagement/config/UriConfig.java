package com.customermanagement.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("uri.prefix")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UriConfig {

    private String customer;
}
