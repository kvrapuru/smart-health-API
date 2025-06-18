package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("PATCH") || request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            logger.info("=== Incoming Request Details ===");
            logger.info("Method: {}", request.getMethod());
            logger.info("URL: {}", request.getRequestURL());
            logger.info("Content-Type: {}", request.getContentType());
            
            // Log headers
            logger.info("=== Request Headers ===");
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                logger.info("{}: {}", headerName, request.getHeader(headerName));
            });

            // Only log request body for ContentCachingRequestWrapper to avoid consuming the stream
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    String requestBody = new String(buf, StandardCharsets.UTF_8);
                    logger.info("=== Request Body ===");
                    logger.info(requestBody);
                }
            }
        }
        return true;
    }
} 