package com.Elecciones.elections.service;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.repository.UserAppRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserAppService
{
    private UserAppRepository userAppRepository;
    private final Logger log = LoggerFactory.getLogger(UserAppService.class);
    
    public UserAppService(UserAppRepository userAppRepository) {
        this.userAppRepository = userAppRepository;
    }

    public Iterable<UserApp> getAllUsers() {
        this.log.info("Get all users");
        return this.userAppRepository.findAll();
    }
    
    private UserApp existUserById(String id) {
        UserApp user = this.userAppRepository.findById(id).orElse(null);
        if (user == null) {
            throw new RuntimeException("User does not exist with ID: " + id);
        }
        return user;
    }
    
    public UserApp getUserById(String id) {
        return this.existUserById(id);
    }
    
    public UserApp createUser(UserApp userApp) {
        this.log.info("Create user with email: {}", userApp.getEmail());
        
        if (this.userAppRepository.findByEmail(userApp.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + userApp.getEmail());
        }
        
        userApp.setCreatedAt(LocalDateTime.now());
        userApp.setModifiedAt(LocalDateTime.now());
        
        return this.userAppRepository.save(userApp);
    }
    
    public UserApp patchUser(UserApp patch, String id) {
        UserApp user = this.existUserById(id);
        
        if (patch.getName() != null) {
            user.setName(patch.getName());
        }
        if (patch.getEmail() != null) {
            this.userAppRepository.findByEmail(patch.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Email already in use by another user");
                }
            });
            user.setEmail(patch.getEmail());
        }
        if (patch.getPhoto() != null) {
            user.setPhoto(patch.getPhoto());
        }
        
        user.setModifiedAt(LocalDateTime.now());
        return this.userAppRepository.save(user);
    }
    
    public UserApp putUser(String id, UserApp putUser) {
        UserApp user = this.existUserById(id);
        
        if (!putUser.getId().equals(user.getId())) {
            throw new IllegalArgumentException("ID not valid for update");
        }
        
        // Validar email único
        this.userAppRepository.findByEmail(putUser.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Email already in use by another user");
            }
        });
        
        putUser.setModifiedAt(LocalDateTime.now());
        putUser.setCreatedAt(user.getCreatedAt()); // conservar fecha de creación
        return this.userAppRepository.save(putUser);
    }
    
    public void deleteUser(String id) {
        UserApp user = this.existUserById(id);
        this.userAppRepository.delete(user);
        this.log.info("Deleted user with id {}", id);
    }
    
    public void deleteUserByEmail(String email) {
        UserApp user = this.userAppRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist with email: " + email));
        this.userAppRepository.delete(user);
        this.log.info("Deleted user with email {}", email);
    }
}
