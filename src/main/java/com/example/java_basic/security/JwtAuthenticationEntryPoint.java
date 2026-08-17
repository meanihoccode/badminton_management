package com.example.java_basic.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.util.Locale;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public JwtAuthenticationEntryPoint(MessageSource messageSource, LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        Locale locale = localeResolver.resolveLocale(request);
        String error = messageSource.getMessage("sec.unauthorized.error", null, "Unauthorized", locale);
        String msg = messageSource.getMessage("sec.unauthorized.message", null, "Please login", locale);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(String.format("{\"error\": \"%s\", \"message\": \"%s\"}", error, msg));
    }
}
