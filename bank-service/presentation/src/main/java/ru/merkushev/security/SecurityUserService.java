package ru.merkushev.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.merkushev.contracts.auth.AuthenticatedUserData;
import ru.merkushev.contracts.services.AuthenticationService;
import ru.merkushev.exception.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService {
    private final AuthenticationService authenticationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthenticatedUserData userData;
        try {
            userData = authenticationService.getByLogin(username);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException("User not found", e);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(userData.getLogin())
                .password(userData.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + userData.getRole())))
                .build();
    }
}
