/**
 * Project Name:abtest-monitor
 * File Name:PatternUtil.java
 * Package Name:com.testin.abtest.util
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;

/**
 * ClassName:PatternUtil <br/>
 * Function: <br/>
 * Date: <br/>
 * 
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
public class PatternUtil {
    
    private static final Logger log = LoggerFactory.getLogger(PatternUtil.class);
    
    /**
     * isMatchedVm: <br/>
     * 按照模式进行模糊匹配目标vm的mainClass全限定名 <br>
     * 
     * @author xushjie
     * @param vm
     * @param pattern
     * @return
     * @since JDK 1.8
     */
    public static boolean isMatchedVm(MonitoredVm vm,
                                      String pattern) {
        try {
            // 匹配过程不区分大小写
            Pattern p = Pattern.compile(pattern,
                                        Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(MonitoredVmUtil.mainClass(vm,
                                                            true));
            // 任意子串能够匹配到分组，即认为匹配成功，实现模糊匹配效果
            return m.find();
        } catch (MonitorException e) {
            log.error("对目标vm的mainClass进行模式匹配时出现异常：" + e);
        }
        return false;
    }
    
}
