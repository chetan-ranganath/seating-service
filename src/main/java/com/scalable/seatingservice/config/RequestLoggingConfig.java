package com.scalable.seatingservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class RequestLoggingConfig extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Map<String, String> headers = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));

        Map<String, String[]> params = request.getParameterMap();

        String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        log.info("Incoming Request: {} {}\nHeaders: {}\nQuery Params: {}\nBody: {}",
                request.getMethod(),
                request.getRequestURI(),
                headers,
                params,
                requestBody);

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(request, wrappedResponse);

        String responseBody = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);

        log.info("Outgoing Response: {} {}\nBody: {}",
                response.getStatus(),
                request.getRequestURI(),
                responseBody);

        wrappedResponse.copyBodyToResponse();
    }
}
