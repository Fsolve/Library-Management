package com.ensam.library.authen.service.config;

import com.ensam.library.authen.service.model.Role;
import com.ensam.library.authen.service.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RoleInitializer implements CommandLineRunner{
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
    }
}
