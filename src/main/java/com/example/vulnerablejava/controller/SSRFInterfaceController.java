package com.example.vulnerablejava.controller;

import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Override")
public interface SSRFInterfaceController {
    @ApiOperation("override重写方法")
    @GetMapping("/override")
    public String Interface1(String url);

}