package com.example.vulnerablejava.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.entity.User;
import com.example.vulnerablejava.mapper.UserMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "登录")
@RestController
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @ApiOperation("用户名密码登录")
    @GetMapping("login")
    public String login(String username, String password, HttpSession session) {
        User user = userMapper.findUserByNameSafety(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "Success";
        }
        return "Login failed";
    }

    @ApiOperation("退出")
    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "Success";
    }
}
