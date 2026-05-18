package com.developersclub.nethackchatbot.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {
    @NotBlank(message="userId is required")
    private String userId;
    @NotBlank(message="message is required")
    private String message;
    
    private String promptType;

    public String getUserId(){
        return userId;
    }
    public String getPromptType(){
        return promptType;
    }
    public String getMessage(){
        return message;
    }
}
