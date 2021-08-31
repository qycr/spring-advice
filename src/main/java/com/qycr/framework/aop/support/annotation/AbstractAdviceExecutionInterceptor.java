package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.config.AdviceMethodExecutionInvoker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

public class AbstractAdviceExecutionInterceptor implements BeanFactoryAware, Ordered {


    private BeanFactory beanFactory;

    private Integer order=Integer.MAX_VALUE;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    protected  Object execute(AdviceMethodExecutionInvoker invoker, Object target, Method method, Object[] args) throws Throwable{
        throw new UnsupportedOperationException("This operation is not supported and needs to be implemented by subclasses");
    }


    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
