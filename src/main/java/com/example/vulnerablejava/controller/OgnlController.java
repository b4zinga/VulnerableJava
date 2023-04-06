package com.example.vulnerablejava.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.entity.Image;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

@Api(tags = "OGNL注入漏洞")
@RestController
@RequestMapping("ognl")
public class OgnlController {

    /**
     * 存在OGNL注入漏洞，攻击者传入?name=@java.lang.Runtime@getRuntime().exec('whoami')即可执行whoami命令
     * 也可传入?name=@com.example.vulnerablejava.utils.CommandUtil@execute('whoami')调用内部类执行命令
     */
    @ApiOperation("存在OGNL注入漏洞")
    @GetMapping("1")
    public String ognl(String name) throws OgnlException {
        return Ognl.getValue(name, null).toString();
    }

    /**
     * 误报案例，表达式不可控
     */
    @ApiOperation("误报案例")
    @GetMapping("2")
    public String ognl2(String name) throws OgnlException {
        Image image = new Image(name, "www.example.com");
        OgnlContext context = new OgnlContext();
        context.setRoot(image);
        return Ognl.getValue("name.length", context, context.getRoot()).toString();
    }
}
