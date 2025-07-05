package com.Elecciones.elections.controller;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.dto.UserInput;
import com.Elecciones.elections.dto.UserOut;
import com.Elecciones.elections.service.UserAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserAppController
{
    private UserAppService userAppService;
    
    public UserAppController(UserAppService userAppService) {
        this.userAppService = userAppService;
    }
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(this.userAppService.getAllUsers());
    }
    
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserInput userInput) {
        UserOut created = this.userAppService.createUser(userInput);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id)
    {
        UserOut user = this.userAppService.getUserOutById(id);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        this.userAppService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    
    /// //////////////////////////////////////////////////
    
    @PatchMapping("/{id}")
    public ResponseEntity<UserApp> patchUser(
            @PathVariable String id,
            @RequestBody UserApp patchUser
    ) {
        UserApp updated = this.userAppService.patchUser(patchUser, id);
        return ResponseEntity.ok(updated);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserApp> putUser(
            @PathVariable String id,
            @RequestBody UserApp putUser
    ) {
        putUser.setId(id);
        UserApp updated = this.userAppService.putUser(id, putUser);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email) {
        this.userAppService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
