package com.developersclub.nethackchatbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="ai.openai")
public class AiProperties {
    private String apiKey;
    private String model;
    private String url;
}
