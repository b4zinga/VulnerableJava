package com.example.vulnerablejava.controller;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "xpath注入")
@RestController
@RequestMapping("xpath")
public class XpathInjectionController {
    final private String xmlString = "<users>" +
            "<user username='myc' password='123456'>myc</user>" +
            "<user username='tom' password='654321'>tom</user>" +
            "</users>";

    /**
     * 存在漏洞，攻击者传入如下数据即可直接绕过校验
     * ?user=1' or 1=1 or ''='
     */
    @GetMapping("1")
    @ApiOperation("存在漏洞")
    public String getUser(String user, String pass) {
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        dFactory.setNamespaceAware(true);
        try {
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new InputSource(new StringReader(xmlString)));

            XPathFactory xFactory = XPathFactory.newInstance();
            XPath xPath = xFactory.newXPath();
            String expression = "/users/user[@username='" + user + "' and @password='" + pass + "']";
            // XPathExpression xExpression = xPath.compile(expression);
            Object evaluate = xPath.evaluate(expression, document, XPathConstants.STRING);
            return evaluate.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 修复漏洞
     */
    @GetMapping("2")
    @ApiOperation("修复漏洞")
    public String getUser2(String user, String pass) {
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        dFactory.setNamespaceAware(true);
        try {
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new InputSource(new StringReader(xmlString)));

            XPathFactory xFactory = XPathFactory.newInstance();
            XPath xPath = xFactory.newXPath();
            String expression = "/users/user[@username=$user and @password=$pass]";
            xPath.setXPathVariableResolver(v -> {
                switch (v.getLocalPart()) {
                    case "user":
                        return user;
                    case "pass":
                        return pass;
                    default:
                        throw new IllegalArgumentException();
                }
            });
            return xPath.evaluate(expression, document, XPathConstants.STRING).toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 修复漏洞
     */
    @GetMapping("3")
    @ApiOperation("修复漏洞")
    public String getUser3(String user, String pass) {
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        dFactory.setNamespaceAware(true);
        try {
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new InputSource(new StringReader(xmlString)));

            XPathFactory xFactory = XPathFactory.newInstance();
            XPath xPath = xFactory.newXPath();
            String expression = "/users/user[@username=$user and @password=$pass]";
            Map<String, String> map = new HashMap<>();
            xPath.setXPathVariableResolver(new XPathVariableResolver() {
                @Override
                public Object resolveVariable(QName variableName) {
                    return map.get(variableName.getLocalPart());
                }
            });
            map.put("user", user);
            map.put("pass", pass);
            return xPath.evaluate(expression, document, XPathConstants.STRING).toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
