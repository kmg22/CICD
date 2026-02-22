package com.kmg22.cicd.interceptor;

import com.kmg22.cicd.filter.CachingFilter.CachedBodyRequestWrapper;
import com.kmg22.cicd.filter.CachingFilter.CachedBodyResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long duration = System.currentTimeMillis() - (Long) request.getAttribute("startTime");

        String requestBody = "";
        String responseBody = "";

        if (request instanceof CachedBodyRequestWrapper wrapper) {
            requestBody = new String(wrapper.getCachedBody(), StandardCharsets.UTF_8);
        }

        if (response instanceof CachedBodyResponseWrapper wrapper) {
            responseBody = new String(wrapper.getCachedBody(), StandardCharsets.UTF_8);
        }

        log.info("\n[REQUEST]  {} {} {}ms\n  body: {}\n[RESPONSE] {}\n  body: {}",
            request.getMethod(),
            request.getRequestURI(),
            duration,
            requestBody.isBlank() ? "(없음)" : requestBody,
            response.getStatus(),
            responseBody.isBlank() ? "(없음)" : responseBody
        );
    }
}