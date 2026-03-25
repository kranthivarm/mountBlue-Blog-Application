package com.example.demo.restControllers;

import com.example.demo.Utils.JwtUtil;
import com.example.demo.dtos.UserDto;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto){
        System.out.println("Rest auth signup cntrl");
        try{
            userService.signup(userDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message","successful signup"));
        }catch (Exception e){
            return ResponseEntity.badRequest()
                    .body(Map.of("error",e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String>body){
        System.out.println("Rest auth login cntrl");
        try{
            String email=body.get("username");
            String password=body.get("password");
            if(email==null || password==null){
                return ResponseEntity.badRequest()
                        .body(Map.of("error","userName and Password are required"));
            }
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password)
            );
            UserDetails userDetails=userDetailsService.loadUserByUsername(email);
            String token=jwtUtil.generateToken(userDetails);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(
                        Map.of(
                                "message", "Login successful",
                                "token",   token,
                                "email",   userDetails.getUsername(),
                                "roles",   userDetails.getAuthorities()
                                        .stream()
                                        .map(a -> a.getAuthority())
                                        .toList(),
                                "isAdmin", userDetails.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")),
                                "isUser",  userDetails.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
                        )
                    );
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }
        return ResponseEntity.ok(Map.of(
                "email",   auth.getName(),
                "roles",   auth.getAuthorities()
                        .stream()
                        .map(a -> a.getAuthority())
                        .toList(),
                "isAdmin", auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")),
                "isUser",  auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
        ));
    }
}
