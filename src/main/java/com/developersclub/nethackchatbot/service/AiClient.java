package com.developersclub.nethackchatbot.service;

import com.developersclub.nethackchatbot.config.AiProperties;
import com.developersclub.nethackchatbot.model.ChatMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiClient {

    private final AiProperties props;
    private final RestClient.Builder restClientBuilder;

    public String getCompletion(List<ChatMessage> messages) {
        RestClient restClient = restClientBuilder
                .baseUrl(props.getUrl())
                .defaultHeader("Authorization", "Bearer " + props.getApiKey())
                .build();

        var requestBody = Map.of(
                "model", props.getModel(),
                "messages", messages,
                "temperature", 0.5
        );

        try {
            OpenAiChatResponse response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(OpenAiChatResponse.class);

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            return "Sorry, I couldn't generate a response.";
        } catch (RestClientException e) {
            throw new RuntimeException("AI service temporarily unavailable", e);
        }
    }

    @Data
    private static class OpenAiChatResponse {
        private List<Choice> choices;

        @Data
        static class Choice {
            private Message message;
        }

        @Data
        static class Message {
            private String content;
        }
    }
}