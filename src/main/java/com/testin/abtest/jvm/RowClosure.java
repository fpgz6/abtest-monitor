/**
 * Project Name:abtest-monitor
 * File Name:RowClosure.java
 * Package Name:com.testin.abtest.jvm
 * Date:2016年12月10日下午5:45:14
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.jvm;

import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.ColumnFormat;
import sun.tools.jstat.Expression;
import sun.tools.jstat.ExpressionExecuter;

import com.testin.abtest.util.WhiteBox;

/**
 * ClassName:RowClosure <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:45:14 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
public class RowClosure {
    private MonitoredVm vm;
    
    public RowClosure(MonitoredVm vm) {
        this.vm = vm;
    }
    
    /**
     * getRow: <br/>
     * 读取字段值 <br>
     * @author xushjie
     * @param column
     * @return
     * @since JDK 1.8
     */
    public Object getRow(ColumnFormat column) {
        Object value = evaluateFrom(column,
                                    vm);
        if (value instanceof String || value instanceof Number) {
            return value;
        }
        return null;
    }
    
    /**
     * evaluateFrom: <br/>
     * 使用表达式引擎计算值表达式 <br>
     * @author xushjie
     * @param format
     * @param vm
     * @return
     * @since JDK 1.8
     */
    private Object evaluateFrom(ColumnFormat format,
                                MonitoredVm vm) {
        Expression e = format.getExpression();
        ExpressionExecuter ee = (ExpressionExecuter) WhiteBox.newInstance(ExpressionExecuter.class,
                                                                          vm);
        if (ee == null) {
            return null;
        }
        try {
            Object value = ee.evaluate(e);
            return value;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }
}
