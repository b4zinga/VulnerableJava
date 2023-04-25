package com.example.vulnerablejava.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.dao.UserDao;
import com.example.vulnerablejava.mapper.UserMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "SQL注入漏洞")
@RestController
@RequestMapping("sql")
public class SqliController {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    /**
     * 存在SQL注入漏洞，字符串直接拼接导致
     * 当攻击者传入 ?name='or+1=1;时，即可查询所有数据
     */
    @ApiOperation("存在SQL注入漏洞")
    @GetMapping("1")
    public String getUser(String name) {
        StringBuilder sb = new StringBuilder();
        String sql = String.format("SELECT * FROM users WHERE username='%s';", name);
        try {
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String info = String.format("ID: %s, UserName: %s, Password: %s\n",
                                            rs.getInt("id"),
                                            rs.getString("username"),
                                            rs.getString("password"));
                sb.append(info);
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }

    /**
     * 修复SQL注入漏洞，使用预编译
     */
    @ApiOperation("修复SQL注入漏洞")
    @GetMapping("safe")
    public String safeGetUser(String name) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT * FROM users WHERE username=?";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement pStatement = connection.prepareStatement(sql);
            pStatement.setString(1, name);
            ResultSet rs = pStatement.executeQuery();
            while (rs.next()) {
                String info = String.format("ID: %s, UserName: %s, Password: %s\n",
                                            rs.getInt("id"),
                                            rs.getString("username"),
                                            rs.getString("password"));
                sb.append(info);
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }

    @Autowired
    private UserMapper userMapper;
    /**
     * 存在MyBatis SQL注入漏洞，MyBatis使用${}传参导致
     * 当攻击者传入 ?name='or+1=1+limit+1;时，即可查询所有数据
     */
    @ApiOperation("存在MyBatis SQL注入漏洞")
    @GetMapping("2")
    public String getUser2(String name) {
        return userMapper.findUserByName(name).toString();
    }

    /**
     * 修复MyBatis SQL注入漏洞，使用#{}代替${}进行传参
     */
    @ApiOperation("修复MyBatis SQL注入漏洞")
    @GetMapping("safe2")
    public String safeGetUser2(String name) {
        return userMapper.findUserByNameSafety(name).toString();
    }

    /**
     * 误报案例，mapper中使用了`$`，但实际是从配置文件中取值，不可利用
     */
    @ApiOperation("误报案例MyBatis")
    @GetMapping("3")
    public String getUser3(String name) {
        return userMapper.findUserByName2(name).toString();
    }

    @PersistenceContext
    private EntityManager entityManager;
    /**
     * 存在JPA SQL注入漏洞，使用createNativeQuery执行原生SQL拼接导致
     * 当攻击者传入 ?name='or+1=1;时，即可查询所有数据
     */
    @ApiOperation("存在JPA SQL注入漏洞")
    @GetMapping("4")
    public String getUser4(String name) {
        String sql = String.format("SELECT * FROM users WHERE username='%s';", name);
        Query query =  entityManager.createNativeQuery(sql);
        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unchecked")
        List<Object[]> list = query.getResultList();
        for (Object[] objects : list) {
            String info = String.format("ID: %s, UserName: %s, Password: %s\n",
                                        objects[0],
                                        objects[1],
                                        objects[2]);
            sb.append(info);
        }
        return sb.toString();
    }

    /**
     * 修复JPA SQL注入漏洞，使用setParameter和?占位符进行预编译
     */
    @ApiOperation("修复JPA SQL注入漏洞")
    @GetMapping("5")
    public String getUser5(String name) {
        String sql = "SELECT * FROM users WHERE username=?";
        Query query =  entityManager.createNativeQuery(sql);
        query.setParameter(1, name);
        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unchecked")
        List<Object[]> list = query.getResultList();
        for (Object[] objects : list) {
            String info = String.format("ID: %s, UserName: %s, Password: %s\n",
                                        objects[0],
                                        objects[1],
                                        objects[2]);
            sb.append(info);
        }
        return sb.toString();
    }

    @Autowired
    private UserDao userDao;
    /**
     * 修复JPA SQL注入漏洞，使用JPA原生SQL和?1占位符进行预编译
     */
    @ApiOperation("修复JPA SQL注入漏洞")
    @GetMapping("6")
    public String getUser6(String name) {
        return userDao.findByName(name).toString();
    }

    /**
     * 修复JPA SQL注入漏洞，使用JPA JPQL和?1占位符进行预编译
     */
    @ApiOperation("修复JPA SQL注入漏洞")
    @GetMapping("7")
    public String getUser7(String name) {
        return userDao.findByName2(name).toString();
    }

    /**
     * 修复JPA SQL注入漏洞，使用JPA JPQL和@Param代替占位符进行预编译
     */
    @ApiOperation("修复JPA SQL注入漏洞")
    @GetMapping("8")
    public String getUser8(String name) {
        return userDao.findByName3(name).toString();
    }
}
