package com.api.siiconges.controller;

import com.api.siiconges.model.User;
import com.api.siiconges.model.security.RefreshToken;
import com.api.siiconges.repository.UserRepository;
import com.api.siiconges.schema.request.user.UpdatePasswordUserRequest;
import com.api.siiconges.schema.request.user.UpdateUserRequest;
import com.api.siiconges.schema.response.security.MessageResponse;
import com.api.siiconges.schema.response.security.SignupResponse;
import com.api.siiconges.service.security.RefreshTokenService;
import com.api.siiconges.service.security.UserDetailsImpl;
import com.api.siiconges.service.user.UserService;
import com.api.siiconges.utils.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("${api.baseURL}/user")
public class UserController {

    AuthenticationManager authenticationManager;

    UserRepository userRepository;

    UserService userService;

    JwtUtils jwtUtils;

    RefreshTokenService refreshTokenService;

    PasswordEncoder encoder;

    public UserController(
        AuthenticationManager authenticationManager,
        UserRepository userRepository,
        UserService userService,
        JwtUtils jwtUtils,
        RefreshTokenService refreshTokenService,
        PasswordEncoder encoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.encoder = encoder;
    }
    

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestHeader(HttpHeaders.AUTHORIZATION) String headerAuth, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        try {
            // On récupère l'email du token qui est l'email avant modification
            String actualEmail = jwtUtils.getEmailFromJwtToken(headerAuth.split(" ")[1]);
            Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(actualEmail, updateUserRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // On récupère l'id de l'utilisateur
            User actualUser = null;
            if (userRepository.findByEmail(actualEmail).isPresent()) {
                actualUser = userRepository.findByEmail(actualEmail).get();

                if (userRepository.findByEmail(updateUserRequest.getEmail()).isPresent()) {
                    if (!Objects.equals(actualUser.getId(), userRepository.findByEmail(updateUserRequest.getEmail()).get().getId())) {
                        return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
                    }
                }

            }

            //On update l'utilisateur
            User updateUser = new User(
                updateUserRequest.getFirstname(),
                updateUserRequest.getLastname(),
                updateUserRequest.getEmail()
            );
            if (actualUser != null) {
                updateUser.setId(actualUser.getId());
                updateUser.setPassword(actualUser.getPassword());
            }
            User userUpdated = userService.update(updateUser);


            //On regénère les tokens avec le nouvel email
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // nouvel email
            userDetails.setEmail(userUpdated.getEmail());

            // nouveau access token
            String jwt = jwtUtils.generateJwtToken(userDetails);

            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

            // nouveau refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());


            return ResponseEntity.ok(new SignupResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getEmail(), userUpdated.getFirstname(), userUpdated.getLastname(), roles));
        }
        catch(Exception e) {// see note 2
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new MessageResponse("Error: User not exist!"));
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String headerAuth, @Valid @RequestBody UpdatePasswordUserRequest updatePasswordUserRequest) {
        try {

            //On check si les mots de passes sont identiques
            if (!updatePasswordUserRequest.getNewPassword().equals(updatePasswordUserRequest.getConfirmNewPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Password must be identical!"));
            }

            String actualEmail = jwtUtils.getEmailFromJwtToken(headerAuth.split(" ")[1]);
            Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(actualEmail, updatePasswordUserRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // On récupère l'utilisateur
            User actualUser = null;
            if (userRepository.findByEmail(actualEmail).isPresent()) {
                actualUser = userRepository.findByEmail(actualEmail).get();
            }



            // On update l'utilisateur
            User updateUser = new User();
            if (actualUser != null) {
                updateUser.setId(actualUser.getId());
                updateUser.setFirstname(actualUser.getFirstname());
                updateUser.setLastname(actualUser.getLastname());
                updateUser.setEmail(actualUser.getEmail());
                updateUser.setPassword(encoder.encode(updatePasswordUserRequest.getNewPassword()));
            }
            User userUpdated = userService.update(updateUser);

            //On change le password
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // nouveau password
            userDetails.setPassword(userUpdated.getPassword());

            return ResponseEntity.ok(new MessageResponse("Password changed!"));
        }
        catch(Exception e) {// see note 2
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new MessageResponse("Error: Password unchanged!"));
        }
    }
}
