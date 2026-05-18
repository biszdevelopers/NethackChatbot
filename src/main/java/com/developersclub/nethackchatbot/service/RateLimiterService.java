package com.developersclub.nethackchatbot.service;

import com.developersclub.nethackchatbot.config.ChatProperties;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private final ConcurrentHashMap<String, TokenBucket> userBuckets=new ConcurrentHashMap<>();
    private final ChatProperties.RateLimit config;

    public RateLimiterService(ChatProperties chatProperties){
        this.config=chatProperties.getRateLimit();
    }
    public boolean tryConsume(String userId){
        TokenBucket bucket=userBuckets.computeIfAbsent(userId, this::createBucket);
        return bucket.tryConsume();
    }
    private TokenBucket createBucket(String key){
        return new TokenBucket(config.getCapacity(), config.getRefillTokens(), Duration.ofSeconds(config.getRefillSeconds()));
    }

    private static class TokenBucket{
        private final int capacity;
        private final int refillTokens;
        private final Duration refillPeriod;
        private int tokens;
        private Instant lastRefill;
        TokenBucket(int capacity, int refillTokens, Duration refillPeriod){
            this.capacity=capacity;
            this.refillTokens=refillTokens;
            this.refillPeriod=refillPeriod;
            this.tokens=capacity;
            this.lastRefill=Instant.now();
        }
        synchronized boolean tryConsume(){
            refillTokensIfNeeded();
            if (tokens>0){
                tokens--;
                return true;
            }
            return false;
        }
        private void refillTokensIfNeeded(){
            Instant now=Instant.now();
            long periodsElapsed=Duration.between(lastRefill, now).dividedBy(refillPeriod);
            if (periodsElapsed>0){
                tokens=Math.min(capacity, tokens+(int) periodsElapsed*refillTokens);
                lastRefill=now;
            }
        }
    }
}