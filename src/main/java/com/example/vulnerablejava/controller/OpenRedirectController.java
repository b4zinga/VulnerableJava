package com.example.vulnerablejava.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "开放重定向漏洞")
@RestController
@RequestMapping("redirect")
public class OpenRedirectController {

    /**
     * 存在重定向漏洞，攻击者诱骗用户点击如下链接后，用户浏览器就会被重定向到 www.evil.com 钓鱼网站
     * https://www.example.com/redirect/1?url=https://www.evil.com
     */
    @ApiOperation("存在重定向漏洞")
    @GetMapping("1")
    public void redirect(String url, HttpServletResponse response) throws IOException {
        response.sendRedirect(url);
    }

    /**
     * 修复重定向漏洞，通过host白名单进行限制
     */
    @ApiOperation("修复重定向漏洞, 通过host白名单进行限制")
    @GetMapping("safe")
    public void safeRedirect(String url, HttpServletResponse response) throws IOException {
        if (checkParameter(url)) {
            response.sendRedirect(url);
        }
    }

    public static boolean checkParameter(String url) {
        String[] whiteHostList = { ".example.com", ".example2.com" }; // host白名单, 以.开头
        String host = "";
        try {
            url = url.replaceAll("[\\\\#]", "/");
            host = new URL(url).getHost();
            host = URLEncoder.encode(host, "UTF-8");
        } catch (MalformedURLException e) {
            return false;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        for (String white : whiteHostList) {
            if (host.endsWith(white) && !host.contains("%")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 误报案例，从headers取值，不可利用
     */
    @ApiOperation("误报案例, 参数从Header取值, 不可利用")
    @GetMapping("2")
    public void redirect2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getHeader("redirect");
        response.sendRedirect(url);
    }

    /**
     * 存在重定向漏洞，使用setHeader进行重定向
     */
    @ApiOperation("存在重定向漏洞, 使用setHeader进行重定向")
    @GetMapping("3")
    public void redirect3(String url, HttpServletResponse response) {
        response.setHeader("Location", url);
        response.setStatus(302);
    }

    /**
     * 误报案例，使用getRequestURI拼接，不可利用
     */
    @ApiOperation("误报案例, 使用getRequestURI拼接")
    @GetMapping("4")
    public void redirect4(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = "https://www.example.com/" + request.getRequestURI();
        response.sendRedirect(url);
    }
}
