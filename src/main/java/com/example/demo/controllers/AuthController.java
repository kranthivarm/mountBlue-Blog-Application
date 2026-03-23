package com.example.demo.controllers;


import com.example.demo.dtos.UserDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(
       @RequestParam(required = false)String error,
       @RequestParam(required = false)String logout,
       Model model
    ){
        if (error != null)  model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("message", "Logged out successfully.");
        return "loginPage";
    }

    @GetMapping("/signup")
    public String signupPage(Model model){
        model.addAttribute("userDto",new UserDto());
        return "signupPage";
    }
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDto userDto,Model model){
        try{
            userService.signup(userDto);
            return "redirect:/auth/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error",e.getMessage());
            return "signupPage";
        }
    }
}
