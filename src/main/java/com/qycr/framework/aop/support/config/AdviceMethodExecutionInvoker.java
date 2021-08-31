package com.qycr.framework.aop.support.config;

@FunctionalInterface
public interface AdviceMethodExecutionInvoker {

    Object invoke() throws Throwable;


}
