package com.example.vulnerablejava.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.utils.CommandUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "命令注入漏洞")
@RestController
@RequestMapping("cmd")
public class CommandInjectionController {

    /**
     * 存在命令注入漏洞，攻击者传入?domain=www.baidu.com;whoami即可返回当前用户
     */
    @ApiOperation("存在命令注入漏洞")
    @GetMapping("1")
    public String query(String domain) {
        String action = "nslookup " + domain;
        return CommandUtil.execute(action);
    }

    /**
     * 修复命令注入漏洞，通过正则校验限制参数拼接
     */
    @ApiOperation("修复命令注入漏洞, 使用正则校验参数")
    @GetMapping("safe")
    public String safeQuery(String domain) {
        if (checkParameter(domain)) {
            String action = "nslookup " + domain;
            return CommandUtil.execute(action);
        } else {
            return "参数不合法";
        }
    }

    public static boolean checkParameter(String command) {
        return Pattern.compile("[0-9A-Za-z_\\.]+").matcher(command).matches();
    }

    /**
     * 误报案例, 未调用shell执行命令，不可利用
     */
    @ApiOperation("误报案例, 未调用shell, 不可利用")
    @GetMapping("2")
    public String query2(String domain) {
        StringBuilder sb = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "nslookup", domain });
            BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
    }

    /**
     * 误报案例，未调用shell执行命令，不可利用
     */
    @ApiOperation("误报案例, 未调用shell, 不可利用")
    @GetMapping("3")
    public String query3(String domain) {
        List<String> commands = new ArrayList<>();
        commands.add("nslookup");
        commands.add(domain);
        StringBuilder sb = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            Process p = builder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
        } catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
    }
}
