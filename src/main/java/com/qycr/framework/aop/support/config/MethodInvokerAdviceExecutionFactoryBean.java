package com.qycr.framework.aop.support.config;

import com.qycr.framework.aop.support.annotation.AbstractAdviceConfiguration;
import com.qycr.framework.aop.support.annotation.MethodInvokerAdviceExecutionPostProcessor;
import org.aopalliance.aop.Advice;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;


public class MethodInvokerAdviceExecutionFactoryBean extends AbstractAdviceMethodPointcut implements FactoryBean<MethodInvokerAdviceExecutionPostProcessor>  {


    private final String adviceBeanName;

    private MethodInvokerAdviceExecutionPostProcessor methodInvokerAdviceExecutionPostProcessor;


    public MethodInvokerAdviceExecutionFactoryBean( String[] pointcutBeanName, Class<? extends Annotation>[] annotationTypes, AdviceType adviceType, String [] expression ,String adviceBeanName){
           super(pointcutBeanName, annotationTypes, adviceType, expression);
           this.adviceBeanName=adviceBeanName;
    }

    @Override
    public MethodInvokerAdviceExecutionPostProcessor getObject() throws Exception {
        return this.methodInvokerAdviceExecutionPostProcessor;
    }


    @Override
    public Class<?> getObjectType() {
        return MergedBeanDefinitionPostProcessor.class;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Supplier<Advice> advice=()-> AbstractAdviceConfiguration.AdviceNoop.INSTANCE;
        if(StringUtils.hasText(adviceBeanName)){
             advice = ()->beanFactory.getBean(environment.resolvePlaceholders(adviceBeanName), Advice.class);
        }
        super.afterPropertiesSet();
        this.methodInvokerAdviceExecutionPostProcessor = new MethodInvokerAdviceExecutionPostProcessor(advice,pointcut);
        methodInvokerAdviceExecutionPostProcessor.setBeanFactory(this.beanFactory);
    }
}
