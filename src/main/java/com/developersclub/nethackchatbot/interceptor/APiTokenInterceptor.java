package com.developersclub.nethackchatbot.interceptor;

import com.developersclub.nethackchatbot.config.ChatProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ApiTokenInterceptor implements HandlerInterceptor {
    private final ChatProperties chatProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String token=request.getHeader("X-API-Token");
        if (token==null||!token.equals(chatProperties.getApiToken())){
            response.setStatus(401);
            response.getWriter().write("Unauthorized: Invalid or missing X-API-Token header");
            return false;
        }
        return true;
    }
}