/**
 * Project Name:abtest-monitor
 * File Name:JvmMonitorTest.java
 * Package Name:com.testin.abtest.jvm
 * Date:2016年12月9日下午3:15:36
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.jvm;

import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.Option;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.tools.jstat.Arguments;
import sun.tools.jstat.ColumnFormat;
import sun.tools.jstat.Expression;
import sun.tools.jstat.ExpressionExecuter;
import sun.tools.jstat.OptionFinder;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;

/**
 * ClassName:JvmMonitorTest <br/>
 * Function: <br/>
 * Date: 2016年12月9日 下午3:15:36 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
public class JvmMonitorTest {
    
    /**
     * test1: <br/>
     * pid <br>
     * @author xushjie
     * @throws URISyntaxException
     * @throws MonitorException
     * @since JDK 1.8
     */
    @Test
    public void test1() throws URISyntaxException, MonitorException {
        VmIdentifier vmid = new VmIdentifier("4748");
        MonitoredHost host = MonitoredHost.getMonitoredHost(vmid);
        MonitoredVm vm = host.getMonitoredVm(vmid);
        List<Monitor> monitors = vm.findByPattern(".*");
        monitors.stream()
                .map(m -> m.getName() + "\t" + m.getValue())
                .forEach(System.out::println);
        host.detach(vm);
    }
    
    /**
     * test2: <br/>
     * JMX <br>
     * @author xushjie
     * @throws URISyntaxException
     * @throws MonitorException
     * @since JDK 1.8
     */
    @Test
    public void test2() throws URISyntaxException, MonitorException {
        VmIdentifier vmid = new VmIdentifier("//4768@192.168.201.176:1099");
        MonitoredHost host = MonitoredHost.getMonitoredHost(vmid);
        MonitoredVm vm = host.getMonitoredVm(vmid);
        List<Monitor> monitors = vm.findByPattern(".*");
        monitors.stream()
                .map(m -> m.getName() + "\t" + m.getValue())
                .forEach(System.out::println);
        host.detach(vm);
    }
    
    /**
     * test3: <br/>
     * @author xushjie
     * @throws URISyntaxException
     * @throws MonitorException
     * @throws InterruptedException
     * @since JDK 1.8
     */
    @Test
    public void test3() throws URISyntaxException, MonitorException, InterruptedException {
        VmIdentifier vmid = new VmIdentifier("2716");
        MonitoredHost host = MonitoredHost.getMonitoredHost(vmid);
        MonitoredVm vm = host.getMonitoredVm(vmid);
        List<Monitor> monitors = vm.findByPattern(".*");
        monitors.stream()
                .map(m -> m.getName() + "\t" + m.getValue())
                .forEach(System.out::println);
        System.out.println("=========================================");
        host.addHostListener(new HostListener() {
            @Override
            public void disconnected(HostEvent arg0) {
                System.out.println("disconnected: " + arg0.toString());
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public void vmStatusChanged(VmStatusChangeEvent arg0) {
                System.out.println("vmStatusChanged: " + arg0.toString());
                System.out.println("Active: ");
                arg0.getActive()
                    .forEach(System.out::println);
                System.out.println("Started: ");
                arg0.getStarted()
                    .forEach(System.out::println);
                System.out.println("Terminated: ");
                arg0.getTerminated()
                    .forEach(System.out::println);
            }
        });
        Thread.sleep(Long.MAX_VALUE);
        host.detach(vm);
    }
    
    /**
     * test4: <br/>
     * @author xushjie
     * @throws URISyntaxException
     * @throws MonitorException
     * @throws InterruptedException
     * @since JDK 1.8
     */
    @Test
    public void test4() throws URISyntaxException, MonitorException, InterruptedException {
        VmIdentifier vmid = new VmIdentifier("4748");
        MonitoredHost host = MonitoredHost.getMonitoredHost(vmid);
        MonitoredVm vm = host.getMonitoredVm(vmid);
        //
        OptionFinder finder = new OptionFinder(optionsSources());
        OptionFormat format = finder.getOptionFormat("gc",
                                                     true);
        OptionOutputFormatter output = new OptionOutputFormatter(vm,
                                                                 format);
        System.out.println(output.getHeader());
        System.out.println(output.getRow());
        Thread.sleep(1000L);
        System.out.println(output.getRow());
        host.detach(vm);
    }
    
    private List<URL> optionsSources() {
        List<URL> sources = new ArrayList<URL>();
        sources.add(Arguments.class.getResource("resources/jstat_options"));
        sources.add(Arguments.class.getResource("resources/jstat_unsupported_options"));
        return sources;
    }
    
    /**
     * test5: <br/>
     * @author xushjie
     * @throws URISyntaxException
     * @throws MonitorException
     * @throws InterruptedException
     * @since JDK 1.8
     */
    @Test
    public void test5() throws URISyntaxException, MonitorException, InterruptedException {
        VmIdentifier vmid = new VmIdentifier("2592");
        MonitoredHost host = MonitoredHost.getMonitoredHost(vmid);
        MonitoredVm vm = host.getMonitoredVm(vmid);
        OptionFinder finder = new OptionFinder(optionsSources());
        //
        printSpecialOption(finder,
                           vm,
                           "timestamp",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "class",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "compiler",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "gc",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "gccapacity",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "gccause",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "gcnew",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "gcnewcapacity",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "gcold",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "gcoldcapacity",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "gcmetacapacity",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "gcutil",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "printcompilation",
                           true);
        //
        printSpecialOption(finder,
                           vm,
                           "classload",
                           true);
        host.detach(vm);
    }
    
    /**
     * test6: <br/>
     * @author xushjie
     * @throws URISyntaxException
     * @throws MonitorException
     * @throws InterruptedException
     * @since JDK 1.8
     */
    @Test
    public void test6() throws URISyntaxException, MonitorException, InterruptedException {
        VmIdentifier vmid = new VmIdentifier("4748");
        MonitoredHost host = MonitoredHost.getMonitoredHost(vmid);
        MonitoredVm vm = host.getMonitoredVm(vmid);
        OptionFinder finder = new OptionFinder(optionsSources());
        //
        printSpecialOption(finder,
                           vm,
                           "gc",
                           true);
        host.detach(vm);
    }
    
    public static void printSpecialOption(OptionFinder finder,
                                          MonitoredVm vm,
                                          String option,
                                          boolean useTimestamp) throws MonitorException {
        OptionFormat format = finder.getOptionFormat(option,
                                                     useTimestamp);
        MapOutputFormatter output = new MapOutputFormatter(vm,
                                                           format);
        Map<String, Object> maps = output.getMaps();
        System.out.println(Objects.toString(maps));
    }
    
    public static class MapOutputFormatter extends OptionOutputFormatter {
        private MonitoredVm  vm;
        private OptionFormat format;
        
        public MapOutputFormatter(MonitoredVm vm,
                                  OptionFormat format) throws MonitorException {
            super(vm,
                  format);
            this.vm = vm;
            this.format = format;
        }
        
        public Map<String, Object> getMaps() {
            MapOptionFormat poller = new MapOptionFormat(format.getName(),
                                                         new HeaderClosure(),
                                                         new RowClosure(vm));
            return poller.pollAll(format);
        }
    }
    
    public static class MapOptionFormat extends OptionFormat {
        protected HeaderClosure header;
        protected RowClosure    row;
        
        public MapOptionFormat(String name,
                               HeaderClosure header,
                               RowClosure row) {
            super(name);
            this.header = header;
            this.row = row;
        }
        
        @SuppressWarnings("unchecked")
        public Map<String, Object> pollAll(OptionFormat format) {
            Map<String, Object> all = new HashMap<String, Object>();
            List<OptionFormat> subs = (List<OptionFormat>) WhiteBox.getFieldFrom(format,
                                                                                 OptionFormat.class,
                                                                                 "children");
            if (subs == null) {
                return all;
            }
            for (Iterator<OptionFormat> i = subs.iterator(); i.hasNext(); /* empty */) {
                OptionFormat o = i.next();
                if (o instanceof ColumnFormat) {
                    String key = name + "_" + header.getHeader((ColumnFormat) o);
                    Object value = row.getRow((ColumnFormat) o);
                    if (StringUtils.isBlank(key) || value == null) {
                        continue;
                    }
                    if (value instanceof String) {
                        key = key + "_keyword";
                    }
                    if (value instanceof Number) {
                        key = key + "_double";
                    }
                    all.put(key,
                            value);
                } else if (o instanceof OptionFormat) {
                    all.putAll(pollAll(o));
                }
            }
            return all;
        }
    }
    
    public static class WhiteBox {
        public static Object getFieldFrom(Object obj,
                                          Class<?> clz,
                                          String fieldName) {
            try {
                Field field = clz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
        public static Object newInstance(Class<?> clz,
                                         Object... params) {
            try {
                Constructor<?> constructor = clz.getDeclaredConstructor(MonitoredVm.class);
                constructor.setAccessible(true);
                return constructor.newInstance(params);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    
    public static class HeaderClosure {
        private static final char ALIGN_CHAR = '^';
        
        public String getHeader(ColumnFormat column) {
            String h = column.getHeader();
            // check for special alignment character
            if (h.indexOf(ALIGN_CHAR) >= 0) {
                int len = h.length();
                if ((h.charAt(0) == ALIGN_CHAR) && (h.charAt(len - 1) == ALIGN_CHAR)) {
                    // ^<header>^ case - center alignment
                    h = h.substring(1,
                                    len - 1);
                } else if (h.charAt(0) == ALIGN_CHAR) {
                    // ^<header> case - left alignment
                    h = h.substring(1,
                                    len);
                } else if (h.charAt(len - 1) == ALIGN_CHAR) {
                    // <header>^ case - right alignment
                    h = h.substring(0,
                                    len - 1);
                } else {
                    // an internal alignment character - ignore
                }
            } else {
                // User has provided their own padding for alignment purposes
            }
            return h;
        }
    }
    
    public static class RowClosure {
        private MonitoredVm vm;
        
        public RowClosure(MonitoredVm vm) {
            this.vm = vm;
        }
        
        public Object getRow(ColumnFormat column) {
            Object value = evaluateFrom(column,
                                        vm);
            if (value instanceof String || value instanceof Number) {
                return value;
            }
            return null;
        }
    }
    
    public static Object evaluateFrom(ColumnFormat format,
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
    
    /**
     * test7: <br/>
     * @author xushjie
     * @throws URISyntaxException
     * @throws MonitorException
     * @throws InterruptedException
     * @since JDK 1.8
     */
    @Test
    public void test7() throws URISyntaxException, MonitorException, InterruptedException {
        VmIdentifier vmid = new VmIdentifier("//3876?mode=r");
        MonitoredHost host = MonitoredHost.getMonitoredHost(vmid);
        MonitoredVm vm = host.getMonitoredVm(vmid);
        System.out.println(MonitoredVmUtil.mainClass(vm,
                                                     false));
        System.out.println(MonitoredVmUtil.mainClass(vm,
                                                     true));
        System.out.println(MonitoredVmUtil.vmVersion(vm));
        System.out.println(MonitoredVmUtil.mainArgs(vm));
        System.out.println(MonitoredVmUtil.jvmArgs(vm));
        System.out.println(MonitoredVmUtil.jvmFlags(vm));
        System.out.println(MonitoredVmUtil.commandLine(vm));
        System.out.println("vm interval: " + vm.getInterval());
        //
        Pattern p = Pattern.compile("jvmmonitortest",
                                    Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(MonitoredVmUtil.mainClass(vm,
                                                        false));
        System.out.println("matchs? " + m.matches());
        m = p.matcher(MonitoredVmUtil.mainClass(vm,
                                                true));
        System.out.println("matchs? " + m.matches());
        System.out.println(m.find());
        System.out.println(m.group());
        System.out.println(m.groupCount());
        //
        host.detach(vm);
    }
    
    /**
     * test8: <br/>
     * @author xushjie
     * @since JDK 1.8
     */
    @Test
    public void test8() {
        Properties props = new Properties();
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
        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<String, String>("test8",
                                                             Integer.toString(i),
                                                             Integer.toString(i)));
        producer.close();
    }
    
    /**
     * test9: <br/>
     * @author xushjie
     * @since JDK 1.8
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test9() {
        CliBuilder<Runnable> builder = Cli.<Runnable> builder("test9")
                                          .withCommands(Help.class);
        builder.withGroup("jvm")
               .withDefaultCommand(A.class)
               .withCommands(A.class);
        Cli<Runnable> cli = builder.build();
        cli.parse("jvm",
                  "monitor",
                  "-a",
                  "a",
                  "b",
                  "c")
           .run();
    }
    
    @Command(name = "monitor")
    public static class A implements Runnable {
        @Option(name = "-a")
        public String       a;
        @io.airlift.airline.Arguments()
        public List<String> names;
        
        @Override
        public void run() {
            System.out.println(a);
            System.out.println(Objects.toString(names));
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        Object o = null;
        if (o instanceof String) {
            System.out.println("o is string");
        } else {
            System.out.println("o is null");
        }
        while (true) {
            System.out.println("hello jvm");
            Thread.sleep(5000L);
        }
    }
}
