package com.example.vulnerablejava.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import groovy.lang.GroovyShell;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("Groovy漏洞")
@RestController
@RequestMapping("groovy")
public class GroovyController {

    /**
     * 存在漏洞，可执行任意代码
     * 如，传入如下代码，执行系统命令
     * ?script=%22open%20-a%20Calculator.app%22.execute()
     */
    @ApiOperation("存在groovy漏洞, 可执行任意groovy代码")
    @GetMapping("1")
    public String groovy1(String script) {
        GroovyShell groovyShell = new GroovyShell();
        return groovyShell.evaluate(script).toString();
    }
}
