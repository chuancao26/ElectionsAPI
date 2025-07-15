package com.Elecciones.elections.controller;

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
//        LoginResponse response = authService.loginWithGoogle(request);
        LoginResponse response1 = authService.loginWithGoogleFake();
        return ResponseEntity.ok(response1);
    }
}