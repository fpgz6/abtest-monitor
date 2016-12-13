/**
 * Project Name:abtest-monitor
 * File Name:Monitor.java
 * Package Name:com.testin.abtest
 * Date:2016年12月10日下午5:32:53
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest;

import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Help;

import com.testin.abtest.jvm.JvmMonitor;

/**
 * ClassName:Monitor <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:32:53 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
public class Monitor {
    
    /**
     * main: <br/>
     * 实例："jvm", "vm", "-i", "2000", "-b", "192.168.201.176:9092", "-t", "test8", "vmtest" <br>
     * @author xushjie
     * @param args
     * @since JDK 1.8
     */
    public static void main(String[] args) {
        CliBuilder<Runnable> builder = Cli.<Runnable> builder("monitor")
                                          .withDefaultCommand(Help.class)
                                          .withCommand(Help.class);
        builder.withGroup("jvm")
               .withDefaultCommand(JvmMonitor.class)
               .withCommand(JvmMonitor.class);
        Cli<Runnable> cli = builder.build();
        cli.parse(args)
           .run();
    }
    
}
