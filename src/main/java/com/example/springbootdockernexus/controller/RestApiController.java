package com.example.springbootdockernexus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

	@RequestMapping("/hello")
    public String hello() {
        return "Hello World";
    }
    
    @RequestMapping("/hell")
    public String hell() {
        return "Hello World";
    }
}
