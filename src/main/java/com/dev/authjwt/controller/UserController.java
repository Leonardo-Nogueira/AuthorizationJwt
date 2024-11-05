package com.dev.authjwt.controller;

import com.dev.authjwt.domain.User;
import com.dev.authjwt.domain.dto.UserLoginRequestDTO;
import com.dev.authjwt.domain.dto.UserRegisterRequestDTO;
import com.dev.authjwt.domain.dto.UserResponseDTO;
import com.dev.authjwt.repository.UserRepository;
import com.dev.authjwt.service.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserLoginRequestDTO body){
        User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new UserResponseDTO(user.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserRegisterRequestDTO body){
        Optional<User> user = this.repository.findByEmail(body.email());

        if(user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new UserResponseDTO(newUser.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/user")
    public ResponseEntity<String> getUser(){
        return ResponseEntity.ok("sucesso!");
    }
}
