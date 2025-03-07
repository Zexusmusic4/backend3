package com.example.zexus_music_streaming.service;

import com.example.zexus_music_streaming.model.Role;
import com.example.zexus_music_streaming.model.User;
import com.example.zexus_music_streaming.repository.RoleRepository;
import com.example.zexus_music_streaming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .collect(Collectors.toList());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role); // Saves role in the database
    }

    public void registerUser(User user) {
        userRepository.save(user);
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
