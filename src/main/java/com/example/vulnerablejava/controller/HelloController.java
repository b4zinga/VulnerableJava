package com.example.vulnerablejava.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Hello")
@RestController
@RequestMapping("hello")
public class HelloController {

    /**
     * Hello
     */
    @ApiOperation("Hello Vulnerable")
    @RequestMapping("")
    public String hello() {
        return "Hello VulnerableJava";
    }
}
