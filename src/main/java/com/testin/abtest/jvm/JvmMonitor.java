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

import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;

/**
 * ClassName:JvmMonitor <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:35:44 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@Command(name = "vm")
public class JvmMonitor implements Runnable {
    @Option(name = { "-i", "--interval" })
    public Integer      interval = 1000;
    @Option(name = { "-b", "--brokers" })
    public String       brokers  = "localhost:9092";
    @Arguments
    public List<String> vmIds;
    private Properties  props    = new Properties();
    
    @Override
    public void run() {
        init();
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
}
