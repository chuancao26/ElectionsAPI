package com.Elecciones.elections.controller;

import com.Elecciones.elections.dto.DevUser;
import com.Elecciones.elections.dto.GoogleLoginRequest;
import com.Elecciones.elections.dto.LoginResponse;
import com.Elecciones.elections.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        LoginResponse response = authService.loginWithGoogle(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/dev")
    public ResponseEntity<LoginResponse> loginWithDev(@RequestBody DevUser devUser)
    {
        LoginResponse response = authService.loginDev(devUser);
        return ResponseEntity.ok(response);
        
    }
}