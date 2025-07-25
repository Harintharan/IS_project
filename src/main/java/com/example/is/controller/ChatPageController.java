package com.example.is.controller;

import com.example.is.service.ActiveUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/chat")
public class ChatPageController {

    private final ActiveUserService activeUserService;
@Autowired
    public ChatPageController(ActiveUserService activeUserService) {
        this.activeUserService = activeUserService;
    }



    @GetMapping
    public String showChatPage(Model model, Principal principal) {
        String currentUsername = principal.getName();
        model.addAttribute("currentUser", currentUsername);
        model.addAttribute("onlineUsers", activeUserService.getActiveUsers()
                .stream()
                .filter(u -> !u.equals(currentUsername))
                .toList());
        return "chat"; // Thymeleaf template
    }
}
