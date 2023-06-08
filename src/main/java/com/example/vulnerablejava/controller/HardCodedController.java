package com.example.vulnerablejava.controller;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.config.FakeConfig;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "硬编码漏洞")
@RestController
@RequestMapping("hard")
public class HardCodedController {

    /**
     * 存在硬编码漏洞
     */
    @ApiOperation("存在硬编码漏洞")
    @GetMapping("1")
    public void testConnection() {
        String url = "jdbc:mysql://192.168.22.11:3306/users?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        String user = "root";
        String password = "123456";
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 误报案例，从配置类获取连接信息，不存在硬编码漏洞
     */
    @ApiOperation("误报案例, 从配置类获取连接信息")
    @GetMapping("2")
    public void testConnection2() {
        String jdbcParam = "?useUnicode=true&characterEncoding=utf8&failOverReadOnly=false&useSSL=false";
        FakeConfig myConfig = new FakeConfig();
        String url = "jdbc:mysql://" + myConfig.getHost() + ":" + myConfig.getPort() + jdbcParam;
        try {
            Connection connection = DriverManager.getConnection(url, myConfig.getUsername(), myConfig.getPassword());
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 误报案例，自定义数据库连接函数，不存在硬编码漏洞
     */
    @ApiOperation("误报案例, 自定义数据库连接函数, 不存在漏洞")
    public void connectionDB(String host, String port, String database, String username, String password) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
