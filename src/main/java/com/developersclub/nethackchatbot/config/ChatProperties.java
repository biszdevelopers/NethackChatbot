package com.developersclub.nethackchatbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="chat")
public class ChatProperties {
    public String apiToken;
    public RateLimit rateLimit;
    
    @Data
    public static class RateLimit{
        private int capacity;
        private int refillTokens;
        private int refillSeconds;
    }
}
