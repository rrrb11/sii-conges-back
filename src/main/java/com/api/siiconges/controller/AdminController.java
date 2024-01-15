package com.api.siiconges.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.siiconges.model.User;
import com.api.siiconges.model.security.ERole;
import com.api.siiconges.model.security.Role;
import com.api.siiconges.repository.UserRepository;
import com.api.siiconges.repository.security.RoleRepository;
import com.api.siiconges.schema.request.security.SignupRequest;
import com.api.siiconges.schema.response.security.MessageResponse;
import com.api.siiconges.schema.response.user.UserAdminResponse;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("${api.baseURL}/users")
public class AdminController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
    PasswordEncoder encoder;

	
    @GetMapping("")
    public ResponseEntity<?> getAllUsers(){
    	List<User> users = userRepository.findAll();
    	
    	List<UserAdminResponse> userResponseList = new ArrayList<>(); 
    	if(!users.isEmpty()) {
    		for (User user : users) {
    			userResponseList.add(new UserAdminResponse(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getRoles()));
    		}
    		return ResponseEntity.ok(userResponseList);
    	}
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MessageResponse("No result!"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id){
    	User user = userRepository.findById(id).get();
    	if(user != null) {
    		return ResponseEntity.ok(user);
    	}
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found!"));
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @Valid @RequestBody SignupRequest signUpRequest) {

       User userToUpdate = userRepository.findById(id).get();
       if(userToUpdate != null) {
    	   if(signUpRequest.getFirstname() != "") {
    		   userToUpdate.setFirstname(signUpRequest.getFirstname());
    	   }else {
               return ResponseEntity.badRequest().body(new MessageResponse("Error: Firstname is required!"));
    	   }
    	   
    	   if(signUpRequest.getLastname() != "") {
    		   userToUpdate.setLastname(signUpRequest.getLastname());
    	   }else {
               return ResponseEntity.badRequest().body(new MessageResponse("Error: Lastname is required!"));
    	   }
    	   
    	   if(signUpRequest.getEmail() != "") {
    		   userToUpdate.setEmail(signUpRequest.getEmail());
    	   }else {
               return ResponseEntity.badRequest().body(new MessageResponse("Error: email is required!"));
    	   }
    	   
    	   if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
               return ResponseEntity.badRequest().body(new MessageResponse("Error: Passwords must be the same!"));
           }else {
        	   String hashedPassord = encoder.encode(signUpRequest.getPassword());
        	   userToUpdate.setPassword(hashedPassord);
           }
    	   
    	   Set<String> strRoles = signUpRequest.getRole();
    	   Set<Role> roles = new HashSet<>();
    	   
    	   if (strRoles == null) {
    		   Role userRole = roleRepository.findByName(ERole.USER)
    				   .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    		   roles.add(userRole);
    	   } else {
    		   strRoles.forEach(role -> {
    			   switch (role.toLowerCase()) {
    			   case "admin" -> {
    				   Role adminRole = roleRepository.findByName(ERole.ADMIN)
    						   .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    				   roles.add(adminRole);
    			   }
    			   case "super" -> {
    				   Role superRole = roleRepository.findByName(ERole.SUPER)
    						   .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    				   roles.add(superRole);
    			   }
    			   default -> {
    				   Role userRole = roleRepository.findByName(ERole.USER)
    						   .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    				   roles.add(userRole);
    			   }
    			   }
    		   });
    	   }
    	   userToUpdate.setRoles(roles);
    	   userRepository.save(userToUpdate);
       }

        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
    	User user = userRepository.findById(id).get();
    	if(user != null) {
    		userRepository.deleteById(id);
    		return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    	}
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found!"));

    }
}
