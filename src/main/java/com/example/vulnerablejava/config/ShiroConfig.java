package com.example.vulnerablejava.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.vulnerablejava.realm.UserRealm;

@Configuration
public class ShiroConfig {
    
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());

        /**
         * 添加shiro过滤器，实现权限url拦截
         * 
         * anon: 匿名拦截器, 不需要登录就能访问, 一般用于静态资源, 或者移动端接口
         * authc: 登录拦截器, 需要登录认证才能访问的资源
         * logout: 登出拦截器, 用户登出拦截器, 主要属性:redirectURL, 退出登录后重定向的地址
         * user: 用户拦截器, 用户已经身份验证 / 记住我登录的都可
         * perms: 权限拦截器, 验证用户是否拥有资源权限
         * roles: 角色拦截器, 验证用户是否拥有资源角色
         */
        Map<String, String> filterMap = new HashMap<>();
        filterMap.put("/shiro/login", "anon");
        filterMap.put("/shiro/guest", "anon");
        filterMap.put("/shiro/admin", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap); // 设置安全管理器
        shiroFilterFactoryBean.setLoginUrl("/shiro/error"); // 设置无权限被拦截后的跳转页面
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm()); // 关联Realm
        return securityManager;
    }

    @Bean
    public UserRealm userRealm() { // 创建Realm
        return new UserRealm();
    }
}