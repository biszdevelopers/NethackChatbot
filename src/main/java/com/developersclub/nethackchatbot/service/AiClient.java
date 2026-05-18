package com.developersclub.nethackchatbot.service;

import com.developersclub.nethackchatbot.config.AiProperties;
import com.developersclub.nethackchatbot.model.ChatMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiClient {
    private final AiProperties props;
    private final RestClient.Builder restClientBuilder;
    private static final ObjectMapper mapper=new ObjectMapper();

    public String getCompletion(List<ChatMessage> messages){
        RestClient restClient=restClientBuilder
                .baseUrl(props.getUrl())
                .defaultHeader("Authorization", "Bearer "+props.getApiKey())
                .build();
        var requestBody=Map.of(
                "model", props.getModel(),
                "messages", messages,
                "temperature", 0.7,
                "thinking", Map.of("type", "disabled"),
                "stream", false
        );
        OpenAiChatResponse response=restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(OpenAiChatResponse.class);
        if (response!=null&&response.getChoices()!=null&&!response.getChoices().isEmpty()){
            return response.getChoices().get(0).getMessage().getContent();
        }
        return "Sorry, I couldn't generate a response.";
    }

    public Stream<String> getCompletionStream(List<ChatMessage> messages){
        RestClient restClient=restClientBuilder
                .baseUrl(props.getUrl())
                .defaultHeader("Authorization", "Bearer "+props.getApiKey())
                .build();
        var requestBody=Map.of(
                "model", props.getModel(),
                "messages", messages,
                "temperature", 0.7,
                "thinking", Map.of("type", "disabled"),
                "stream", true
        );
        var response=restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(java.io.InputStream.class);
        BufferedReader reader=new BufferedReader(
                new InputStreamReader(response, StandardCharsets.UTF_8)
        );
        var lines=reader.lines().onClose(()->{
            try{
                reader.close();
            }
            catch (Exception ignored){}
        });
        return lines
                .filter(line->line.startsWith("data: "))
                .map(line->line.substring(6))
                .filter(data->!"[DONE]".equals(data.trim()))
                .map(this::extractContent)
                .filter(content->content!=null&&!content.isEmpty())
                .onClose(lines::close);
    }

    private String extractContent(String json){
        try{
            JsonNode node=mapper.readTree(json);
            JsonNode delta=node.path("choices").get(0).path("delta");
            if (delta.has("content")){
                return delta.get("content").asText();
            }
            return null;
        }
        catch (Exception e){
            return null;
        }
    }

    @lombok.Data
    private static class OpenAiChatResponse {
        private List<Choice> choices;
        @lombok.Data
        static class Choice {
            private Message message;
        }
        @lombok.Data
        static class Message {
            private String content;
        }
    }
}