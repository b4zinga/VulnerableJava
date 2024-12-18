package com.example.vulnerablejava.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vulnerablejava.dao.UserDao;
import com.example.vulnerablejava.dto.UserQuery;
import com.example.vulnerablejava.entity.User;
import com.example.vulnerablejava.mapper.UserMapper;
import com.example.vulnerablejava.utils.MybatisOrderByUtils;

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

    @Autowired
    private UserMapper userMapper;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private UserDao userDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    @ApiOperation("修复SQL注入漏洞, 使用PreparedStatement预编译")
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
    @ApiOperation("修复MyBatis SQL注入漏洞, 使用#{}代替${}进行传参")
    @GetMapping("safe2")
    public String safeGetUser2(String name) {
        return userMapper.findUserByNameSafety(name).toString();
    }

    /**
     * 误报案例，mapper中使用了`$`，但实际是从配置文件中取值，不可利用
     */
    @ApiOperation("误报案例MyBatis, mapper中使用了`$`, 但非外部传入")
    @GetMapping("3")
    public String getUser3(String name) {
        return userMapper.findUserByName2(name).toString();
    }

    /**
     * 存在JPA SQL注入漏洞，使用createNativeQuery执行原生SQL拼接导致
     * 当攻击者传入 ?name='or+1=1;时，即可查询所有数据
     */
    @ApiOperation("存在JPA SQL注入漏洞")
    @GetMapping("4")
    public String getUser4(String name) {
        String sql = String.format("SELECT * FROM users WHERE username='%s';", name);
        Query query = entityManager.createNativeQuery(sql);
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
    @ApiOperation("修复JPA SQL注入漏洞, 使用setParameter和?占位符进行预编译")
    @GetMapping("5")
    public String getUser5(String name) {
        String sql = "SELECT * FROM users WHERE username=?";
        Query query = entityManager.createNativeQuery(sql);
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

    /**
     * 修复JPA SQL注入漏洞，使用JPA原生SQL和?1占位符进行预编译
     */
    @ApiOperation("修复JPA SQL注入漏洞, 使用JPA原生SQL和?1占位符进行预编译")
    @GetMapping("6")
    public String getUser6(String name) {
        return userDao.findByName(name).toString();
    }

    /**
     * 修复JPA SQL注入漏洞，使用JPA JPQL和?1占位符进行预编译
     */
    @ApiOperation("修复JPA SQL注入漏洞, 使用JPA JPQL和?1占位符进行预编译")
    @GetMapping("7")
    public String getUser7(String name) {
        return userDao.findByName2(name).toString();
    }

    /**
     * 修复JPA SQL注入漏洞，使用JPA JPQL和@Param代替占位符进行预编译
     */
    @ApiOperation("修复JPA SQL注入漏洞, 使用JPA JPQL和@Param代替占位符进行预编译")
    @GetMapping("8")
    public String getUser8(String name) {
        return userDao.findByName3(name).toString();
    }

    /**
     * 存在JdbcTemplate SQL注入漏洞
     */
    @ApiOperation("存在JdbcTemplate SQL注入漏洞")
    @GetMapping("9")
    public String getUser9(String name) {
        String sql = String.format("SELECT * FROM users WHERE username='%s';", name);
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : mapList) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String info = String.format("%s: %s\n", entry.getKey(), entry.getValue());
                sb.append(info);
            }
        }
        return sb.toString();
    }

    /**
     * 修复JdbcTemplate SQL注入漏洞，使用预编译
     */
    @ApiOperation("修复JdbcTemplate SQL注入漏洞, 使用预编译")
    @GetMapping("10")
    public String getUser10(String name) {
        String sql = "SELECT * FROM users WHERE username=:name";
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql, param);
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : mapList) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String info = String.format("%s: %s\n", entry.getKey(), entry.getValue());
                sb.append(info);
            }
        }
        return sb.toString();
    }

    /**
     * 存在漏洞，mybatis like注入
     * 当攻击者传入?name=xxx' or 1=1;即可查询全部用户
     */
    @ApiOperation("存在漏洞, Mybatis like注入")
    @GetMapping("11")
    public String searchUser(String name) {
        return userMapper.searchUser(name).toString();
    }

    /**
     * 修复Mybatis like注入，使用 like '%' || #{name} || '%';
     *
     * Mysql: select * from users where username like concat('%', #{name}, '%');
     * Oracle、Sqlite: select * from users where username like '%' || #{name} || '%';
     * SQLServer: select * from users where username like '%' + #{name} + '%';
     */
    @ApiOperation("修复Mybatis like注入")
    @GetMapping("12")
    public String safeSearchUser(String name) {
        return userMapper.safeSearchUser(name).toString();
    }

    /**
     * 存在漏洞，Mybatis order by注入
     * 攻击者传入 ?order=id limit 1 即可注入sql语句
     */
    @ApiOperation("存在漏洞, Mybatis order by注入")
    @GetMapping("13")
    public String sortUser(String order) {
        return userMapper.sortUser(order).toString();
    }

    /**
     * 修复Mybatis order by注入，校验传入order的合法性
     */
    @ApiOperation("修复Mybatis order by注入, 对传入参数进行验证")
    @GetMapping("14")
    public String safeSortUser(String order) {
        if (MybatisOrderByUtils.isSafeOrder(order, User.class)) { //
            return userMapper.sortUser(order).toString();
        } else {
            return "参数不合法";
        }
    }

    /**
     * 存在漏洞，MyBatis in 注入
     * 攻击者传入 ?names='') union select * from users;即可查询所有用户
     */
    @ApiOperation("存在漏洞, MyBatis in注入")
    @GetMapping("15")
    public String findUserByNameList(@RequestParam String names) {
        return userMapper.findUserByNameList(names).toString();
    }

    /**
     * 修复MyBatis in注入, mapper中使用foreach
     */
    @ApiOperation("修复MyBatis in注入, mapper中使用foreach")
    @GetMapping("16")
    public String safeFindUserByNameList(@RequestParam List<String> names) {
        return userMapper.safeFindUserByNameList(names).toString();
    }

    /**
     * 误报案例，mybatis框架下，mapper中存在$，但不可控
     */
    @ApiOperation("误报案例, mybatis框架下, mapper中存在$,但不可控")
    @GetMapping("17")
    public String getUser17(String name) {
        User u = new User();
        u.setUsername(name);
        u.setPassword("123456");
        return userMapper.findUserByName3(u).toString();
    }

    /**
     * 存在漏洞，mybatis框架下，使用动态列名导致注入
     */
    @ApiOperation("存在漏洞, mybatis框架下, 动态列名导致注入")
    @GetMapping("18")
    public String getUser18(String col, String val) {
        UserQuery query = new UserQuery();
        query.setColumn(col);
        query.setValue(val);
        return userMapper.findUserByColumn(query).toString();
    }

    /**
     * 漏洞修复，mybatis框架下，安全的使用动态列名
     */
    @ApiOperation("漏洞修复, mybatis框架下, 动态列名替换")
    @GetMapping("19")
    public String getUser19(String col, String val) {
        UserQuery query = new UserQuery();
        query.setColumn(col);
        query.setValue(val);
        return userMapper.findUserByColumnSafe(query).toString();
    }
}
