package ru.merkushev.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/logout").authenticated()

                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/users/me").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/users/me/friends").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.POST, "/users/me/friends/*").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/users/me/friends/*").hasRole("CLIENT")

                        .requestMatchers(HttpMethod.GET, "/accounts/me").hasRole("CLIENT")

                        .requestMatchers(HttpMethod.POST, "/operations/deposits").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.POST, "/operations/withdrawals").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.POST, "/operations/transfers").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/operations").hasAnyRole("ADMIN", "CLIENT")

                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/*/accounts").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/accounts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/accounts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/accounts/*/operations").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/accounts/*").hasAnyRole("ADMIN", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/accounts/*/balance").hasAnyRole("ADMIN", "CLIENT")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}