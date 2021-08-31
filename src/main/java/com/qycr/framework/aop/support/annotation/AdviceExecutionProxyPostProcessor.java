package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.exception.AdviceExecutionProxyException;
import com.qycr.framework.aop.support.proxy.DefaultProxyFactory;
import com.qycr.framework.aop.support.proxy.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Deprecated
public class AdviceExecutionProxyPostProcessor  extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware, EmbeddedValueResolverAware, ResourceLoaderAware, SmartInitializingSingleton{



    private StringValueResolver valueResolver;

    private ConfigurableBeanFactory beanFactory;

    private ClassLoader tmpClassLoader;

    private ProxyFactory proxyFactory;

    private final Set<String> beanNames=new CopyOnWriteArraySet<>();

    private final Map<String,Class<?>> cacheClassCondition=new ConcurrentHashMap<>();

    private final Map<String,List<Method>> cacheMethodCondition=new ConcurrentHashMap<>();

    private final AntPathMatcher antPathMatcher=new AntPathMatcher();

    private ResourcePatternResolver resourcePatternResolver;


    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {

        List<Method> methodCondition = new ArrayList<>();
        final boolean hasAnnotation = AnnotatedElementUtils.hasAnnotation(beanClass, Advice.class);

        ReflectionUtils.doWithMethods( beanClass,method -> methodCondition.add(method),method -> AnnotatedElementUtils.hasAnnotation(method,Advice.class));

        if( hasAnnotation || !CollectionUtils.isEmpty(methodCondition)){
            final Class<?> proxyClass = proxyFactory.getProxyClass(beanClass);
            final AbstractBeanDefinition beanDefinition =(AbstractBeanDefinition) beanFactory.getMergedBeanDefinition(beanName);
            beanDefinition.setBeanClass(proxyClass);
            addCacheAdvice(beanName,beanClass,methodCondition);
        }
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if(beanNames.contains(beanName)) {
            DefaultProxyFactory defaultProxyFactory = (DefaultProxyFactory) this.proxyFactory;
            defaultProxyFactory.populateBean(bean, beanName);
        }
        return super.postProcessAfterInstantiation(bean, beanName);
    }


    @Override
    public void afterSingletonsInstantiated() {
        clearCacheAdvice();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if(!(beanFactory instanceof ConfigurableBeanFactory)){
            throw new AdviceExecutionProxyException(String.format("AdviceExecutionProxyPostProcessor requires a ConfigurableBeanFactory: %s", beanFactory));
        }
        this.beanFactory=(ConfigurableBeanFactory) beanFactory;
        this.proxyFactory=new DefaultProxyFactory(this.beanFactory);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
           this.valueResolver=resolver;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
         this.tmpClassLoader=resourceLoader.getClassLoader();
         this.resourcePatternResolver=new PathMatchingResourcePatternResolver(tmpClassLoader);
    }

    private void addCacheAdvice(String beanName,Class<?> beanClass,List<Method> methodCondition){
        beanNames.add(beanName);
        cacheClassCondition.putIfAbsent(beanName,beanClass);
        cacheMethodCondition.put(beanName,methodCondition);
    }

    private void clearCacheAdvice(){
        beanNames.clear();
        cacheClassCondition.clear();
        cacheMethodCondition.clear();
    }
}
