package com.example.vulnerablejava.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.vulnerablejava.entity.User;

@Mapper
public interface UserMapper {
    User findUserByName(String name);
    User findUserByNameSafety(String name);
    User findUserByName2(String name);
}
