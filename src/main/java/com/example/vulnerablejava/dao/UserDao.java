package com.example.vulnerablejava.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.vulnerablejava.entity.User;

/**
 * `@Query`支持JPQL和原生SQL两种方式，默认是JPQL，语句中用的是实体中的类名和属性，
 * 指定nativeQuery=true时是原生SQL，语句中用的表名和字段名
 *
 * 如果是更新或者删除操作，方法上面要加@Modifying
 */
public interface UserDao extends JpaRepository<User, Integer> {

    @Query(value = "select * from users where username=?1", nativeQuery = true)
    User findByName(String name);

    @Query("select u from User u where u.username=?1")
    User findByName2(String name);

    @Query("select u from User u where u.username=:name")
    User findByName3(@Param("name") String name);
}
