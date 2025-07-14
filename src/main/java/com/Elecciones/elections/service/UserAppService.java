package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.ConflictException;
import com.Elecciones.elections.Exception.ResourceNotFoundException;
import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.dto.UserInput;
import com.Elecciones.elections.dto.UserOut;
import com.Elecciones.elections.dto.UserPatchInput;
import com.Elecciones.elections.repository.UserAppRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserAppService
{
    private UserAppRepository userAppRepository;
    private final Logger log = LoggerFactory.getLogger(UserAppService.class);
    
    private UserOut makeUserOut(UserApp userApp)
    {
        return new UserOut(userApp.getId(), userApp.getName(), userApp.getEmail());
    }
    private List<UserOut> listUserOut(List<UserApp> userApps)
    {
        return userApps.stream()
                .map(
                        user -> new UserOut(user.getId(), user.getName(), user.getEmail())
                )
                .toList();
    }
    public UserAppService(UserAppRepository userAppRepository) {
        this.userAppRepository = userAppRepository;
    }
    
    public UserOut createUser(UserInput userInput)
    {
        this.log.info("Create user with email: {}", userInput.email());
        UserApp userApp = new UserApp(userInput);
        userAppRepository.save(userApp);
        return this.makeUserOut(userApp);
    }
    
    public List<UserOut> getAllUsers() {
        this.log.info("Get all users");
        List<UserApp> userApps = this.userAppRepository.findAll();
        return listUserOut(userApps);
    }
    
    public UserOut getUserOutById(String id) {
        UserApp user = this.userAppRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User does not exist with ID: " + id)
        );
        return new UserOut(user.getId(), user.getName(), user.getEmail());
    }
    
    public UserApp getUserById(String id) {
        UserApp user = this.userAppRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User does not exist with ID: " + id)
        );
        return user;
    }
    
    public UserOut patchUser(UserPatchInput patch , String id) {
        UserApp user = this.getUserById(id);
        
        if (patch.name() != null) {
            user.setName(patch.name());
        }
        if (patch.email() != null) {
            this.userAppRepository.findByEmail(patch.email()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Email already in use by another user");
                }
            });
            user.setEmail(patch.email());
        }
        
        user.setModifiedAt(LocalDateTime.now());
        return makeUserOut(this.userAppRepository.save(user));
    }
    
    public void deleteUser(String id) {
        UserApp user = this.getUserById(id);
        this.userAppRepository.delete(user);
        this.log.info("Deleted user with id {}", id);
    }
    
    public void deleteUserByEmail(String email) {
        UserApp user = this.userAppRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist with email: " + email));
        this.userAppRepository.delete(user);
        this.log.info("Deleted user with email {}", email);
    }
}
