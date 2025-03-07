package com.example.zexus_music_streaming.controller;

import com.example.zexus_music_streaming.model.Role;
import com.example.zexus_music_streaming.model.User;
import com.example.zexus_music_streaming.service.UserService;
import com.example.zexus_music_streaming.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody JsonNode userJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.treeToValue(userJson, User.class);

            if (userService.userExists(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Username already exists"));
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Handling roles properly
            Set<Role> roles = new HashSet<>();
            if (userJson.has("roles") && userJson.get("roles").isArray()) {
                for (JsonNode roleNameNode : userJson.get("roles")) {
                    String roleName = roleNameNode.asText();
                    Role role = userService.findRoleByName(roleName);
                    if (role == null) {
                        role = new Role();
                        role.setName(roleName);
                        role = userService.saveRole(role); // Save role before use
                    }
                    roles.add(role);
                }
            } else {
                Role userRole = userService.findRoleByName("USER");
                if (userRole == null) {
                    userRole = new Role();
                    userRole.setName("USER");
                    userRole = userService.saveRole(userRole); // Save role before use
                }
                roles.add(userRole);
            }
            user.setRoles(roles);

            userService.registerUser(user);
            String token = jwtUtil.generateToken(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User registered successfully", "token", token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            String token = jwtUtil.generateToken(userService.findUserByUsername(user.getUsername()));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }
}
