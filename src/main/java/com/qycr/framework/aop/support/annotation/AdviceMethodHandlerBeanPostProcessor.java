package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.exception.AdviceOverrideLoadException;
import com.qycr.framework.aop.support.replacer.AdviceMethodExecutionReplacer;
import com.qycr.framework.aop.support.replacer.AdviceOverride;
import com.qycr.framework.aop.support.replacer.processor.AdviceReplacerProcessor;
import com.qycr.framework.aop.support.replacer.processor.SimpleAdviceReplacerProcessor;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.MethodOverrides;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

public class AdviceMethodHandlerBeanPostProcessor  extends AbstractAdviceMethodPostProcessor implements MergedBeanDefinitionPostProcessor, BeanFactoryAware, SmartInitializingSingleton {


    private ConfigurableBeanFactory beanFactory;

    private final Set<LocalMethod> replaceMethods = new CopyOnWriteArraySet<>();


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new AdviceOverrideLoadException(String.format("AdviceMethodHandlerBeanPostProcessor requires a ConfigurableBeanFactory: %s", beanFactory));
        }
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {

        Set<AdviceOverride> adviceOverrides = new HashSet<>();

        processAdviceMethod(adviceOverrides, beanClass, beanName);

        if (!CollectionUtils.isEmpty(adviceOverrides)) {
            final RootBeanDefinition beanDefinition = (RootBeanDefinition) this.beanFactory.getMergedBeanDefinition(beanName);
            final MethodOverrides overrides = new MethodOverrides();
            overrides.getOverrides().addAll(adviceOverrides);
            beanDefinition.getMethodOverrides().addOverrides(overrides);
        }

        return null;
    }

    private void processAdviceMethod(Set<AdviceOverride> adviceOverrides, Class<?> beanClass, String beanName) {
        ReflectionUtils.doWithMethods(beanClass, method -> {
            registerAdviceOverride(adviceOverrides, method.getName(), method.getParameterTypes());
            registerLocalMethod(beanClass, beanName, method.getParameterTypes(), method.getName());
        }, method -> matches(beanClass, method));

    }


    private void registerAdviceOverride(Set<AdviceOverride> replaceOverrides, String methodName, Class[] parameterTypes) {
        final AdviceOverride adviceOverride = new AdviceOverride(methodName, AdviceMethodExecutionReplacer.COMMON_ADVICE_REPLACER, true);
        Stream.of(parameterTypes).filter(Objects::nonNull).forEach(type -> adviceOverride.addTypeIdentifier(type.getName()));
        replaceOverrides.add(adviceOverride);
    }

    private void registerLocalMethod(Class<?> beanClass, String beanName, Class[] parameterType, String methodName) {
        final LocalMethod localMethod = new LocalMethod(beanClass, beanName, parameterType, methodName);
        replaceMethods.add(localMethod);
    }

    @Override
    public void afterSingletonsInstantiated() {
        final AdviceMethodExecutionReplacer adviceMethodExecutionReplacer = this.beanFactory.getBean(AdviceMethodExecutionReplacer.COMMON_ADVICE_REPLACER, AdviceMethodExecutionReplacer.class);
        final Set<AdviceReplacerProcessor> adviceReplacerProcessors = adviceMethodExecutionReplacer.getAdviceReplacerProcessors();
        adviceReplacerProcessors.stream().forEach(adviceReplacerProcessor -> {
            if (adviceReplacerProcessor instanceof SimpleAdviceReplacerProcessor) {
                SimpleAdviceReplacerProcessor simpleAdviceReplacerProcessor = (SimpleAdviceReplacerProcessor) adviceReplacerProcessor;
                this.replaceMethods.stream().filter(Objects::nonNull).forEach(localMethod -> simpleAdviceReplacerProcessor.registerBean(localMethod.methodName, localMethod.beanType, localMethod.parameterType, localMethod.beanName));
            }
        });
        this.replaceMethods.clear();
    }


    private class LocalMethod {

        protected Class<?> beanType;

        protected String beanName;

        protected Class<?>[] parameterType;

        private String methodName;

        public LocalMethod(Class<?> beanType, String beanName, Class[] parameterType, String methodName) {
            this.beanType = beanType;
            this.beanName = beanName;
            this.parameterType = parameterType;
            this.methodName = methodName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocalMethod that = (LocalMethod) o;
            return Objects.equals(beanType, that.beanType) && Objects.equals(beanName, that.beanName) && Arrays.equals(parameterType, that.parameterType) && Objects.equals(methodName, that.methodName);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(beanType, beanName, methodName);
            result = 31 * result + Arrays.hashCode(parameterType);
            return result;
        }
    }

    private class MethodCache {

        private String methodName;

        private Class[] parameterType;

        private MethodCache(String methodName, Class[] parameterType) {
            this.methodName = methodName;
            this.parameterType = parameterType;
        }

        public MethodCache(Method method) {
            this.methodName = method.getName();
            this.parameterType = method.getParameterTypes();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodCache that = (MethodCache) o;
            return Objects.equals(methodName, that.methodName) && Arrays.equals(parameterType, that.parameterType);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(methodName);
            result = 31 * result + Arrays.hashCode(parameterType);
            return result;
        }

        public boolean matches(String methodName) {
            return this.matches(methodName, null);
        }

        public boolean matches(String methodName, Class[] parameterType) {
            final MethodCache other = new MethodCache(methodName, parameterType);
            return equals(other);
        }
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
     //NOOP...
    }

    public boolean matches(Class<?> clazz, Method method) {

        final boolean matches = pointcut.getClassFilter().matches(clazz);
        boolean match = false;
        if (matches) {
            final MethodMatcher methodMatcher = pointcut.getMethodMatcher();

            if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
                match = ((IntroductionAwareMethodMatcher) methodMatcher).matches(method, clazz, false);
            } else {
                match = methodMatcher.matches(method, clazz);
            }
        }
        return match;
    }
}