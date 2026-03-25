package com.example.grades.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="http://localhost:5173")
public class AuthController {

    @GetMapping()
    public Map<String, Object> me(Authentication authentication) {
        if (authentication == null) {
            Map<String, Object> res = new HashMap<>();
            res.put("authenticated", false);
            return res;
        }

        Map<String, Object> res = new HashMap<>();
        res.put("authenticated", true);
        res.put("username", authentication.getName());
        res.put("roles", authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList()));
        return res;
    }
}
