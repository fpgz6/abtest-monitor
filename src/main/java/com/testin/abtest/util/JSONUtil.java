/**
 * Project Name:abtest-monitor
 * File Name:JSONUtil.java
 * Package Name:com.testin.abtest.util
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ClassName:JSONUtil <br/>
 * Function: <br/>
 * Date: <br/>
 * 
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
public class JSONUtil {
    private static final Logger       log    = LoggerFactory.getLogger(JSONUtil.class);
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * toJson: <br/>
     * 转换对象为json串 <br>
     * 
     * @author xushjie
     * @param t
     * @return
     * @since JDK 1.8
     */
    public static <T> String toJson(T t) {
        String json = "{}";
        try {
            json = mapper.writeValueAsString(t);
        } catch (Exception e) {
            log.error("转换对象为JSON串异常：" + e);
        }
        return json;
    }
    
}
