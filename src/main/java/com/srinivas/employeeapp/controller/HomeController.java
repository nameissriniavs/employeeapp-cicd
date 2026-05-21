package com.srinivas.employeeapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("title", "CI/CD Pipeline Project");

        model.addAttribute(
                "message",
                "Spring Boot Application Successfully Deployed using Jenkins Pipeline on AWS EC2"
        );

        return "home";
    }
}
