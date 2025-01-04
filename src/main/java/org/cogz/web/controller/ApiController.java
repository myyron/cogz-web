package org.cogz.web.controller;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import org.cogz.web.dto.UserDto;
import org.cogz.web.model.ERole;
import org.cogz.web.model.Role;
import org.cogz.web.model.User;
import org.cogz.web.repository.RoleRepository;
import org.cogz.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author altrax
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        User user = new User(signUpRequest.getUsername(), passwordEncoder.encode(signUpRequest.getPassword()));

        ERole role = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        user.setFirstname(signUpRequest.getFirstname());
        user.setLastname(signUpRequest.getLastname());
        user.setEmail(signUpRequest.getEmail());
        user.setMobileNumber(signUpRequest.getMobileNumber());
        user.setBirthdate(signUpRequest.getBirthdate());

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}
