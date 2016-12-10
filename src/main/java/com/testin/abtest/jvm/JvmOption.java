/**
 * Project Name:abtest-monitor
 * File Name:JvmOption.java
 * Package Name:com.testin.abtest.jvm
 * Date:2016年12月10日下午5:57:40
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.jvm;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import lombok.Getter;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFinder;
import sun.tools.jstat.OptionFormat;

/**
 * ClassName:JvmOption <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:57:40 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
@Getter
public enum JvmOption {
    TIMESTAMP("timestamp"),
    CLASS("class"),
    COMPILER("compiler"),
    GC("gc"),
    GC_CAPACITY("gccapacity"),
    GC_CAUSE("gccause"),
    GC_NEW("gcnew"),
    GC_NEW_CAPACITY("gcnewcapacity"),
    GC_OLD("gcold"),
    GC_OLD_CAPACITY("gcoldcapacity"),
    GC_META_CAPACITY("gcmetacapacity"),
    GC_UTIL("gcutil"),
    PRINT_COMPILATION("printcompilation"),
    CLASS_LOAD("classload");
    private String option;
    
    private JvmOption(String option) {
        this.option = option;
    }
    
    /**
     * getSpecialOption: <br/>
     * 获取指定Option的column的Map，带时间 <br>
     * @author xushjie
     * @param finder
     * @param vm
     * @return
     * @throws MonitorException
     * @since JDK 1.8
     */
    public Map<String, Object> getSpecialOption(OptionFinder finder,
                                                MonitoredVm vm) throws MonitorException {
        return getSpecialOption(finder,
                                vm,
                                true);
    }
    
    /**
     * getSpecialOption: <br/>
     * 获取指定Option的column的Map <br>
     * @author xushjie
     * @param finder
     * @param vm
     * @param useTimestamp
     * @return
     * @throws MonitorException
     * @since JDK 1.8
     */
    public Map<String, Object> getSpecialOption(OptionFinder finder,
                                                MonitoredVm vm,
                                                boolean useTimestamp) throws MonitorException {
        OptionFormat format = finder.getOptionFormat(option,
                                                     useTimestamp);
        MapOutputFormatter output = new MapOutputFormatter(vm,
                                                           format);
        return output.getMaps();
    }
    
    /**
     * collectAllOptions: <br/>
     * 所有Options <br>
     * @author xushjie
     * @param finder
     * @param vm
     * @param useTimestamp
     * @return
     * @since JDK 1.8
     */
    public Map<String, Object> collectAllOptions(OptionFinder finder,
                                                 MonitoredVm vm,
                                                 boolean useTimestamp) {
        final Map<String, Object> all = new HashMap<String, Object>();
        Stream.of(JvmOption.values())
              .forEach(jo -> {
                  try {
                      all.putAll(getSpecialOption(finder,
                                                  vm,
                                                  useTimestamp));
                  } catch (Exception e) {
                      // skip
                  }
              });
        return all;
    }
    
}
