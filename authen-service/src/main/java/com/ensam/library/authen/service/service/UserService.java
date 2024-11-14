package com.ensam.library.authen.service.service;

import com.ensam.library.authen.service.model.User;
import com.ensam.library.authen.service.model.Role;
import com.ensam.library.authen.service.repository.UserRepository;
import com.ensam.library.authen.service.repository.RoleRepository;
import com.ensam.library.authen.service.dto.LoginRequest;
import com.ensam.library.authen.service.dto.RegisterRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public User register(RegisterRequest registerRequest) {
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role role = roleRepository.findByName(registerRequest.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found" + registerRequest.getRole()));
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public User  login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        return userRepository.findByEmail(loginRequest.getEmail());
    }
}
