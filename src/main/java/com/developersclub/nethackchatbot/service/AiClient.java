package com.developersclub.nethackchatbot.service;

import com.developersclub.nethackchatbot.config.AiProperties;
import com.developersclub.nethackchatbot.model.ChatMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiClient {
    private final AiProperties props;
    private final RestTemplate restTemplate;

    public String getCompletion(List<ChatMessage> messages){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(props.getApiKey());
        Map<String, Object> requestBody=Map.of(
            "model", props.getModel(),
            "messages", messages,
            "temperature", 0.7
        );
        HttpEntity<Map<String, Object>> request=new HttpEntity<>(requestBody, headers);
        try{
            OpenAiChatResponse response=restTemplate.postForObject(
                props.getUrl(),
                request,
                OpenAiChatResponse.class
            );
            if (response!=null&&response.getChoices()!=null&&!response.getChoices().isEmpty()){
                return response.getChoices().get(0).getMessage().getContent();
            }
            return "Sorry, I couldn't generate a response.";
        }
        catch (HttpClientErrorException e){
            throw new RuntimeException("AI service temporarily unavailable: "+e.getResponseBodyAsString());
        }
        catch (RestClientException e){
            throw new RuntimeException("AI service temporarily unavailable", e);
        }
    }
    @Data
    private static class OpenAiChatResponse{
        private List<Choice> choices;
        @Data
        static class Choice{
            private Message message;
        }
        @Data
        static class Message{
            private String content;
        }
    }
}