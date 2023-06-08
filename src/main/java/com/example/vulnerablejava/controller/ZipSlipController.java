package com.example.vulnerablejava.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Zip Slip漏洞")
@RestController
@RequestMapping("zip")
public class ZipSlipController {

    /**
     * 存在Zip Slip漏洞，攻击者上传恶意zip文件，即可向任意目录写入新文件或覆盖现有文件
     */
    @ApiOperation("存在Zip Slip漏洞")
    @PostMapping("1")
    public String unZip(MultipartFile file) {
        StringBuilder sb = new StringBuilder();
        String dataDir = System.getProperty("user.dir");
        try {
            ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream());
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File f = new File(dataDir, fileName);
                sb.append(f.getAbsolutePath()+"\n");
                if (fileName.endsWith("/")) {
                    f.mkdir();
                } else {
                    FileOutputStream fileOutputStream = new FileOutputStream(f);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    int n;
                    byte[] bytes = new byte[1024];
                    while ((n=zipInputStream.read(bytes)) != -1) {
                        bufferedOutputStream.write(bytes, 0, n);
                    }
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                }
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }

    /**
     * 修复Zip Slip漏洞，过滤压缩包内文件名包含..的文件
     */
    @ApiOperation("修复Zip Slip漏洞, 过滤压缩包内文件名包含..的文件")
    @PostMapping("safe")
    public String safeUnZip(MultipartFile file) {
        StringBuilder sb = new StringBuilder();
        String dataDir = System.getProperty("user.dir");
        try {
            ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream());
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                if (!checkParameter(fileName)) {
                    throw new Exception("非法文件");
                }
                File f = new File(dataDir, fileName);
                sb.append(f.getAbsolutePath()+"\n");
                if (fileName.endsWith("/")) {
                    f.mkdir();
                } else {
                    FileOutputStream fileOutputStream = new FileOutputStream(f);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    int n;
                    byte[] bytes = new byte[1024];
                    while ((n=zipInputStream.read(bytes)) != -1) {
                        bufferedOutputStream.write(bytes, 0, n);
                    }
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                }
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }

    public static boolean checkParameter(String fileName) {
        if (fileName.contains("..")) {
            return false;
        } else {
            return true;
        }
    }
}
