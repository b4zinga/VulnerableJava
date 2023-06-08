package com.example.vulnerablejava.controller;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "SPEL注入漏洞")
@RestController
@RequestMapping("spel")
public class SpelController {

    /**
     * 存在SPEL注入漏洞，攻击者传入 ?name=T(java.lang.Runtime).getRuntime().exec('whoami') 即可执行whoami命令
     */
    @ApiOperation("存在SPEL注入漏洞")
    @GetMapping("1")
    public String spel(String name) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(name);
        return expression.getValue().toString();
    }

    /**
     * 修复SPEL注入漏洞，使用SimpleEvaluationContext防止SPEL注入
     */
    @ApiOperation("修复SPEL注入漏洞, 使用SimpleEvaluationContext")
    @GetMapping("safe")
    public String safeSpel(String name) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(name);
        EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        return expression.getValue(context).toString();
    }

    /**
     * 误报案例，表达式不可控
     */
    @ApiOperation("误报案例, 表达式不可控")
    @GetMapping("2")
    public String spel2(String name) {
       EvaluationContext context = new StandardEvaluationContext();
       context.setVariable("name", name);
       ExpressionParser parser = new SpelExpressionParser();
       Expression expression = parser.parseExpression("'Hi,'+#name");
       return expression.getValue(context).toString();
    }
}
