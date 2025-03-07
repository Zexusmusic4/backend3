package com.example.zexus_music_streaming.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor // ✅ Required for JPA
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name; // e.g., "ADMIN", "USER"

    // ✅ Add a constructor that accepts a string
    public Role(String name) {
        this.name = name;
    }
}
