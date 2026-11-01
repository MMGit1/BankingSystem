
package ru.merkushev.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.merkushev.abstractions.repositories.Repositories;
import ru.merkushev.contracts.enums.GenderDto;
import ru.merkushev.contracts.enums.RoleDto;
import ru.merkushev.contracts.services.UserService;
import ru.merkushev.contracts.user.CreateUserRequest;

@Configuration
public class BootstrapAdminConfig {

    @Bean
    public ApplicationRunner bootstrapAdmin(
            UserService userService,
            Repositories repositories,
            @Value("${ADMIN_PASSWORD:}") String adminPassword
    ) {
        return args -> {

            if (repositories.getUserRepository()
                    .findByLogin("admin").isPresent()) {
                return;
            }

            if (adminPassword.isBlank()) {
                System.err.println(
                        "Admin not created: ADMIN_PASSWORD is not set"
                );
                return;
            }

            CreateUserRequest request = new CreateUserRequest();
            request.setLogin("admin");
            request.setPassword(adminPassword);
            request.setName("Administrator");
            request.setAge(0);
            request.setGender(GenderDto.MEN);
            request.setHairColor("admin");
            request.setRole(RoleDto.ADMIN);

            userService.createUser(request);
        };
    }
}
