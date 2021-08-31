package com.qycr.framework.aop.support.replacer;

import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@FunctionalInterface
public interface AdviceReplacer {

    public Object advice(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable;

}
