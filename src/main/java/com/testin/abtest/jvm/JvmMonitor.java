/**
 * Project Name:abtest-monitor
 * File Name:JvmMonitor.java
 * Package Name:com.testin.abtest.jvm
 * Date:2016年12月10日下午5:35:44
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.jvm;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Option;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.tools.jstat.OptionFinder;

/**
 * ClassName:JvmMonitor <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:35:44 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
@Command(name = "vm")
public class JvmMonitor implements Runnable {
    @Option(name = { "-i", "--interval" })
    public Integer      interval = 1000;
    @Option(name = { "-b", "--brokers" })
    public String       brokers  = "localhost:9092";
    @Option(name = { "-h", "--host" })
    public String       host     = "//localhost";
    @Arguments
    public List<String> vmIds;
    private Properties  props    = new Properties();
    
    @Override
    public void run() {
        init();
        try {
            VmIdentifier vmid = new VmIdentifier("");
            MonitoredHost hostVm = MonitoredHost.getMonitoredHost(host);
            MonitoredVm vm = hostVm.getMonitoredVm(vmid);
            OptionFinder finder = new OptionFinder(optionsSources());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init() {
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                  "192.168.201.176:9092");
        props.put(ProducerConfig.ACKS_CONFIG,
                  "all");
        props.put(ProducerConfig.RETRIES_CONFIG,
                  0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,
                  16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG,
                  1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG,
                  33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                  "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                  "org.apache.kafka.common.serialization.StringSerializer");
    }
    
    private List<URL> optionsSources() {
        List<URL> sources = new ArrayList<URL>();
        sources.add(Arguments.class.getResource("resources/jstat_options"));
        sources.add(Arguments.class.getResource("resources/jstat_unsupported_options"));
        return sources;
    }
}
