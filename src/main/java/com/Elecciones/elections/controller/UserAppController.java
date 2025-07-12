package com.Elecciones.elections.controller;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.dto.UserInput;
import com.Elecciones.elections.dto.UserOut;
import com.Elecciones.elections.service.UserAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserAppController
{
    private UserAppService userAppService;
    
    public UserAppController(UserAppService userAppService)
    {
        this.userAppService = userAppService;
    }
    @GetMapping
    public ResponseEntity<List<UserOut>> getAllUsers()
    {
        return ResponseEntity.ok(this.userAppService.getAllUsers());
    }
    
    @PostMapping
    public ResponseEntity<UserOut> createUser(@RequestBody UserInput userInput)
    {
        UserOut created = this.userAppService.createUser(userInput);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserOut> getUserById(@PathVariable String id)
    {
        UserOut user = this.userAppService.getUserOutById(id);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id)
    {
        this.userAppService.deleteUser(id);
    }
    
    
    /// //////////////////////////////////////////////////
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<UserOut> patchUser(
//            @PathVariable String id,
//            @RequestBody UserApp patchUser
//    ) {
//        UserOut updated = this.userAppService.patchUser(patchUser, id);
//        return ResponseEntity.ok(updated);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<UserOut> putUser(
//            @PathVariable String id,
//            @RequestBody UserInput putUser
//    ) {
//        putUser.setId(id);
//        UserOut updated = this.userAppService.putUser(id, putUser);
//        return ResponseEntity.ok(updated);
//    }
    
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email) {
        this.userAppService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
