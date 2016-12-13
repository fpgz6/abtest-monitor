/**
 * Project Name:abtest-monitor
 * File Name:JvmStatSampler.java
 * Package Name:com.testin.abtest.jvm
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.jvm;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.Arguments;
import sun.tools.jstat.OptionFinder;

import com.testin.abtest.util.JSONUtil;
import com.testin.abtest.util.KafkaV9Engine;

/**
 * ClassName:JvmStatSampler <br/>
 * Function: <br/>
 * Date: <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
@Getter
public class JvmStatSampler {
    private static final Logger       log       = LoggerFactory.getLogger(JvmStatSampler.class);
    private volatile boolean          active    = false;
    private ReentrantReadWriteLock    lock      = new ReentrantReadWriteLock();
    private ReadLock                  rlock;
    private WriteLock                 wlock;
    private OptionFinder              finder;
    private Map<Integer, MonitoredVm> vmMaps    = new HashMap<Integer, MonitoredVm>();
    private String                    topic;
    private Integer                   interval;
    private final ExecutorService     kafkaPool = Executors.newCachedThreadPool();
    
    /**
     * init: <br/>
     * @author xushjie
     * @param vms
     * @param topic
     * @param interval
     * @since JDK 1.8
     */
    public void init(Map<Integer, MonitoredVm> vms,
                     String topic,
                     Integer interval) {
        assert vms != null : "用于初始化JvmStatSampler采样器的vm列表不能为null!";
        if (!active) {
            finder = new OptionFinder(optionsSources());
            vmMaps.putAll(vms);
            rlock = lock.readLock();
            wlock = lock.writeLock();
            this.topic = topic;
            this.interval = interval;
            active = true;
        }
    }
    
    /**
     * start: <br/>
     * @author xushjie
     * @since JDK 1.8
     */
    public void start() {
        while (active) {
            assert finder != null : "OptionFinder不能为空!";
            assert !StringUtils.isBlank(topic) : "目标topic不能为空!";
            final Set<ProducerRecord<String, String>> records = new HashSet<ProducerRecord<String, String>>();
            rlock.lock();
            try {
                vmMaps.values()
                      .stream()
                      .forEach(vm -> {
                          Map<String, Object> all = JvmOption.collectAllOptions(finder,
                                                                                vm,
                                                                                true);
                          records.add(new ProducerRecord<String, String>(topic,
                                                                         JSONUtil.toJson(all)));
                      });
            } catch (Exception e) {
                log.error("采样过程中出现异常：" + e);
            } finally {
                rlock.unlock();
            }
            // 采样后进行kafka的producer发送
            log.info("总共需要发送[" + records.size() + "]条采样记录。");
            kafkaPool.submit(() -> KafkaV9Engine.sendMessages(records));
            // 采样频率与MonitoredVm的采样频率一致，都是interval
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                // skip
            }
        }
    }
    
    /**
     * stop: <br/>
     * @author xushjie
     * @since JDK 1.8
     */
    public void stop() {
        active = false;
    }
    
    /**
     * offer: <br/>
     * @author xushjie
     * @param vm
     * @return
     * @since JDK 1.8
     */
    public boolean offer(MonitoredVm vm) {
        wlock.lock();
        try {
            if (!vmMaps.containsKey(vm.getVmIdentifier()
                                      .getLocalVmId())) {
                vmMaps.put(vm.getVmIdentifier()
                             .getLocalVmId(),
                           vm);
                log.info("[" + vm.getVmIdentifier()
                                 .getLocalVmId() + "]的vm被添加到监控列表，新增后当前监控列表：" + Objects.toString(vmMaps.keySet()));
                return true;
            }
        } catch (Exception e) {
            log.error("插入新的采样vm时出现异常：" + e);
        } finally {
            wlock.unlock();
        }
        return false;
    }
    
    /**
     * poll: <br/>
     * @author xushjie
     * @param vmId
     * @return
     * @since JDK 1.8
     */
    public MonitoredVm poll(Integer vmId) {
        wlock.lock();
        try {
            MonitoredVm rm = vmMaps.remove(vmId);
            if (rm != null) {
                log.info("[" + vmId + "]的vm被移出监控列表，当前监控列表剩余vmId：" + Objects.toString(vmMaps.keySet()));
            }
            return rm;
        } catch (Exception e) {
            log.error("移除现有vm时出现异常：" + e);
        } finally {
            wlock.unlock();
        }
        return null;
    }
    
    /**
     * optionsSources: <br/>
     * 加载option配置文件 <br>
     * @author xushjie
     * @return
     * @since JDK 1.8
     */
    private List<URL> optionsSources() {
        List<URL> sources = new ArrayList<URL>();
        sources.add(Arguments.class.getResource("resources/jstat_options"));
        sources.add(Arguments.class.getResource("resources/jstat_unsupported_options"));
        return sources;
    }
}
