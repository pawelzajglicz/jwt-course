package com.nimuairy.auth.controller;

import com.nimuairy.auth.exception.ExceptionHandling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserController extends ExceptionHandling {

    @GetMapping("/home")
    public String showUser() {
        return "home";
    }
}
