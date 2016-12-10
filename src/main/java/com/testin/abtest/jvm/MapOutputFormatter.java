/**
 * Project Name:abtest-monitor
 * File Name:MapOutputFormatter.java
 * Package Name:com.testin.abtest.jvm
 * Date:2016年12月10日下午5:49:57
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.jvm;

import java.util.Map;

import lombok.Getter;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;

/**
 * ClassName:MapOutputFormatter <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:49:57 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
@Getter
public class MapOutputFormatter extends OptionOutputFormatter {
    private MonitoredVm  vm;
    private OptionFormat format;
    
    public MapOutputFormatter(MonitoredVm vm,
                              OptionFormat format) throws MonitorException {
        // 构造父类，并完成resolve解析操作
        super(vm,
              format);
        this.vm = vm;
        this.format = format;
    }
    
    /**
     * getMaps: <br/>
     * 解析当前vm的所有column值，构成一个Map <br>
     * @author xushjie
     * @return
     * @since JDK 1.8
     */
    public Map<String, Object> getMaps() {
        MapOptionFormat poller = new MapOptionFormat(format.getName(),
                                                     new HeaderClosure(),
                                                     new RowClosure(vm));
        return poller.pollAll(format);
    }
}
