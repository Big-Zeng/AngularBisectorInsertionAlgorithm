/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.test.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;

/**
 * Created by zxf on 2016/12/24.
 */
public class ThreadManageContextLoaderListener extends ContextLoaderListener {
    private static Logger logger = LoggerFactory.getLogger(ThreadManageContextLoaderListener.class);

    public void contextInitialized(ServletContextEvent event) {
        logger.info("开始加载---");
        //super.contextInitialized(event);
    }

    //这个方法是在关闭tomcat时候收回所有后台线程的
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("-------开始回收，这里就会调用bean里面定义了的destroy-close方法");
        super.contextDestroyed(event);
    }


}
