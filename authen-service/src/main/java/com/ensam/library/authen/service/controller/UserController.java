package com.ensam.library.authen.service.controller;

import com.ensam.library.authen.service.dto.UserDto;
import com.ensam.library.authen.service.dto.RegisterRequest;
import com.ensam.library.authen.service.dto.LoginRequest;
import com.ensam.library.authen.service.dto.LoginResponse;
import com.ensam.library.authen.service.model.User;
import com.ensam.library.authen.service.service.UserService;
import com.ensam.library.authen.service.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest registerRequest) {
        User registeredUser = userService.register(registerRequest);

        UserDto userDto = new UserDto();
        userDto.setId(registeredUser.getId());
        userDto.setEmail(registeredUser.getEmail());
        userDto.setName(registeredUser.getName());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        User authenticatedUser = userService.login(loginRequest);

        String jwtToken = jwtTokenProvider.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtTokenProvider.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        UserDto userDto = new UserDto();
        userDto.setId(currentUser.getId());
        userDto.setEmail(currentUser.getEmail());
        userDto.setName(currentUser.getName());
        return ResponseEntity.ok(userDto);
    }
}
