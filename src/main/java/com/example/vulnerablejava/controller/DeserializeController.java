package com.example.vulnerablejava.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.entity.Image;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "反序列化漏洞")
@RestController
@RequestMapping("ser")
public class DeserializeController {

    /**
     * 序列化接口，传入name和url对象，输出base64编码后的序列化数据
     */
    @ApiOperation("序列化接口")
    @GetMapping("0")
    public String seriablize(String name, String url) {
        Image image = new Image(name, url);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(stream);
            oos.writeObject(image);
            oos.close();
        } catch (Exception e) {
            return e.getMessage();
        }
        String base64 = Base64.getEncoder().encodeToString(stream.toByteArray());
        return String.format("%s<br><br>serialize=><br>%s<br><br>base64=><br>%s", image, stream, base64);
    }

    /**
     * 存在反序列化漏洞
     */
    @ApiOperation("存在反序列化漏洞")
    @GetMapping("1")
    public String deserialize(String base64) {
        Object o;
        try {
            base64 = base64.replace(" ", "+");
            byte[] bytes = Base64.getDecoder().decode(base64);
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(stream);
            o = ois.readObject();
            ois.close();
        } catch (Exception e) {
            o = e.getMessage();
        }
        return o.toString();
    }

    /**
     * 修复反序列化漏洞
     */
    @ApiOperation("修复反序列化漏洞")
    @GetMapping("safe")
    public String safeDeserialize(String base64) {
        Object o;
        try {
            base64 = base64.replace(" ", "+");
            byte[] bytes = Base64.getDecoder().decode(base64);
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(stream);
            o = ois.readUnshared();
            ois.close();
        } catch (Exception e) {
            o = e.getMessage();
        }
        return o.toString();
    }

    /**
     * 误报案例，commons-io包中的ValidatingObjectInputStream类的accept/reject方法实现了
     * 反序列化类白/黑名单控制, 可用于修复反序列化漏洞，修复时建议使用白名单，避免被绕过
     */
    @ApiOperation("误报案例")
    @GetMapping("2")
    public String deserialize2(String base64) {
        Object o;
        try {
            base64 = base64.replace(" ", "+");
            byte[] bytes = Base64.getDecoder().decode(base64);
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            ValidatingObjectInputStream ois = new ValidatingObjectInputStream(stream);
            ois.accept(Image.class);
            o = ois.readObject();
            ois.close();
        } catch (Exception e) {
            o = e.getMessage();
        }
        return o.toString();
    }
}
