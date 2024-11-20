package com.example.vulnerablejava.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.vulnerablejava.dto.UserQuery;
import com.example.vulnerablejava.entity.User;

@Mapper
public interface UserMapper {
    User findUserByName(String name);

    User findUserByNameSafety(String name);

    User findUserByName2(String name);

    User findUserByName3(User user);

    User findUserByColumn(UserQuery query); // 动态列名

    User findUserByColumnSafe(UserQuery query); // 安全动态列名

    List<User> findAllUsers();

    int addUser(User user);

    // Mybatis like 注入
    List<User> searchUser(String name);

    List<User> safeSearchUser(String name);

    // Mybatis order by注入
    List<User> sortUser(String order);

    // Mybatis in 注入
    List<User> findUserByNameList(String names);

    List<User> safeFindUserByNameList(List<String> names);
}
