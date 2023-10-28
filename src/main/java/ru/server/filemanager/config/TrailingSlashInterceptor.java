package ru.server.filemanager.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TrailingSlashInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
            throws Exception {
        String requestURL = request.getRequestURL().toString();
        if (requestURL.endsWith("/")) {
            response.sendRedirect(requestURL.substring(0, requestURL.length() - 1));
            return false;
        }
        return true;
    }
}
