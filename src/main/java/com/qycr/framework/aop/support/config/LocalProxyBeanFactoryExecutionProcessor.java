package com.qycr.framework.aop.support.config;

import com.qycr.framework.aop.support.proxy.LocalCglibSubclassingInstantiationStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;

public class LocalProxyBeanFactoryExecutionProcessor implements BeanFactoryPostProcessor {


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if(beanFactory instanceof AbstractAutowireCapableBeanFactory){
            ( (AbstractAutowireCapableBeanFactory) beanFactory).setInstantiationStrategy(new LocalCglibSubclassingInstantiationStrategy());
        }
    }
}
