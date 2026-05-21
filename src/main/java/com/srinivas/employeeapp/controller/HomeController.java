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
                "DevOps 2026 Automated CI/CD Deployment Successful"
        );

        return "home";
    }
}
