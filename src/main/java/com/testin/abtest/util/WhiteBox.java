/**
 * Project Name:abtest-monitor
 * File Name:WhiteBox.java
 * Package Name:com.testin.abtest.util
 * Date:2016年12月10日下午5:41:58
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import sun.jvmstat.monitor.MonitoredVm;

/**
 * ClassName:WhiteBox <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:41:58 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
public class WhiteBox {
    /**
     * getFieldFrom: <br/>
     * 反射字段值 <br>
     * @author xushjie
     * @param obj
     * @param clz
     * @param fieldName
     * @return
     * @since JDK 1.8
     */
    public static Object getFieldFrom(Object obj,
                                      Class<?> clz,
                                      String fieldName) {
        try {
            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * newInstance: <br/>
     * 反射构造器 <br>
     * @author xushjie
     * @param clz
     * @param params
     * @return
     * @since JDK 1.8
     */
    public static Object newInstance(Class<?> clz,
                                     Object... params) {
        try {
            Constructor<?> constructor = clz.getDeclaredConstructor(MonitoredVm.class);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
