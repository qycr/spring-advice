package com.qycr.framework.aop.support.annotation;

import org.springframework.aop.*;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import java.lang.reflect.Method;

public abstract class AbstractAdviceMethodPostProcessor  extends InstantiationAwareBeanPostProcessorAdapter {


    protected Pointcut pointcut;


    protected   abstract  boolean matches(Class<?> clazz, Method method);

    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }
}
