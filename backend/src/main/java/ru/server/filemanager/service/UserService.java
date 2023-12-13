package ru.server.filemanager.service;

import org.springframework.security.core.context.SecurityContext;
import ru.server.filemanager.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UUID getUserIdBySecurityContext(SecurityContext securityContext);
    Optional<User> getUserById(UUID id);
    User addUser(UUID id);
}
