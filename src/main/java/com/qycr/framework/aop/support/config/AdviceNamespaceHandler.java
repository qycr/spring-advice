package com.qycr.framework.aop.support.config;


import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class AdviceNamespaceHandler extends NamespaceHandlerSupport {


    @Override
    public void init() {
        this.registerBeanDefinitionParser("advice-driven",new AnnotationDrivenAdviceBeanDefinitionParser());
    }
}
