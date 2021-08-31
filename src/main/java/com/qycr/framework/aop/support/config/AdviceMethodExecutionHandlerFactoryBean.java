package com.qycr.framework.aop.support.config;

import com.qycr.framework.aop.support.annotation.AdviceMethodHandlerBeanPostProcessor;
import org.springframework.beans.factory.FactoryBean;

import java.lang.annotation.Annotation;


public class AdviceMethodExecutionHandlerFactoryBean extends AbstractAdviceMethodPointcut implements FactoryBean<AdviceMethodHandlerBeanPostProcessor> {


    public AdviceMethodExecutionHandlerFactoryBean(String[] pointcutBeanName, Class<? extends Annotation>[] annotationTypes, AdviceType adviceType, String[] expression) {
        super(pointcutBeanName, annotationTypes, adviceType, expression);
    }

    @Override
    public AdviceMethodHandlerBeanPostProcessor getObject() throws Exception {
        final AdviceMethodHandlerBeanPostProcessor adviceMethodHandlerBeanPostProcessor = new AdviceMethodHandlerBeanPostProcessor();
        adviceMethodHandlerBeanPostProcessor.setPointcut(AdviceTypeSelector.INSTANCE.buildPointcut(pointcut));
        adviceMethodHandlerBeanPostProcessor.setBeanFactory(this.beanFactory);
        return adviceMethodHandlerBeanPostProcessor;
    }


    @Override
    public Class<?> getObjectType() {
        return AdviceMethodHandlerBeanPostProcessor.class;
    }
}
