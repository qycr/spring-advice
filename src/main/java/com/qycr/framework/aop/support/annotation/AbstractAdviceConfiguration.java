package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.config.AdviceTypeSelector;
import com.qycr.framework.aop.support.config.ConditionOnDefinition;
import com.qycr.framework.aop.support.config.LocalProxyBeanFactoryExecutionProcessor;
import com.qycr.framework.aop.support.engine.inject.AdviceProxyAwarePostProcessor;
import com.qycr.framework.aop.support.replacer.AdviceMethodExecutionReplacer;
import com.qycr.framework.aop.support.replacer.processor.SimpleAdviceReplacerProcessor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import java.util.Objects;
import java.util.function.Supplier;

@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public  abstract class AbstractAdviceConfiguration implements ImportAware , BeanFactoryAware , EnvironmentAware {

    public static final String METHOD_INVOKER_ADVICE="internalMethodInvokerAdviceExecutionPostProcessor";

    protected AnnotationAttributes attributes;

    protected AnnotationAttributes adviceFilter;

    protected String[] expression;

    protected BeanFactory beanFactory;

    protected Environment environment;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.attributes = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableAdvice.class.getName(), false));
        if (Objects.isNull(this.attributes)) {
            throw new IllegalArgumentException("@EnableAdvice is not present on importing class " + importMetadata.getClassName());
        }
        this.adviceFilter = this.attributes.getAnnotation("advice");
        this.expression = this.adviceFilter.getStringArray("expression");
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionOnDefinition({LocalProxyBeanFactoryExecutionProcessor.class})
    public static LocalProxyBeanFactoryExecutionProcessor localProxyBeanFactoryExecutionProcessor() {
        return new LocalProxyBeanFactoryExecutionProcessor();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionOnDefinition(AdviceProxyAwarePostProcessor.class)
    public static AdviceProxyAwarePostProcessor adviceProxyAwarePostProcessor() {
        return new AdviceProxyAwarePostProcessor();
    }


    @Bean(AdviceMethodExecutionReplacer.COMMON_ADVICE_REPLACER)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionOnDefinition(AdviceMethodExecutionReplacer.class)
    public  AdviceMethodExecutionReplacer adviceMethodExecutionReplacer() {
        return new AdviceMethodExecutionReplacer();
    }


    @Bean(METHOD_INVOKER_ADVICE)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionOnDefinition(classes =MethodInvokerAdviceExecutionPostProcessor.class )
    public  MethodInvokerAdviceExecutionPostProcessor methodInvokerAdviceExecutionPostProcessor() {
        final MethodInvokerAdviceExecutionPostProcessor processor = new MethodInvokerAdviceExecutionPostProcessor(advice(), pointcut());
        if(this.attributes.getBoolean("exposeProxy")){
            processor.setExposeProxy(true);
        }
        return  processor;
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionOnDefinition(AdviceMethodHandlerBeanPostProcessor.class)
    public  AdviceMethodHandlerBeanPostProcessor adviceMethodHandlerBeanPostProcessor() {
        AdviceMethodHandlerBeanPostProcessor adviceMethodHandlerBeanPostProcessor =  new AdviceMethodHandlerBeanPostProcessor();
        adviceMethodHandlerBeanPostProcessor.setPointcut(AdviceTypeSelector.INSTANCE.buildPointcut(pointcut()));
        return adviceMethodHandlerBeanPostProcessor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionOnDefinition(SimpleAdviceReplacerProcessor.class)
    public  SimpleAdviceReplacerProcessor simpleAdviceReplacerProcessor(){
        return new SimpleAdviceReplacerProcessor();
    }

    public String[] expression(){
        String[] originalExpression=this.expression;
        for(int i = 0; i < originalExpression.length; i ++){
            originalExpression[i] = this.environment.resolvePlaceholders(originalExpression[i]);
        }
        return originalExpression;
    }
    protected abstract Pointcut[] pointcut();


    protected  Supplier<Advice> advice(){
        return ()->AdviceNoop.INSTANCE;
    }

    public static class AdviceNoop{

        private AdviceNoop(){

        }

        public static final Advice INSTANCE=new Advice(){};
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory =  beanFactory;
    }
}
