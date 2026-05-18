package com.developersclub.nethackchatbot.controller;

import com.developersclub.nethackchatbot.dto.ChatRequest;
import com.developersclub.nethackchatbot.dto.ChatResponse;
import com.developersclub.nethackchatbot.service.NethackchatbotService;
import com.developersclub.nethackchatbot.service.PromptBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final NethackchatbotService chatBotService;
    private final PromptBuilder promptBuilder;
    private final ExecutorService executor=Executors.newCachedThreadPool();

    @GetMapping("/prompts")
    public ResponseEntity<List<String>> getAvailablePrompts(){
        return ResponseEntity.ok(promptBuilder.getAvailablePromptTypes());
    }

    @PostMapping("/chat/{promptType}")
    public ResponseEntity<ChatResponse> chat(
            @PathVariable String promptType,
            @Valid @RequestBody ChatRequest request){
        ChatResponse response=chatBotService.processMessage(request, promptType);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value="/chat/{promptType}/stream", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(
            @PathVariable String promptType,
            @Valid @RequestBody ChatRequest request){
        SseEmitter emitter=new SseEmitter(0L);
        executor.execute(()->{
            try{
                Stream<String> tokenStream=chatBotService.processMessageStream(request, promptType);
                tokenStream.forEach(token->{
                    try{
                        emitter.send(SseEmitter.event().data(token));
                    }
                    catch (IOException e){
                        emitter.completeWithError(e);
                    }
                });
                emitter.complete();
            }
            catch (Exception e){
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}