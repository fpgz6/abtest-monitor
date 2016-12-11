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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.MonitorStatusChangeEvent;
import sun.jvmstat.monitor.event.VmEvent;
import sun.jvmstat.monitor.event.VmListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;

import com.testin.abtest.util.KafkaV9Engine;
import com.testin.abtest.util.PatternUtil;

/**
 * ClassName:JvmMonitor <br/>
 * Function: <br/>
 * Date: 2016年12月10日 下午5:35:44 <br/>
 * 
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
@Command(name = "vm")
public class JvmMonitor implements Runnable {
    private static final Logger  log          = LoggerFactory.getLogger(JvmMonitor.class);
    
    @Option(name = {"-i", "--interval"})
    public Integer               interval     = 1000;
    @Option(name = {"-b", "--brokers"})
    public String                brokers      = "localhost:9092";
    @Option(name = {"-h", "--host"})
    public String                host         = "//localhost";
    @Option(name = {"-t", "--topic"})
    public String                topic        = "jvm_topic";
    @Arguments
    public List<String>          vmIds        = new ArrayList<String>();
    
    private Properties           props        = new Properties();
    private MonitoredHost        hostVm;
    private final JvmStatSampler sampler      = new JvmStatSampler();
    private final HostListener   hostListener = new HostListener() {
                                                  @Override
                                                  public void disconnected(HostEvent event) {
                                                      // TODO: 发送邮件
                                                  }
                                                  
                                                  @SuppressWarnings("unchecked")
                                                  @Override
                                                  public void vmStatusChanged(VmStatusChangeEvent event) {
                                                      // 处理新启动的vm
                                                      Set<Integer> started = event.getStarted();
                                                      if (sampler.isActive()) {
                                                          // 在采样器处于活跃状态时，进行新增操作
                                                          started.parallelStream()
                                                                 .map(i -> String.valueOf(i))
                                                                 .filter(id -> vmIds.contains(id))
                                                                 .forEach(vmid -> {
                                                                     try {
                                                                         findActiveVm(vmid).forEach(newVm -> {
                                                                             try {
                                                                                 if (sampler.offer(newVm)) {
                                                                                     newVm.addVmListener(vmListener);
                                                                                 } else {
                                                                                     hostVm.detach(newVm);
                                                                                 }
                                                                             } catch (Exception e) {
                                                                                 log.error("插入新的vm到采样器时出现异常：" + e);
                                                                             }
                                                                         });
                                                                     } catch (Exception e) {
                                                                         log.error("处理新启动的vm时出现异常：" + e);
                                                                     }
                                                                 });
                                                      }
                                                  }
                                              };
    private final VmListener     vmListener   = new VmListener() {
                                                  @Override
                                                  public void disconnected(VmEvent event) {
                                                      // 处理vm的离线，或者宕机
                                                      MonitoredVm monitoredVm = event.getMonitoredVm();
                                                      try {
                                                          monitoredVm.removeVmListener(vmListener);
                                                          sampler.poll(monitoredVm);
                                                          hostVm.detach(monitoredVm);
                                                      } catch (MonitorException e) {
                                                          log.error("[" + event.getMonitoredVm()
                                                                               .getVmIdentifier()
                                                                               .getLocalVmId() + "]的vm发生disconnected事件，处理过程中发生异常：" + e);
                                                      }
                                                  }
                                                  
                                                  @Override
                                                  public void monitorStatusChanged(MonitorStatusChangeEvent event) {
                                                      // TODO: 发送邮件
                                                  }
                                                  
                                                  @Override
                                                  public void monitorsUpdated(VmEvent event) {
                                                      // TODO: 发送邮件
                                                  }
                                              };
    
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            init();
            sampler.start();
        } catch (Exception e) {
            log.error("执行jvm监控报错：" + e);
        }
        
    }
    
    /**
     * init: <br/>
     * 初始化 <br>
     * 
     * @author xushjie
     * @throws URISyntaxException
     * @throws MonitorException
     * @since JDK 1.8
     */
    private void init() throws MonitorException, URISyntaxException {
        //
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                  brokers);
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
        //
        hostVm = MonitoredHost.getMonitoredHost(host);
        hostVm.addHostListener(hostListener);
        //
        sampler.init(buildTargetVmList(),
                     topic,
                     interval);
        // handle user termination requests by stopping sampling loops
        Runtime.getRuntime()
               .addShutdownHook(new Thread(() -> shutdown()));
    }
    
    /**
     * shutdown: <br/>
     * 结束 <br>
     * 
     * @author xushjie
     * @since JDK 1.8
     */
    private void shutdown() {
        //
        try {
            hostVm.removeHostListener(hostListener);
        } catch (MonitorException e) {
            log.error("删除host vm的侦听器出现异常：" + e);
        }
        //
        sampler.stop();
        sampler.getVmMaps()
               .values()
               .forEach(vm -> {
                   try {
                       vm.removeVmListener(vmListener);
                       hostVm.detach(vm);
                   } catch (Exception e) {
                       log.error("[" + vm.getVmIdentifier()
                                         .getLocalVmId() + "]的vm在移除侦听器时出现异常：" + e);
                   }
               });
        //
        KafkaV9Engine.shutdown();
    }
    
    /**
     * buildTargetVmList: <br/>
     * 根据命令行提供的参数解析所有目标监控的vm列表 <br>
     * 
     * @author xushjie
     * @return
     * @since JDK 1.8
     */
    private Map<Integer, MonitoredVm> buildTargetVmList() {
        assert hostVm != null : "MonitoredHost不能为空！";
        final List<MonitoredVm> vmList = new ArrayList<MonitoredVm>();
        vmIds.parallelStream()
             .forEach(id -> {
                 try {
                     List<MonitoredVm> resultSet = findActiveVm(id);
                     vmList.addAll(resultSet);
                 } catch (Exception e) {
                     log.error("解析[" + id + "]时发生异常：" + e);
                 }
             });
        // 转换为map映射<vmId, vm>，注册侦听器，并且去重，重复原因是通过vmId指定的目标vm，会和通过名称模糊匹配到的vm相重叠
        final Map<Integer, MonitoredVm> vmMaps = new HashMap<Integer, MonitoredVm>();
        vmList.forEach(vm -> {
            try {
                // 去重
                if (vmMaps.get(vm.getVmIdentifier()
                                 .getLocalVmId()) == null) {
                    // 新增map映射
                    vmMaps.put(vm.getVmIdentifier()
                                 .getLocalVmId(),
                               vm);
                    // 注册vm的监听器
                    vm.addVmListener(vmListener);
                } else {
                    // 将重复的进行detach掉
                    hostVm.detach(vm);
                }
            } catch (Exception e) {
                log.error("对vm进行配置时，比如注册侦听器，出现异常：" + e);
            }
        });
        return vmMaps;
    }
    
    /**
     * findActiveVm: <br/>
     * 分别根据整数类型的vmId以及字符串模糊匹配类的vmId进行vm的匹配查找 <br>
     * 
     * @author xushjie
     * @param id
     * @return
     * @throws NumberFormatException
     * @throws MonitorException
     * @throws URISyntaxException
     * @since JDK 1.8
     */
    private List<MonitoredVm> findActiveVm(String id) throws NumberFormatException, MonitorException, URISyntaxException {
        final List<MonitoredVm> matches = new ArrayList<MonitoredVm>();
        // 对于指定的vm的id为pid的场景
        if (id.matches("^\\d+$")) {
            Optional<Integer> any = hostVm.activeVms()
                                          .stream()
                                          .filter(activeVm -> activeVm.equals(Integer.valueOf(id)))
                                          .findAny();
            if (any.isPresent()) {
                MonitoredVm monitoredVm = hostVm.getMonitoredVm(new VmIdentifier("//" + id + "?mode=r"),
                                                                interval);
                matches.add(monitoredVm);
            }
            return matches;
        }
        // 对于指定的vm的id为名字的模糊匹配模式的场景
        hostVm.activeVms()
              .stream()
              .forEach(activeVm -> {
                  try {
                      MonitoredVm monitoredVm = hostVm.getMonitoredVm(new VmIdentifier("//" + id + "?mode=r"),
                                                                      interval);
                      if (PatternUtil.isMatchedVm(monitoredVm,
                                                  id)) {
                          matches.add(monitoredVm);
                      } else {
                          // 如果没有通过匹配，务必进行detach
                          hostVm.detach(monitoredVm);
                      }
                  } catch (Exception e) {
                      log.error("对于非整数类型的vmId进行查找目标vm时异常：" + e);
                  }
              });
        // 不会返回null，只可能是空集合或者正常集合
        return matches;
    }
}
