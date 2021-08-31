package com.qycr.framework.aop.support.replacer;

import com.qycr.framework.aop.support.annotation.AdviceContext;
import com.qycr.framework.aop.support.replacer.processor.AdviceReplacerProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

public class AdviceMethodExecutionReplacer implements AdviceReplacer , BeanFactoryAware {

    public static final String COMMON_ADVICE_REPLACER="internalCommonAdviceReplacer";

    private boolean nestedResourceHandler;

    private final Set<AdviceReplacerProcessor> adviceReplacerProcessors=new CopyOnWriteArraySet<>();

    @Override
    public Object advice(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {

        if(nestedResourceHandler){
            //Late implementation of nesting processing
        }
        final boolean proxyStatus = AdviceContext.currentProxyStatus();
        if(proxyStatus){
            return applyAdviceAfterHandler(obj,args,mp);
        }
        return applyAdviceBeforeHandler(obj,method,args);
    }


    public Object applyAdviceAfterHandler(Object obj, Object[] args, MethodProxy mp) throws Throwable{
        Object returnObject=null;
        for(AdviceReplacerProcessor replacerProcessor : getAdviceReplacerProcessors()){
            returnObject =  replacerProcessor.afterAdvice(obj,args,mp);
        }
        return returnObject;
    }


    public Object applyAdviceBeforeHandler(Object obj, Method method, Object[] args) throws Throwable{
        Object returnObject=null;
        for(AdviceReplacerProcessor replacerProcessor : getAdviceReplacerProcessors()){
            returnObject =  replacerProcessor.beforeAdvice(obj, method, args);
        }
       return returnObject;
    }

    public Set<AdviceReplacerProcessor> getAdviceReplacerProcessors() {
        return adviceReplacerProcessors;
    }

    public void addAdviceReplacerProcessor(AdviceReplacerProcessor adviceReplacerProcessor){
        this.adviceReplacerProcessors.add(adviceReplacerProcessor);
    }

    public void setNestedResourceHandler(boolean nestedResourceHandler) {
        this.nestedResourceHandler = nestedResourceHandler;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (CollectionUtils.isEmpty(this.adviceReplacerProcessors)) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
            synchronized (adviceReplacerProcessors) {
                final String[] beanNamesForType = listableBeanFactory.getBeanNamesForType(AdviceReplacerProcessor.class, true, false);
                Stream.of(beanNamesForType).forEach(beanName -> addAdviceReplacerProcessor(listableBeanFactory.getBean(beanName, AdviceReplacerProcessor.class)));
            }
        }
    }
}

