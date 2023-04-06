package com.example.vulnerablejava.controller;

import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.entity.Image;
import com.example.vulnerablejava.utils.HttpUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "SSRF漏洞")
@RestController
@RequestMapping("ssrf")
public class SSRFController {

    /**
     * 存在SSRF漏洞，攻击者传入 ?url=http://10.10.10.1/admin 即可访问内网
     */
    @ApiOperation("存在SSRF漏洞")
    @GetMapping("1")
    public String download(String url) {
        return HttpUtil.doGet(url);
    }

    /**
     * 修复SSRF漏洞，通过URL白名单进行限制
     */
    @ApiOperation("修复SSRF漏洞")
    @GetMapping("safe")
    public String safeDownload(String url) {
        if (checkParameter(url)) {
            return HttpUtil.doGet(url);
        } else {
            return "参数不合法";
        }
    }

    public static boolean checkParameter(String url) {
        String[] urlWhiteList = {"https://img.example.com/", "https://cdn.example.com/"}; // url白名单，以 / 结尾
        for (String whiteUrl : urlWhiteList) {
            if (url.startsWith(whiteUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 误报案例，数据流追踪错误导致误报
     */
    @ApiOperation("误报案例")
    @GetMapping("2")
    public String download2(String name) {
        Image image = Image.builder().name(name).url("http://www.example.com").build();
        return HttpUtil.doGet(image.getUrl());
    }

    /**
     * 误报案例，URL为常量拼接，不可利用
     */
    private String HOST = "http://www.example.com/";
    @ApiOperation("误报案例")
    @GetMapping("3")
    public String download3(String name) {
        StringBuilder url = new StringBuilder();
        url.append(HOST);
        url.append("?name=").append(name);
        return HttpUtil.doGet(url.toString());
    }

    /**
     * 误报案例，URL已使用正则校验，不可利用
     */
    @ApiOperation("误报案例")
    @GetMapping("4")
    public String download4(String url) {
        String regex = "^https://www\\.example\\.com/.*";
        if (Pattern.matches(regex, url)) {
            return HttpUtil.doGet(url);
        } else {
            return "参数不合法";
        }
    }
}
