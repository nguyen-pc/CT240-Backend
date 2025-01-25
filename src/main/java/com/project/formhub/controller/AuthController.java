package com.project.formhub.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AuthController {
    
    @GetMapping("/path")
    public String getMethodName() {
        return new String("hello");
    }
    
}
