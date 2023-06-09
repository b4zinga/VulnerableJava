package com.example.vulnerablejava.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.example.vulnerablejava.entity.User;

@Mapper
public interface UserMapper {
    User findUserByName(String name);
    User findUserByNameSafety(String name);
    User findUserByName2(String name);
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
