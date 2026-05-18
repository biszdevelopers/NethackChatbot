package com.developersclub.nethackchatbot.controller;

import com.developersclub.nethackchatbot.dto.ChatRequest;
import com.developersclub.nethackchatbot.dto.ChatResponse;
import com.developersclub.nethackchatbot.service.NethackchatbotService;
import com.developersclub.nethackchatbot.service.PromptBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final NethackchatbotService chatBotService;
    private final PromptBuilder promptBuilder;

    @GetMapping("/prompts")
    public ResponseEntity<List<String>> getAvailablePrompts(){
        return ResponseEntity.ok(promptBuilder.getAvailablePromptTypes());
    }

    @PostMapping("/chat/{promptType}")
    public ResponseEntity<ChatResponse> chat(@PathVariable String promptType, @Valid @RequestBody ChatRequest request){
        ChatResponse response=chatBotService.processMessage(request, promptType);
        return ResponseEntity.ok(response);
    }
}