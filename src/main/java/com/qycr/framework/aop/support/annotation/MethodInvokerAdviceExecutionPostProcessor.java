package com.qycr.framework.aop.support.annotation;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import java.util.function.Supplier;

public class MethodInvokerAdviceExecutionPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {


    private Pointcut[] pointcut;

    private Supplier<Advice> advice;

    private boolean exposeProxy;

    public MethodInvokerAdviceExecutionPostProcessor(Supplier<Advice> advice,Pointcut[] pointcut){
          this.pointcut=pointcut;
          this.advice=advice;
    }

    @Override
    protected ProxyFactory prepareProxyFactory(Object bean, String beanName) {
        final ProxyFactory proxyFactory = super.prepareProxyFactory(bean, beanName);
        if(!proxyFactory.isExposeProxy() && exposeProxy){
            proxyFactory.setExposeProxy(true);
        }
        return proxyFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        final MethodInvokerAdviceExecutionAdvisor advisor = new MethodInvokerAdviceExecutionAdvisor(pointcut,advice);
        advisor.setBeanFactory(beanFactory);
        this.advisor = advisor;
    }

    @Override
    public void setExposeProxy(boolean exposeProxy) {
        this.exposeProxy = exposeProxy;
    }
}