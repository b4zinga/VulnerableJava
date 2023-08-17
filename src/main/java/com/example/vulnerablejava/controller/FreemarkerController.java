package com.example.vulnerablejava.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.vulnerablejava.entity.Image;

import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "freemarker注入漏洞")
@Controller
@RequestMapping("/freemarker")
public class FreemarkerController {
    /**
     * 渲染文件用法
     * 如果有机会修改模版文件index.ftl，则插入payload后可执行命令
     * <#assign ex="freemarker.template.utility.Execute"?new()>${ex("open -a Calculator.app")}
     */
    @ApiOperation("渲染文件")
    @GetMapping("1")
    public String freemarker1(Model model, String name) {
        if (name==null || "".equals(name)) {
            name="test.jpg";
        }
        Image image = new Image();
        image.setName(name);
        image.setUrl("https://www.example.com/");
        model.addAttribute("image", image);
        return "index";
    }

    /**
     * 存在漏洞，攻击者传入如下代码即可执行系统命令
     * ?msg=<#assign ex="freemarker.template.utility.Execute"?new()>${ex("open -a Calculator.app")}
     */
    @ApiOperation("存在漏洞")
    @ResponseBody
    @GetMapping("2")
    public String freemarker2(String msg) throws IOException, TemplateException {
        StringWriter writer = new StringWriter();
        Map<String, Object> context = new HashMap<>();
        context.put("key", "value");
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
        Template template = new Template("", msg, configuration);
        template.process(context, writer);
        return writer.toString();
    }

    /**
     * 修复漏洞，使用SAFER_RESOLVER限制调用危险函数
     */
    @ApiOperation("修复漏洞, 使用SAFER_RESOLVER")
    @ResponseBody
    @GetMapping("3")
    public String freemarker3(String msg) throws IOException, TemplateException {
        StringWriter writer = new StringWriter();
        Map<String, Object> context = new HashMap<>();
        context.put("key", "value");
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
        Template template = new Template("", msg, configuration);
        template.process(context, writer);
        return writer.toString();
    }
}