package com.example.zexus_music_streaming.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.zexus_music_streaming.util.RoleDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonDeserialize(using = RoleDeserializer.class) // ✅ Custom deserializer
    private Set<Role> roles;
}
