/**
 * Project Name:abtest-monitor
 * File Name:KafkaV9Engine.java
 * Package Name:com.testin.abtest.util
 * Date:2016年12月10日下午6:19:45
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.util;

import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * ClassName:KafkaV9Engine <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午6:19:45 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
public class KafkaV9Engine {
    
    /**
     * sendMessages: <br/>
     * 发送一组消息 <br>
     * @author xushjie
     * @param props
     * @param messages
     * @since JDK 1.8
     */
    public static void sendMessages(Properties props,
                                    Set<ProducerRecord<String, String>> messages) {
        KafkaProducer<String, String> producer = producers.get();
        if (producer == null) {
            producer = new KafkaProducer<String, String>(props);
            producers.set(producer);
        }
        final KafkaProducer<String, String> prd = producer;
        messages.parallelStream()
                .forEach(msg -> prd.send(msg));
    }
    
    private static ThreadLocal<KafkaProducer<String, String>> producers = new ThreadLocal<KafkaProducer<String, String>>();
    
}
