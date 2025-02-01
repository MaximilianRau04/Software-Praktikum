package com.sopra.eaplanner.auth.controllers;

import com.sopra.eaplanner.auth.models.userlogin.UserLogin;
import com.sopra.eaplanner.auth.models.userlogin.UserLoginRepository;
import com.sopra.eaplanner.auth.payload.request.LoginRequest;
import com.sopra.eaplanner.auth.payload.request.SignupRequest;
import com.sopra.eaplanner.auth.payload.response.JwtResponse;
import com.sopra.eaplanner.auth.payload.response.MessageResponse;
import com.sopra.eaplanner.auth.security.jwt.JwtUtils;
import com.sopra.eaplanner.auth.security.services.UserDetailsImpl;
import com.sopra.eaplanner.auth.models.role.ERole;
import com.sopra.eaplanner.auth.models.role.Role;
import com.sopra.eaplanner.auth.models.role.RoleRepository;
import com.sopra.eaplanner.event.tags.TagService;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.trainerprofile.TrainerProfileRepository;
import com.sopra.eaplanner.trainerprofile.TrainerProfileService;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserLoginRepository userLoginRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private TrainerProfileService trainerProfileService;
    @Autowired
    private TrainerProfileRepository trainerProfileRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Cookie cookie = new Cookie("jwt", jwt);

        cookie.setMaxAge(3600);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userLoginRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        Iterable<Role> presentRoles = roleRepository.findAll();

        int roleCount = 0;
        for (Role role : presentRoles) {
            roleCount++;
        }

        if (roleCount < 3) {
            Role roleUser = new Role();
            roleUser.setName(ERole.ROLE_USER);

            Role roleAdmin = new Role();
            roleAdmin.setName(ERole.ROLE_ADMIN);

            Role roleTrainer = new Role();
            roleTrainer.setName(ERole.ROLE_TRAINER);

            roleRepository.save(roleUser);
            roleRepository.save(roleTrainer);
            roleRepository.save(roleAdmin);
        }


        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "TRAINER":
                        Role modRole = roleRepository.findByName(ERole.ROLE_TRAINER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        UserLogin userLogin = new UserLogin(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));

        userLogin.setRoles(roles);
        Set<Role> userLoginRoles = userLogin.getRoles();
        Set<ERole> userLoginNames = userLoginRoles.stream().map(Role::getName).collect(Collectors.toSet());
        User.Role userRole = userLoginNames.contains(ERole.ROLE_ADMIN) ? User.Role.ADMIN : User.Role.USER;

        User newUser = new User(signUpRequest, userLogin, userRole);
        userLogin.setUser(newUser);

        User user = userRepository.save(newUser);

        if (user.getRole() == User.Role.ADMIN) {
            TrainerProfile trainerProfile = new TrainerProfile();
            user.setTrainerProfile(trainerProfile);
            trainerProfile.setUser(user);
            trainerProfileRepository.save(trainerProfile);
        }
        user.setInterestTags(tagService.mergeAndGetTagsFromRequest(signUpRequest.getTags()));
        userLogin.setUser(user);

        userRepository.save(user);
        userLoginRepository.save(userLogin);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}