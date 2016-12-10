/**
 * Project Name:abtest-monitor
 * File Name:HeaderClosure.java
 * Package Name:com.testin.abtest.jvm
 * Date:2016年12月10日下午5:44:10
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.jvm;

import sun.tools.jstat.ColumnFormat;

/**
 * ClassName:HeaderClosure <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:44:10 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
public class HeaderClosure {
    private static final char ALIGN_CHAR = '^';
    
    /**
     * getHeader: <br/>
     * 获取头信息 <br>
     * @author xushjie
     * @param column
     * @return
     * @since JDK 1.8
     */
    public String getHeader(ColumnFormat column) {
        String h = column.getHeader();
        // check for special alignment character
        if (h.indexOf(ALIGN_CHAR) >= 0) {
            int len = h.length();
            if ((h.charAt(0) == ALIGN_CHAR) && (h.charAt(len - 1) == ALIGN_CHAR)) {
                // ^<header>^ case - center alignment
                h = h.substring(1,
                                len - 1);
            } else if (h.charAt(0) == ALIGN_CHAR) {
                // ^<header> case - left alignment
                h = h.substring(1,
                                len);
            } else if (h.charAt(len - 1) == ALIGN_CHAR) {
                // <header>^ case - right alignment
                h = h.substring(0,
                                len - 1);
            } else {
                // an internal alignment character - ignore
            }
        } else {
            // User has provided their own padding for alignment purposes
        }
        return h;
    }
}
