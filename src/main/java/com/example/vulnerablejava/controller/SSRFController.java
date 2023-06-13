package com.example.vulnerablejava.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.vulnerablejava.entity.Image;
import com.example.vulnerablejava.utils.HttpUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Api(tags = "SSRF漏洞")
@RestController
@RequestMapping("ssrf")
public class SSRFController {

    /**
     * 存在SSRF漏洞，攻击者传入 ?url=http://10.10.10.1/admin 即可访问内网
     */
    @ApiOperation("存在SSRF漏洞")
    @GetMapping("1")
    public String download(String url) {
        return HttpUtil.doGet(url);
    }

    /**
     * 修复SSRF漏洞，通过URL白名单进行限制
     */
    @ApiOperation("修复SSRF漏洞, 通过URL白名单进行限制")
    @GetMapping("safe")
    public String safeDownload(String url) {
        if (checkParameter(url)) {
            return HttpUtil.doGet(url);
        } else {
            return "参数不合法";
        }
    }

    public static boolean checkParameter(String url) {
        String[] urlWhiteList = {"https://img.example.com/", "https://cdn.example.com/"}; // url白名单，以 / 结尾
        for (String whiteUrl : urlWhiteList) {
            if (url.startsWith(whiteUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 误报案例，数据流追踪错误导致误报
     */
    @ApiOperation("误报案例, builder用法导致数据流追踪错误")
    @GetMapping("2")
    public String download2(String name) {
        Image image = Image.builder().name(name).url("http://www.example.com").build();
        return HttpUtil.doGet(image.getUrl());
    }

    /**
     * 误报案例，URL为常量拼接，不可利用
     */
    private String HOST = "http://www.example.com/";
    @ApiOperation("误报案例, 常量域名拼接变量参数")
    @GetMapping("3")
    public String download3(String name) {
        StringBuilder url = new StringBuilder();
        url.append(HOST);
        url.append("?name=").append(name);
        return HttpUtil.doGet(url.toString());
    }

    /**
     * 误报案例，URL已使用正则校验，不可利用
     */
    @ApiOperation("误报案例, URL已使用正则校验")
    @GetMapping("4")
    public String download4(String url) {
        String regex = "^https://www\\.example\\.com/.*";
        if (Pattern.matches(regex, url)) {
            return HttpUtil.doGet(url);
        } else {
            return "参数不合法";
        }
    }

    /**
     * 误报案例，数据流追踪错误导致误报
     */
    @ApiOperation("误报案例, 使用setXXX导致数据流追踪错误")
    @GetMapping("5")
    public String download5(String name) {
        Image image = new Image();
        image.setName(name);
        image.setUrl("www.example.com");
        return HttpUtil.doGet(image.getUrl());
    }

    /**
     * 误报案例，拼接GET请求参数导致数据流追踪错误，不可利用
     */
    @ApiOperation("误报案例, 使用StringBuilder拼接GET请求参数导致数据流追踪错误")
    @GetMapping("6")
    public String download6(String name) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("name", name);
        StringBuilder sb = new StringBuilder();
        sb.append("http://www.example.com/");
        sb.append("?");
        for (Map.Entry<String, String> e : paramMap.entrySet()) {
            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue());
            sb.append("&");
        }
        return HttpUtil.doGet(sb.toString());
    }

    /**
     * 存在SSRF漏洞，使用RestTemplate发起请求
     */
    @ApiOperation("存在SSRF漏洞, 使用RestTemplate发起请求")
    @GetMapping("7")
    public String download7(String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = restTemplate.getForEntity(url, String.class);
        return entity.getBody();
    }

    /**
     * 误报案例，设置RestTemplate请求参数，不可利用
     */
    @ApiOperation("误报案例, 设置RestTemplate请求参数")
    @GetMapping("8")
    public String download8(String body) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://www.example.com", entity, String.class);
        return response.toString();
    }

    /**
     * 特么的，人写的东西太灵活了，怎么可能通过固定的规则全覆盖！
     */

    /**
     * 存在漏洞，存在白名单校验，但可被绕过
     * curl "http://127.0.0.1:8080/ssrf/9?url=http://127.0.0.1:5000/a?id=https://www.example.com/"
     */
    @ApiOperation("误报案例, 存在白名单校验（但可被绕过）")
    @GetMapping("9")
    public String download9(Image image) {
        String url = image.getUrl();
        String[] whiteUrlList = new String[]{"https://www.example.com/"};
        boolean flag = Stream.of(whiteUrlList).anyMatch(url::contains);
        if (flag) {
            return HttpUtil.doGet(image.getUrl());
        } else {
            return "参数不合法";
        }
    }

    /**
     * 修复漏洞，使用startsWith校验，不可利用
     */
    @ApiOperation("修复SSRF漏洞, 使用startsWith校验")
    @GetMapping("10")
    public String download10(Image image) {
        String url = image.getUrl();
        String[] whiteUrlList = new String[]{"https://www.example.com/"};
        boolean flag = Stream.of(whiteUrlList).anyMatch(url::startsWith);
        if (flag) {
            return HttpUtil.doGet(image.getUrl());
        } else {
            return "参数不合法";
        }
    }

    /**
     * 存在漏洞，使用okhttp3发起请求
     */
    @ApiOperation("存在漏洞, 使用okhttp3发起请求")
    @GetMapping("11")
    public String download11(String url) {
        String result = "";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            result = response.body().string();
        } catch (IOException e) {
            result = e.toString();
        }
        return result;
    }

    /**
     * 不存在漏洞，使用join拼接URL参数
     */
    @ApiOperation("误报案例, 使用join拼接URL参数")
    @GetMapping("12")
    public String download12(String name) {
        String url = StringUtils.join("https://www.example.com/", "?name=", name);
        return HttpUtil.doGet(url);
    }
}
