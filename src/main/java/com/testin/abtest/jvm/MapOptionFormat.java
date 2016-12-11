/**
 * Project Name:abtest-monitor
 * File Name:MapOptionFormat.java
 * Package Name:com.testin.abtest.jvm
 * Date:2016年12月10日下午5:47:34
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.jvm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import sun.tools.jstat.ColumnFormat;
import sun.tools.jstat.OptionFormat;

import com.testin.abtest.util.WhiteBox;

/**
 * ClassName:MapOptionFormat <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:47:34 <br/>
 * 
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
@Getter
public class MapOptionFormat extends OptionFormat {
    protected HeaderClosure header;
    protected RowClosure    row;
    
    /**
     * Creates a new instance of MapOptionFormat.
     * 
     * @param name
     * @param header
     * @param row
     */
    public MapOptionFormat(String name,
                           HeaderClosure header,
                           RowClosure row) {
        super(name);
        this.header = header;
        this.row = row;
    }
    
    /**
     * pollAll: <br/>
     * 读取所有的column字段 <br>
     * 
     * @author xushjie
     * @param format
     * @return
     * @since JDK 1.8
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> pollAll(OptionFormat format) {
        Map<String, Object> all = new HashMap<String, Object>();
        List<OptionFormat> subs = (List<OptionFormat>) WhiteBox.getFieldFrom(format,
                                                                             OptionFormat.class,
                                                                             "children");
        if (subs == null) {
            return all;
        }
        for (Iterator<OptionFormat> i = subs.iterator(); i.hasNext(); /* empty */) {
            OptionFormat o = i.next();
            if (o instanceof ColumnFormat) {
                String key = name + "_" + header.getHeader((ColumnFormat) o);
                Object value = row.getRow((ColumnFormat) o);
                if (StringUtils.isBlank(key) || value == null) {
                    continue;
                }
                if (value instanceof String) {
                    key = key + "_keyword";
                }
                if (value instanceof Number) {
                    key = key + "_double";
                }
                all.put(key,
                        value);
            } else if (o instanceof OptionFormat) {
                all.putAll(pollAll(o));
            }
        }
        return all;
    }
}
