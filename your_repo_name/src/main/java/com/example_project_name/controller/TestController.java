package com.example_project_name.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example_project_name.service.TestService;
import com.example_project_name.service.TestServiceImpl;

@RestController
public class TestController {
    TestService testService = new TestServiceImpl(); //injecting the test service
    
    @RequestMapping("/test")
    public String test() {
        return testService.test(); //calling the test method from the service
    }
}
