package com.example.demo.restControllers;

import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthControllers {
    private final UserService userService;
}
