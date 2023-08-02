package com.example.vulnerablejava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "文件包含漏洞")
@Controller
@RequestMapping("include")
public class FileInclusionController {

    /**
     * Hello JSP
     */
    @ApiOperation("Hello JSP")
    @GetMapping("hello")
    public ModelAndView hello() {
        ModelAndView mv = new ModelAndView("/hello");
        mv.addObject("msg", "JSP Page");
        return mv;
    }

    /**
     * 存在本地文件包含漏洞，攻击者传入?file=./hello.jsp即可包含本地文件
     */
    @ApiOperation("存在漏洞, 本地文件包含")
    @GetMapping("1")
    public ModelAndView include() {
        ModelAndView mv = new ModelAndView("/local");
        return mv;
    }

    /**
     * 存在远程文件包含漏洞，攻击者传入?url=http://127.0.0.1:8080/include/hello即可包含远程文件
     *
     * <c:import> 的 url 属性值可以使用 java.net.URL 类所支持的任何协议
     * （http, https, ftp, file,jar,mailto,netdoc）
     */
    @ApiOperation("存在漏洞, 远程文件包含")
    @GetMapping("2")
    public ModelAndView include2() {
        ModelAndView mv = new ModelAndView("/remote");
        return mv;
    }
}
