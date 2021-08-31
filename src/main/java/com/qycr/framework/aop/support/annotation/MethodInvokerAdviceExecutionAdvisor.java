package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.config.AbstractAdviceMethodInvokerAdvisor;
import com.qycr.framework.aop.support.config.AdviceTypeSelector;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.util.Objects;
import java.util.function.Supplier;


public class MethodInvokerAdviceExecutionAdvisor extends AbstractAdviceMethodInvokerAdvisor {

    private transient Pointcut pointcut;


    public MethodInvokerAdviceExecutionAdvisor(Pointcut [] pointcuts, Supplier<Advice> adviceSupplier) {
        this.advice = buildAdvice(adviceSupplier);
        this.pointcut = AdviceTypeSelector.INSTANCE.buildPointcut(pointcuts);
    }

    private  Advice buildAdvice(Supplier<Advice> adviceSupplier){
        return adviceSupplier.get();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory=beanFactory;
        if(Objects.isNull(advice) ||  advice == AbstractAdviceConfiguration.AdviceNoop.INSTANCE){
            advice=createAdvice();
        }
        if(advice == AbstractAdviceConfiguration.AdviceNoop.INSTANCE){
            advice=new MethodInvokerAdviceExecutionInterceptor();
        }
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }



    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    protected Advice createAdvice() {
        try {
            return this.beanFactory.getBean(MethodInvokerAdviceExecutionInterceptor.class);
        }catch (Exception e){
            return AbstractAdviceConfiguration.AdviceNoop.INSTANCE;
        }

    }
}
