

package com.example.is.controller;

import com.example.is.entity.User;
import com.example.is.service.ActiveUserService;
import com.example.is.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final ActiveUserService activeUserService;
    private final AuthenticationManager authenticationManager;





    @Autowired
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          ActiveUserService activeUserService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.activeUserService = activeUserService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("user") User user, Model model) {
        boolean success = userService.registerUser(user);
        if (success) {
            return "redirect:/auth/login"; // redirect to login page after success
        } else {
            model.addAttribute("error", "Username or Email already exists");
            return "signup";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Model model) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );


            activeUserService.login(username); // 👈 Add this line
            return "redirect:/chat"; // Or your desired page
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}
