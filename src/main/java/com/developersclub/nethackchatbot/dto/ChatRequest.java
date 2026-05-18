package com.developersclub.nethackchatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message="userId is required")
    private String userId;

    @NotBlank(message="message is required")
    private String message;

    public String getUserId(){
        return userId;
    }
    public String getMessage(){
        return message;
    }
}