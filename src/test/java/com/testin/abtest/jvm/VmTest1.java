/**
 * Project Name:abtest-monitor
 * File Name:Test1.java
 * Package Name:com.testin.abtest.jvm
 * Date:2016年12月12日上午10:15:01
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.testin.abtest.jvm;

/**
 * ClassName:VmTest1 <br/>
 * Function: <br/>
 * Date: 2016年12月12日 上午10:15:01 <br/>
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
public class VmTest1 {
    
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println("hello Test1");
            Thread.sleep(5000L);
        }
    }
    
}
