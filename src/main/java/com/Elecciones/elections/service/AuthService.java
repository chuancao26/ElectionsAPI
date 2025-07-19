package com.Elecciones.elections.service;

import com.Elecciones.elections.dto.*;
import com.Elecciones.elections.security.JwtService;
import com.Elecciones.elections.security.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {
    
    private final GoogleOAuthService googleOAuthService;
    private final UserAppService userAppService;
    private final JwtService jwtService;
    
    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {
        var googleTokens = googleOAuthService.exchangeCodeForTokens(
                request.code(),
                request.redirectUri()
        );
        
        Map<String, Object> payload = JwtUtils.decodeJWT(googleTokens.idToken());
        
        String id = (String) payload.get("sub");
        String email = (String) payload.get("email");
        String name = (String) payload.get("name");
        String photo = (String) payload.get("picture");
        
        UserInput userInput = new UserInput(id, name, email, photo);
        UserOut user = userAppService.createUser(userInput);
        
        
        String token = jwtService.generateToken(user);
        
        return new LoginResponse(
                token,
                user.id(),
                user.email(),
                user.name()
        );
    }
    public LoginResponse loginDev(DevUser dev)
    {
        UserInput userInput = new UserInput(dev.id(), "name", dev.email(), "photo1");
        UserOut userOut = userAppService.createUser(userInput);
        
        String token = jwtService.generateToken(userOut);
        
        return new LoginResponse(
                token,
                userOut.id(),
                userOut.email(),
                userOut.name()
        );
    }
}