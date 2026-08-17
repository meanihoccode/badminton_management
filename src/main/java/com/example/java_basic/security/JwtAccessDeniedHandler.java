package com.example.java_basic.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.util.Locale;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public JwtAccessDeniedHandler(MessageSource messageSource, LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        Locale locale = localeResolver.resolveLocale(request);
        String error = messageSource.getMessage("sec.forbidden.error", null, "Forbidden", locale);
        String msg = messageSource.getMessage("sec.forbidden.message", null, "Access Denied", locale);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(String.format("{\"error\": \"%s\", \"message\": \"%s\"}", error, msg));
    }
}
