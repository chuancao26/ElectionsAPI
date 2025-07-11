package com.Elecciones.elections.service;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.dto.GoogleLoginRequest;
import com.Elecciones.elections.dto.LoginResponse;
import com.Elecciones.elections.dto.UserInput;
import com.Elecciones.elections.repository.UserAppRepository;
import com.Elecciones.elections.security.JwtService;
import com.Elecciones.elections.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final GoogleOAuthService googleOAuthService;
    private final UserAppService userAppService;
    private final UserAppRepository userAppRepository;
    private final JwtService jwtService;
    
    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {
        var googleTokens = googleOAuthService.exchangeCodeForTokens(
                request.code(),
                request.codeVerifier(),
                request.redirectUri()
        );
        
        Map<String, Object> payload = JwtUtils.decodeJWT(googleTokens.idToken());
        String email = (String) payload.get("email");
        String name = (String) payload.get("name");
        
        UserApp user = userAppRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserApp newUser = new UserApp();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    return userAppRepository.save(newUser);
                });
        
        String token = jwtService.generateToken(user);
        
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
    public LoginResponse loginWithGoogleFake()
    {
        UserApp user = new UserApp();
        user.setId("id1");
        user.setName("name");
        user.setEmail("email1");
        user.setPhoto("photo1");
        userAppRepository.save(user);
        
        String token = jwtService.generateToken(user);
        
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}