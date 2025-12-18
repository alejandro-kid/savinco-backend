package com.savinco.financial.web.config;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HttpLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingInterceptor.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_ID_MDC = "requestId";
    private static final String METHOD_MDC = "method";
    private static final String PATH_MDC = "path";
    private static final String STATUS_MDC = "status";

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
        
        // Store start time for duration calculation
        request.setAttribute("startTime", System.currentTimeMillis());
        
        // Generate or use existing request ID
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Set request ID in response header
        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        // Set MDC for structured logging
        MDC.put(REQUEST_ID_MDC, requestId);
        MDC.put(METHOD_MDC, request.getMethod());
        MDC.put(PATH_MDC, request.getRequestURI());
        
        // Log incoming request
        String queryString = request.getQueryString();
        String fullPath = queryString != null 
            ? request.getRequestURI() + "?" + queryString 
            : request.getRequestURI();
        
        log.info("Incoming HTTP request: {} {} | Client: {} | User-Agent: {}", 
            request.getMethod(), 
            fullPath,
            request.getRemoteAddr(),
            request.getHeader("User-Agent"));
        
        return true;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex) throws Exception {
        
        int status = response.getStatus();
        MDC.put(STATUS_MDC, String.valueOf(status));
        
        long startTime = (Long) request.getAttribute("startTime");
        long duration = startTime > 0 ? System.currentTimeMillis() - startTime : 0;
        
        if (ex != null) {
            log.error("HTTP request failed: {} {} | Status: {} | Duration: {}ms | Error: {}", 
                request.getMethod(),
                request.getRequestURI(),
                status,
                duration,
                ex.getMessage(),
                ex);
        } else if (status >= 400) {
            log.warn("HTTP request completed with error: {} {} | Status: {} | Duration: {}ms", 
                request.getMethod(),
                request.getRequestURI(),
                status,
                duration);
        } else {
            log.info("HTTP request completed: {} {} | Status: {} | Duration: {}ms", 
                request.getMethod(),
                request.getRequestURI(),
                status,
                duration);
        }
        
        // Clear MDC
        MDC.clear();
    }
}
