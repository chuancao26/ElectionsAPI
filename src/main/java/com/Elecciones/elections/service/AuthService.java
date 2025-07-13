package com.Elecciones.elections.service;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.dto.GoogleLoginRequest;
import com.Elecciones.elections.dto.LoginResponse;
import com.Elecciones.elections.dto.UserInput;
import com.Elecciones.elections.dto.UserOut;
import com.Elecciones.elections.repository.UserAppRepository;
import com.Elecciones.elections.security.JwtService;
import com.Elecciones.elections.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    
    private final GoogleOAuthService googleOAuthService;
    private final UserAppService userAppService;
    private final JwtService jwtService;
    
    public AuthService(GoogleOAuthService googleOAuthService, UserAppService userAppService, JwtService jwtService)
    {
        this.googleOAuthService = googleOAuthService;
        this.userAppService = userAppService;
        this.jwtService = jwtService;
    }
    
    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {
        var googleTokens = googleOAuthService.exchangeCodeForTokens(
                request.code(),
                request.codeVerifier(),
                request.redirectUri()
        );
        
        Map<String, Object> payload = JwtUtils.decodeJWT(googleTokens.idToken());
        
        String id = (String) payload.get("id_token");
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
    public LoginResponse loginWithGoogleFake()
    {
        UserApp user = new UserApp();
        user.setId("id1");
        user.setName("name");
        user.setEmail("email1");
        user.setPhoto("photo1");
        UserInput userInput = new UserInput("id1", "name", "email1", "photo1");
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