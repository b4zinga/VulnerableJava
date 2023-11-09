package com.example.vulnerablejava.controller;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "XXE漏洞")
@RestController
@RequestMapping("xxe")
public class XXEController {

    /**
     * 存在XXE漏洞，攻击者传入 ?xml=<?xml version="1.0" encoding="utf-8"?><!DOCTYPE foo
     * [<!ENTITY xxe SYSTEM "file:///etc/passwd">]><name>&xxe;</name> 即可读取系统文件
     */
    @ApiOperation("存在XXE漏洞")
    @GetMapping("1")
    public String parse(String xml) {
        StringBuilder sb = new StringBuilder();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));
            NodeList nodeList = document.getElementsByTagName("name");
            for (int i = 0; i < nodeList.getLength(); i++) {
                String name = document.getElementsByTagName("name").item(i).getFirstChild().getNodeValue();
                sb.append("name: " + name);
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }

    /**
     * 修复XXE漏洞，禁止解析外部实体
     */
    @ApiOperation("修复XXE漏洞, 禁止解析外部实体")
    @GetMapping("safe")
    public String safeParse(String xml) {
        StringBuilder sb = new StringBuilder();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = null;
        try {
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, false);
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbf.setFeature(FEATURE, false);
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            dbf.setFeature(FEATURE, false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));
            NodeList nodeList = document.getElementsByTagName("name");
            for (int i = 0; i < nodeList.getLength(); i++) {
                String name = document.getElementsByTagName("name").item(i).getFirstChild().getNodeValue();
                sb.append("name: " + name + ", ");
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }
}
