package ru.server.filemanager.service.imp;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import ru.server.filemanager.service.UserService;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {

    @Override
    public UUID getUserIdBySecurityContext(SecurityContext securityContext) {
        OidcUser principal = (OidcUser) securityContext
                .getAuthentication()
                .getPrincipal();

        return UUID.fromString(principal.getSubject());
    }
}
