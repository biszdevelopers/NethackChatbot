package com.developersclub.nethackchatbot.service;

import com.developersclub.nethackchatbot.model.ChatMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PromptBuilder {
    private static final Logger log=LoggerFactory.getLogger(PromptBuilder.class);
    private static final String PROMPTS_DIRECTORY="classpath:static/nethack-assistants/prompts/*.md";
    private final ResourcePatternResolver resourceResolver;
    private final Map<String, String> prompts=new LinkedHashMap<>();

    public PromptBuilder(ResourcePatternResolver resourceResolver){
        this.resourceResolver=resourceResolver;
    }
    @PostConstruct
    public void loadPrompts(){
        try{
            Resource[] resources=resourceResolver.getResources(PROMPTS_DIRECTORY);
            log.info("Found {} prompt files", resources.length);
            for (Resource resource:resources){
                String filename=resource.getFilename();
                if (filename!=null&&filename.endsWith(".md")){
                    String promptType=filename.substring(0, filename.length() - 3);
                    String content=FileCopyUtils.copyToString(
                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
                    ).trim();
                    prompts.put(promptType, content);
                    log.info("Loaded prompt type '{}'", promptType);
                }
            }
        }
        catch (Exception e){
            log.error("Failed to load prompt files", e);
        }
    }
    public List<ChatMessage> buildMessages(String userMessage, String promptType){
        String systemPrompt=prompts.get(promptType);
        if (systemPrompt==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prompt type '"+promptType+"' not available");
        }
        return List.of(
            new ChatMessage("system", systemPrompt),
            new ChatMessage("user", userMessage)
        );
    }

    public List<String> getAvailablePromptTypes(){
        return new ArrayList<>(prompts.keySet());
    }
}