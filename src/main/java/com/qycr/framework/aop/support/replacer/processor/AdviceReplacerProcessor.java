package com.qycr.framework.aop.support.replacer.processor;

import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface AdviceReplacerProcessor {

    public Object beforeAdvice(Object obj, Method method, Object[] args) throws Throwable;

    public Object afterAdvice(Object obj,Object[] args, MethodProxy mp) throws Throwable;


}
