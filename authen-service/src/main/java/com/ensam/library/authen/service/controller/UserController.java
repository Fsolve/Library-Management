package com.ensam.library.authen.service.controller;

import com.ensam.library.authen.service.dto.RegisterRequest;
import com.ensam.library.authen.service.dto.LoginRequest;
import com.ensam.library.authen.service.dto.LoginResponse;
import com.ensam.library.authen.service.dto.UserDto;
import com.ensam.library.authen.service.model.User;
import com.ensam.library.authen.service.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest registerRequest) {
        User registeredUser = userService.register(registerRequest);
        User retrievedUser = userRepository.findByEmail(registeredUser.getEmail());
        return ResponseEntity.ok(toUserDto(retrievedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        User authenticatedUser = userService.login(loginRequest);
        String jwtToken = jwtTokenProvider.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(toUserDto(currentUser));
    }


    public UserDto toUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRoles().stream().findFirst().get().getName());
        return userDto;
    }
}
