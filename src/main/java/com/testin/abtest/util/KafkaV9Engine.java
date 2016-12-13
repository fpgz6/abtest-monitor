/**
 * Project Name:abtest-monitor
 * File Name:KafkaV9Engine.java
 * Package Name:com.testin.abtest.util
 * Date:2016年12月10日下午6:19:45
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.util;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(KafkaV9Engine.class);
    
    /**
     * sendMessages: <br/>
     * 发送一组消息 <br>
     * @author xushjie
     * @param messages
     * @since JDK 1.8
     */
    public static void sendMessages(Set<ProducerRecord<String, String>> messages) {
        rlock.lock();
        try {
            if (producer.isPresent()) {
                messages.parallelStream()
                        .forEach(msg -> producer.get()
                                                .send(msg));
            }
        } catch (Exception e) {
            log.error("发送一组消息出现异常：" + e);
        } finally {
            rlock.unlock();
        }
    }
    
    /**
     * shutdown: <br/>
     * 关闭KafkaProducer <br>
     * @author xushjie
     * @since JDK 1.8
     */
    public static void shutdown() {
        wlock.lock();
        try {
            producer.ifPresent(p -> p.close());
            producer = Optional.empty();
            inited = false;
        } catch (Exception e) {
            log.error("close关闭kafka producer出现异常：" + e);
        } finally {
            wlock.unlock();
        }
    }
    
    private static Optional<KafkaProducer<String, String>> producer = Optional.empty();
    
    private static ReentrantReadWriteLock                  lock     = new ReentrantReadWriteLock();
    
    private static ReadLock                                rlock;
    
    private static WriteLock                               wlock;
    
    private static boolean                                 inited   = false;
    
    /**
     * init: <br/>
     * 初始化kafka producer <br>
     * @author xushjie
     * @param props
     * @since JDK 1.8
     */
    public static void init(Properties props) {
        assert props != null : "properties不能为空，否则无法完成kafka producer的初始化!";
        if (!inited) {
            rlock = lock.readLock();
            wlock = lock.writeLock();
            producer = Optional.ofNullable(new KafkaProducer<String, String>(props));
            inited = true;
        }
    }
    
}
