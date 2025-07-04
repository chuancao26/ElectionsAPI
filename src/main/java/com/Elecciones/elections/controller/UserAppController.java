package com.Elecciones.elections.controller;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.dto.UserInput;
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
    public ResponseEntity<Iterable<UserApp>> getAllUsers() {
        return ResponseEntity.ok(this.userAppService.getAllUsers());
    }
    
    @PostMapping
    public ResponseEntity<UserApp> createUser(@RequestBody UserInput userInput) {
        UserApp created = this.userAppService.createUser(userInput);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserApp> getUserById(@PathVariable String id)
    {
        UserApp user = this.userAppService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
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
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        this.userAppService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email) {
        this.userAppService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
