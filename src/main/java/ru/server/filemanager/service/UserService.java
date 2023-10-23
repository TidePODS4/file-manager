package ru.server.filemanager.service;

import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UUID getUserIdBySecurityContext(SecurityContext securityContext);
}
