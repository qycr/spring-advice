package com.qycr.framework.aop.support.config;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public abstract class AbstractAdviceMethodInvokerAdvisor  extends AbstractPointcutAdvisor implements BeanFactoryAware {


    protected transient Advice advice;

    protected  BeanFactory beanFactory;

    @Override
    public Advice getAdvice() {
        return advice;
    }

    protected  abstract Advice createAdvice();


}
