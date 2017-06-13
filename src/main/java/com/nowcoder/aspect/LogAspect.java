package com.nowcoder.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Created by zhanghe on 2016/7/14.
 * 面向切面编程，就是现在又很多业务，什么课程业务，学生业务，现在要处理所有的服务
 * 所有的controller都要插一刀，可以利用为毛·切面的思想插入到各种业务中去
 *  Aspect是一个切面
 * 只要写了@component，以一个组件的而方式在依赖注入的时候将它构造出来
 *
 */
@Aspect
@Component
/**
 * 现在把所有访问controller的方法都做一个切面的截获
 * 在调用indexController和SettingController之前都调用beforMethod方法，之后都调用afterMethod
 * 所以需要对切面的的@before和@after注解
 */
public class LogAspect {
    private static final Logger logger =  LoggerFactory.getLogger(LogAspect.class);
    //* 返回值 xx.xx.xx 包 .xx 类 .* 方法 (..) 各种各样的参数
    @Before("execution(* com.nowcoder.controller.*Controller.*(..))")
    //joinpoint是一个切点，有很多参数，打印它的信息，可以知道调用的参数是谁，谁调用了我
    public void beforMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()){
            if (arg != null) {
                sb.append("arg:" + arg.toString() + "|");
            }
        }
        logger.info("befor method" + sb.toString());

    }
    @After("execution(* com.nowcoder.controller.IndexController.*(..))")
    public void afterMethod(){
        logger.info("after method" + new Date());

    }
}
