package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.server.filemanager.model.User;
import ru.server.filemanager.repository.UserRepository;
import ru.server.filemanager.service.UserService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UUID getUserIdBySecurityContext(SecurityContext securityContext) {
        Jwt principal = (Jwt) securityContext
                .getAuthentication()
                .getPrincipal();

        return UUID.fromString(principal.getSubject());
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }
}
