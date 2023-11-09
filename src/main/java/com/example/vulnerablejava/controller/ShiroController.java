package com.example.vulnerablejava.controller;

import java.io.IOException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Shiro漏洞")
@RestController
@RequestMapping("/shiro")
public class ShiroController {

    @ApiOperation("首页, 存在rce漏洞")
    @GetMapping()
    public String index() throws IOException {
        return "<a href='/shiro/guest'>guest</a><br><a href='shiro/admin'>admin</a>";
    }

    /**
     * 存在权限绕过漏洞
     * 未登录状态下，正常访问/shiro/admin会被拦截
     * 而访问/shiro/admin/ 或 /;/shiro/admin 则可绕过权限校验
     */
    @ApiOperation("管理页面, 存在权限绕过漏洞")
    @GetMapping("admin")
    public String admin() {
        return "Admin Page";
    }

    @ApiOperation("游客页面")
    @GetMapping("guest")
    public String guest() {
        return "Guest Page";
    }

    @ApiOperation("登陆页面")
    @GetMapping("login")
    public String login(String username, String password) {
        if (username == null && password == null) {
            return "please login with ?username=&password=";
        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token); // 调用login会去实现UserRealm中的逻辑
            // 如果没有异常，则表示登陆成功
            return "success, <a href='/shiro/admin'>admin</a>";
        } catch (UnknownAccountException e) {
            return "falied, user don't exist";
        } catch (IncorrectCredentialsException e) {
            return "falied, password error";
        }
    }

    @ApiOperation("错误页面")
    @GetMapping("error")
    public String error() {
        return "Error, <a href='/shiro/login'>please login</a>";
    }
}