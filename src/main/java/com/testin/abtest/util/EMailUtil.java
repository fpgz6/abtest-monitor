/**
 * Project Name:abtest-monitor
 * File Name:EMailUtil.java
 * Package Name:com.testin.abtest.util
 * Date:2016年12月12日下午6:13:12
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.util;

import java.util.Objects;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;

/**
 * ClassName:EMailUtil <br/>
 * Function: <br/>
 * Date: 2016年12月12日 下午6:13:12 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SuppressWarnings("restriction")
public class EMailUtil {
    private static final Logger log = LoggerFactory.getLogger(EMailUtil.class);
    private Email               email;
    
    /**
     * sendAnEmail: <br/>
     * @author xushjie
     * @return
     * @since JDK 1.8
     */
    public static EMailUtil sendAnEmail() {
        EMailUtil send = new EMailUtil();
        send.email = new SimpleEmail();
        // FIXME
        send.email.setHostName("smtp.qq.com");
        send.email.setSmtpPort(465);
        send.email.setAuthentication("xsj2013@foxmail.com",
                                     "********");
        send.email.setSSLOnConnect(true);
        return send;
    }
    
    /**
     * fromAddress: <br/>
     * @author xushjie
     * @param from
     * @return
     * @since JDK 1.8
     */
    public EMailUtil fromAddress(String from) {
        try {
            email.setFrom(from);
        } catch (EmailException e) {
            log.error("设置发送者地址[" + from + "]时出现异常：" + e);
        }
        return this;
    }
    
    /**
     * withSubject: <br/>
     * @author xushjie
     * @param subject
     * @return
     * @since JDK 1.8
     */
    public EMailUtil withSubject(String subject) {
        email.setSubject("JVM_alert");
        return this;
    }
    
    /**
     * withMessageBody: <br/>
     * @author xushjie
     * @param <T>
     * @return
     * @since JDK 1.8
     */
    public <T> EMailUtil withMessageBody(T t) {
        try {
            if (t instanceof MonitoredVm) {
                MonitoredVm vm = (MonitoredVm) t;
                email.setMsg("Alert message for jvm of vmId[" + vm.getVmIdentifier()
                                                                  .getLocalVmId() + "]：\nMain-Class：" + MonitoredVmUtil.mainClass(vm,
                                                                                                                                  true) + "\nMain-Args: " + MonitoredVmUtil.mainArgs(vm) + "\nJVM-Args: " + MonitoredVmUtil.jvmArgs(vm) + "\nJVM-Flags: " + MonitoredVmUtil.jvmFlags(vm) + "\nCommand-Line: " + MonitoredVmUtil.commandLine(vm));
            } else {
                email.setMsg("ALERT! " + Objects.toString(t));
            }
        } catch (Exception e) {
            log.error("设置email的message信息出现异常：" + e);
        }
        return this;
    }
    
    /**
     * toAddress: <br/>
     * @author xushjie
     * @param to
     * @return
     * @since JDK 1.8
     */
    public String toAddress(String to) {
        try {
            email.addTo(to);
            return email.send();
        } catch (EmailException e) {
            log.error("发送邮件到[" + to + "]失败，异常信息：" + e);
        }
        return "";
    }
    
    /**
     * Creates a new instance of EMailUtil.
     */
    private EMailUtil() {
    }
    
}
