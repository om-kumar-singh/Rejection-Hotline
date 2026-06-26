package com.om.rejectionhotline.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.google")
@Getter
@Setter
public class GoogleProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
