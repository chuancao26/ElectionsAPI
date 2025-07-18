package com.Elecciones.elections.security;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.repository.UserAppRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserAppRepository userAppRepository;
    
    public JwtAuthenticationFilter(JwtService jwtService, UserAppRepository userAppRepository) {
        this.jwtService = jwtService;
        this.userAppRepository = userAppRepository;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwt = authHeader.substring(7);
        String email = null;
        
        try {
            email = jwtService.extractEmail(jwt);
        } catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token.");
            filterChain.doFilter(request, response);
            return;
        }
        
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserApp user = userAppRepository.findByEmail(email).orElse(null);
            
            if (user != null && jwtService.isTokenValid(jwt, user)) {
                UsernamePasswordAuthenticationToken authToken = getUsernamePasswordAuthenticationToken(user);
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(UserApp user) {
        UserPrincipal userPrincipal = new UserPrincipal(
                user.getId(),
                user.getEmail(),
                null
        );
        
        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );
    }
}
