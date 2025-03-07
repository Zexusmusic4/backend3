package com.example.zexus_music_streaming.util;

import com.example.zexus_music_streaming.model.Role;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RoleDeserializer extends JsonDeserializer<Set<Role>> {

    @Override
    public Set<Role> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Set<Role> roles = new HashSet<>();
        String[] roleNames = p.readValueAs(String[].class); // Read roles as an array of Strings
        for (String roleName : roleNames) {
            roles.add(new Role(roleName)); // Convert string to Role object
        }
        return roles;
    }
}
