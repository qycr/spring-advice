package com.qycr.framework.aop.support.engine.inject;

import com.qycr.framework.aop.support.config.ConditionMatcher;
import com.qycr.framework.aop.support.engine.aware.AdviceAware;
import com.qycr.framework.aop.support.exception.AdviceAccessTargetException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/**
 * <h3>Notice:</h3>
 * <p>
 *   Already handled as much as possible, there is still a defect,
 *   and the current object is initialized to call the NPE.
 *   like {@link PostConstruct} or {@link InitializingBean} or other initialize,
 *   The call is not processed.
 * </p>
 */
@Slf4j
public class AdviceProxyAwarePostProcessor implements BeanPostProcessor,MergedBeanDefinitionPostProcessor, SmartInitializingSingleton, ApplicationContextAware, Ordered {

    private final Map<String, Object> cacheMapper = new ConcurrentHashMap<>();

    private boolean enableSingleton = false;

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        final ConditionMatcher conditionMatcher = ConditionMatcher.nonMatcher();
        processAware(conditionMatcher, bean, beanName);
        processInjectAdvised(conditionMatcher, bean, beanName);
        if (conditionMatcher.isMatch()) {
            log.info(conditionMatcher.message());
            if (enableSingleton)
                cacheMapper.put(beanName, bean);
        }
        return bean;

    }

    private void processAware(ConditionMatcher conditionMatcher, Object bean, String beanName) {
        if (bean instanceof Advised && bean instanceof AdviceAware) {
            AdviceAware adviceAware = (AdviceAware) bean;
            adviceAware.setAdvice((Advised) bean);
            conditionMatcher.toMatch().matcherMessage(String.format("current bean is Advised, beanName is %s  current object implement AdviceAware", beanName));
        }
    }

    private void processInjectAdvised(ConditionMatcher conditionMatcher, Object bean, String beanName) throws BeansException {

        if (bean instanceof Advised) {
            //It is only processed once, the ideal is that there is only one, and there is no point in having multiple.
            if (conditionMatcher.isMatch()) return;

            Advised advised = (Advised) bean;

            Object target;

            try {
                   target = advised.getTargetSource().getTarget();
             } catch (Exception e) {
                  throw new AdviceAccessTargetException(e);
            }

            final Class<?> targetClass = AopUtils.getTargetClass(bean);

            ReflectionUtils.doWithFields(targetClass, field -> {
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, target, bean);
                conditionMatcher.toMatch().matcherMessage(String.format("current bean is Advised, beanName is (%s) bean type is (%s)  current object fieldName (%s) exist @InjectAdvised", beanName, targetClass, field.getName()));
            }, field -> AnnotatedElementUtils.hasAnnotation(field, InjectAdvised.class));


            if (conditionMatcher.isMatch()) return;

            ReflectionUtils.doWithMethods(targetClass, method -> {
                ReflectionUtils.makeAccessible(method);
                ReflectionUtils.invokeMethod(method, target, bean);
                conditionMatcher.toMatch().matcherMessage(String.format("current bean is Advised, beanName is (%s) bean type is (%s)  current object methodName (%s) exist @InjectAdvised", beanName, targetClass, method.getName()));
            }, method -> AnnotatedElementUtils.hasAnnotation(method, InjectAdvised.class));
        }

    }


    @Override
    public void afterSingletonsInstantiated() {
        if (enableSingleton) {
            Stream.of(applicationContext.getBeanDefinitionNames()).forEach(beanName -> {
                if (cacheMapper.containsKey(beanName)) {
                    return;
                }
                final Object bean = applicationContext.getBean(beanName);
                //Only Aware is processed here.
                processAware(null, bean, beanName);
            });
            cacheMapper.clear();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        //NOOP
    }

    public void setEnableSingleton(boolean enableSingleton) {
        this.enableSingleton = enableSingleton;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
