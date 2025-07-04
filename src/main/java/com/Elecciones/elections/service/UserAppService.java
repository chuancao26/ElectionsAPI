package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.dto.UserInput;
import com.Elecciones.elections.repository.UserAppRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    
    public UserApp createUser(UserInput userInput)
    {
        this.log.info("Create user with email: {}", userInput.email());
        UserApp userApp = new UserApp(userInput);
        return this.userAppRepository.save(userApp);
    }
    
    public Iterable<UserApp> getAllUsers() {
        this.log.info("Get all users");
        return this.userAppRepository.findAll();
    }
    
    public UserApp getUserById(String id) {
        UserApp user = this.userAppRepository.findById(id).orElseThrow(
                () -> new ConflictException("User does not exist with ID: " + id)
        );
        return user;
    }
    
    public UserApp patchUser(UserApp patch, String id) {
        UserApp user = this.getUserById(id);
        
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
        UserApp user = this.getUserById(id);
        
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
        UserApp user = this.getUserById(id);
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
