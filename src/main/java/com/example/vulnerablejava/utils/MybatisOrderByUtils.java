package com.example.vulnerablejava.utils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class MybatisOrderByUtils {
    private static final String desc = " desc";

    /**
     * 获取对象全部属性
     */
    public static Set<String> getFieldSet(Class<?> object) {
        Set<String> resultList = new HashSet<>();
        try {
            Field[] fields = object.getDeclaredFields();
            for (Field field : fields) {
                resultList.add(field.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 判断order字段是否在对象的属性中
     */
    public static Boolean isSafeOrder(String order, Class<?> object) {
        Set<String> parameterSet = getFieldSet(object);
        if (parameterSet.contains(order)) {
            return true;
        }
        if (order.lastIndexOf(desc) > 0) {
            String temp = order.substring(0, order.lastIndexOf(desc));
            if (parameterSet.contains(temp)) {
                return true;
            }
        }
        return false;
    }
}
