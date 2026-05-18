package com.developersclub.nethackchatbot.controller;

import com.developersclub.nethackchatbot.dto.ChatRequest;
import com.developersclub.nethackchatbot.dto.ChatResponse;
import com.developersclub.nethackchatbot.service.NethackchatotService;
import com.developersclub.nethackchatbot.service.PromptBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final NethackchatotService chatBotService;
    private final PromptBuilder promptBuilder;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request){
        ChatResponse response=chatBotService.processMessage(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/prompts")
    public ResponseEntity<List<String>> getAvailablePrompts(){
        return ResponseEntity.ok(promptBuilder.getAvailablePromptTypes());
    }
}