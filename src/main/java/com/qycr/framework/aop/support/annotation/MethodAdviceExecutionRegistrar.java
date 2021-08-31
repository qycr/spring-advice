package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.config.LocalProxyBeanFactoryExecutionProcessor;
import com.qycr.framework.aop.support.replacer.AdviceMethodExecutionReplacer;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

@Deprecated
public class MethodAdviceExecutionRegistrar implements ImportBeanDefinitionRegistrar {


    private final BeanNameGenerator beanNameGenerator=new DefaultBeanNameGenerator();


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registrarMethodInvokerAdviceExecutionPostProcessor(importingClassMetadata,registry);
        registrarLocalProxyBeanFactoryExecutionProcessor(registry);
        registrarAdviceMethodHandlerBeanPostProcessor(registry);
        registrarAdviceMethodExecutionReplacer(registry);
    }

    public void registrarMethodInvokerAdviceExecutionPostProcessor(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry){
        final AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableAdvice.class.getName()));
        if(Objects.isNull(attributes)){
            return;
        }
        final Class<?>[] annotationTypes = attributes.getClassArray("annotationTypes");
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MethodInvokerAdviceExecutionPostProcessor.class);
        if(annotationTypes.length>0) {
            builder.addPropertyValue("annotationTypes", annotationTypes);
        }
        final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        final String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
        registry.registerBeanDefinition(beanName,beanDefinition);

    }

    public void registrarLocalProxyBeanFactoryExecutionProcessor(BeanDefinitionRegistry registry){
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(LocalProxyBeanFactoryExecutionProcessor.class);
        final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        final String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
        registry.registerBeanDefinition(beanName,beanDefinition);

    }

    public void registrarAdviceMethodHandlerBeanPostProcessor(BeanDefinitionRegistry registry){
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AdviceMethodHandlerBeanPostProcessor.class);
        final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        final String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
        registry.registerBeanDefinition(beanName,beanDefinition);

    }

    public void registrarAdviceMethodExecutionReplacer(BeanDefinitionRegistry registry){
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AdviceMethodExecutionReplacer.class);
        final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(AdviceMethodExecutionReplacer.COMMON_ADVICE_REPLACER,beanDefinition);

    }


    
}
