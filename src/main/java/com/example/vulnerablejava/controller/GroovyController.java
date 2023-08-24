package com.example.vulnerablejava.controller;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("Groovy漏洞")
@RestController
@RequestMapping("groovy")
public class GroovyController {

    /**
     * 存在漏洞，可执行任意代码
     * 如，传入如下代码，执行系统命令
     * ?script=%22open%20-a%20Calculator.app%22.execute()
     */
    @ApiOperation("存在groovy漏洞, 可执行任意groovy代码")
    @GetMapping("1")
    public String groovy1(String script) {
        GroovyShell groovyShell = new GroovyShell();
        return groovyShell.evaluate(script).toString();
    }

    /**
     * 存在漏洞
     * 如果攻击者可以控制GroovyScriptEngine参数中的地址(文件、URL或数据库)，上传恶意脚本，即可执行任意恶意脚本
     * ?path=1.groovy
     *
     * 1.groovy内容为"open -a Calculator.app".execute()
     */
    @ApiOperation("存在漏洞")
    @GetMapping("2")
    public String groovy2(String path) throws IOException, ResourceException, ScriptException {
        GroovyScriptEngine groovyScriptEngine = new GroovyScriptEngine("http://127.0.0.1:8081/");
        return groovyScriptEngine.run(path, new Binding()).toString();
    }

    /**
     * 存在漏洞
     * 传入如下代码即可执行命令
     * ?script=%22open%20-a%20Calculator.app%22.execute()
     */
    @ApiOperation("存在漏洞")
    @GetMapping("3")
    public String groovy3(String script) throws InstantiationException, IllegalAccessException {
        try (GroovyClassLoader groovyClassLoader = new GroovyClassLoader()) {
            Class<?> gClass = groovyClassLoader.parseClass(script);
            GroovyObject gObject = (GroovyObject) gClass.newInstance();
            return gObject.invokeMethod("main", "").toString();
        } catch (CompilationFailedException | IOException e) {
            return e.getMessage();
        }
    }

    /**
     * 存在漏洞
     * 传入如下代码即可执行命令
     * ?script=%22open%20-a%20Calculator.app%22.execute()
     */
    @ApiOperation("存在漏洞")
    @GetMapping("4")
    public String groovy4(String script) throws javax.script.ScriptException {
        ScriptEngine groovyEngine = new ScriptEngineManager().getEngineByName("groovy");
        return groovyEngine.eval(script).toString();
    }
}
