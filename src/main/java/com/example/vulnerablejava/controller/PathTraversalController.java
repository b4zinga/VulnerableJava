package com.example.vulnerablejava.controller;

import java.io.File;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.utils.FileUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "目录穿越漏洞")
@RestController
@RequestMapping("path")
public class PathTraversalController {

    /**
     * 存在目录穿越漏洞，攻击者传入 ?fileName=../../../../../../etc/passwd 即可读取/etc/passwd文件
     */
    @ApiOperation("存在目录穿越漏洞")
    @GetMapping("1")
    public String read(String fileName) {
        return FileUtil.readFile(fileName);
    }

    /**
     * 修复目录穿越漏洞，过滤.. / \ 等目录穿越字符
     */
    @ApiOperation("修复目录穿越漏洞")
    @GetMapping("safe")
    public String safeRead(String fileName) {
        String dataDir = System.getProperty("user.dir");
        if (checkParameter(fileName)) {
            return FileUtil.readFile(dataDir+ File.separator +fileName);
        } else {
            return "参数错误";
        }
    }

    public static boolean checkParameter(String fileName) {
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 误报案例，获取文件名后拼接到指定目录下，不可利用
     */
    @ApiOperation("误报案例")
    @GetMapping("2")
    public String read2(String filePath) {
        String dataDir = System.getProperty("user.dir");
        String fileName = filePath.substring(filePath.lastIndexOf("/"));
        return FileUtil.readFile(dataDir + fileName);
    }

    /**
     * 误报案例，@PathVariable 传参，不可利用
     */
    @ApiOperation("误报案例")
    @GetMapping("3/{fileName}")
    public String read3(@PathVariable String fileName) {
        return FileUtil.readFile(fileName);
    }
}
