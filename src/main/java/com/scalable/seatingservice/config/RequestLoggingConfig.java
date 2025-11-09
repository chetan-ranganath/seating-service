package com.scalable.seatingservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestLoggingConfig extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logRequestResponse(wrappedRequest, wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequestResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        try {
            Map<String, String> headers = Collections.list(request.getHeaderNames())
                    .stream()
                    .collect(Collectors.toMap(h -> h, request::getHeader));

            String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);

            log.info("""
                    Incoming Request: {} {}
                    Headers: {}
                    Query Params: {}
                    Body: {}
                    """,
                    request.getMethod(),
                    request.getRequestURI(),
                    headers,
                    request.getParameterMap(),
                    requestBody);

            log.info("""
                    Outgoing Response: {} {}
                    Body: {}
                    """,
                    response.getStatus(),
                    request.getRequestURI(),
                    responseBody);
        } catch (Exception e) {
            log.error("Error logging request/response", e);
        }
    }
}
