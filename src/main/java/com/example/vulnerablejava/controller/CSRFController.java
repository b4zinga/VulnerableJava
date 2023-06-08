package com.example.vulnerablejava.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.entity.User;
import com.example.vulnerablejava.mapper.UserMapper;
import com.example.vulnerablejava.utils.CSRFUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "CSRF漏洞")
@RestController
@RequestMapping("csrf")
public class CSRFController {

    @Autowired
    private UserMapper userMapper;

    @ApiOperation("查询所有用户")
    @GetMapping("list")
    public String listUsers(){
        List<User> users = userMapper.findAllUsers();
        return users.toString();
    }

    /**
     * 存在CSRF漏洞，攻击者诱骗登录用户点击如下链接，即可新增一个用户
     * http://www.example.com/csrf/1?username=roy&password=666666
     */
    @ApiOperation("存在CSRF漏洞")
    @GetMapping("1")
    public String addUser(User newUser, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            userMapper.addUser(newUser);
            return "New user id: " + newUser.getId().toString();
        }
        return "请登录";
    }

    /**
     * 修复CSRF漏洞，增加CSRF Token校验
     */
    @ApiOperation("修复CSRF漏洞, 增加CSRF Token校验")
    @GetMapping("safe")
    public String safeAddUser(User newUser, HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String csrfSessionToken = (String) request.getSession().getAttribute("csrftoken");
        String csrfFormToken = request.getParameter("_csrf");

        if (user == null) {
            return "请登录";
        }

        if (csrfSessionToken == null) {
            csrfSessionToken = CSRFUtil.generateToken();
            request.getSession().setAttribute("csrftoken", csrfSessionToken);
            Cookie cookie = new Cookie("_csrf", csrfSessionToken);
            response.addCookie(cookie);
        } else {
            if (csrfSessionToken.equals(csrfFormToken)) {
                    userMapper.addUser(newUser);
                    return "New user id: " + newUser.getId().toString();
            }
        }
        return "非法请求";
    }

    /**
     * 修复CSRF漏洞，通过filter校验CSRF Token
     */
    @ApiOperation("修复CSRF漏洞, 通过filter统一校验CSRF Token")
    @GetMapping("safe2")
    public String safeAddUser2(User newUser,  HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            userMapper.addUser(newUser);
            return "New user id: " + newUser.getId().toString();
        } else {
            return "请登录";
        }
    }
}
