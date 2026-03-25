package com.example.grades;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.grades.controller.AuthController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;



class AuthControllerTest {

    private final AuthController authController = new AuthController();

    
    @Test
    void AuthenticationIsNull() {
        Map<String, Object> response = authController.me(null);

        assertNotNull(response);
        assertEquals(false, response.get("authenticated"));
        assertFalse(response.containsKey("username"));
        assertFalse(response.containsKey("roles"));
    }

    
    @Test
    void AuthenticationIsPresent() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        Map<String, Object> response = authController.me(authentication);

        assertNotNull(response);
        assertEquals(true, response.get("authenticated"));
        assertEquals("admin", response.get("username"));

        List<String> roles = (List<String>) response.get("roles");
        assertEquals(1, roles.size());
        assertEquals("ROLE_ADMIN", roles.get(0));
    }
}