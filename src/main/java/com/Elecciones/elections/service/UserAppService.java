package com.Elecciones.elections.service;

import com.Elecciones.elections.Exception.ForbiddenException;
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
    private final UserAppRepository userAppRepository;
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
        UserApp userApp = new UserApp(userInput);
        userAppRepository.save(userApp);
        return this.makeUserOut(userApp);
    }
    
//    public List<UserOut> getAllUsers() {
//        this.log.info("Get all users");
//        List<UserApp> userApps = this.userAppRepository.findAll();
//        return listUserOut(userApps);
//    }
    private void validateUser(String currentId, String principalId)
    {
        if (!currentId.equals(principalId))
            throw new ForbiddenException("You do not have permission to access this user.");
    }
    
    public UserOut getUserOutById(String id, String userId) {
        UserApp user = this.userAppRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User does not exist with ID: " + id)
        );
        validateUser(user.getId(), userId);
        return new UserOut(user.getId(), user.getName(), user.getEmail());
    }
    
    public UserApp getUserById(String id) {
        return this.userAppRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User does not exist with ID: " + id)
        );
    }
    
    public UserOut patchUser(UserPatchInput patch , String id, String userId) {
        UserApp user = this.getUserById(id);
        
        validateUser(user.getId(), userId);
        
        if (patch.name() != null) {
            user.setName(patch.name());
        }
        
        user.setModifiedAt(LocalDateTime.now());
        return makeUserOut(this.userAppRepository.save(user));
    }
    
    public void deleteUser(String id, String userId) {
        UserApp user = this.getUserById(id);
        
        validateUser(id, userId);
        
        this.userAppRepository.delete(user);
        this.log.info("Deleted user with id {}", id);
    }
}
