package com.developersclub.nethackchatbot.service;

import com.developersclub.nethackchatbot.dto.ChatRequest;
import com.developersclub.nethackchatbot.dto.ChatResponse;
import com.developersclub.nethackchatbot.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class NethackchatbotService {
    private final PromptBuilder promptBuilder;
    private final AiClient aiClient;
    private final RateLimiterService rateLimiter;

    public ChatResponse processMessage(ChatRequest request, String promptType){
        if (!rateLimiter.tryConsume(request.getUserId())){
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded. Please wait before sending more messages.");
        }
        List<ChatMessage> messages=promptBuilder.buildMessages(request.getMessage(), promptType);
        String aiReply=aiClient.getCompletion(messages);
        return new ChatResponse(aiReply);
    }

    public Stream<String> processMessageStream(ChatRequest request, String promptType){
        if (!rateLimiter.tryConsume(request.getUserId())){
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded. Please wait before sending more messages.");
        }
        List<ChatMessage> messages=promptBuilder.buildMessages(request.getMessage(), promptType);
        return aiClient.getCompletionStream(messages);
    }
}