package com.qycr.framework.aop.support.replacer.processor;

import com.qycr.framework.aop.support.annotation.AdviceContext;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleAdviceReplacerProcessor implements AdviceReplacerProcessor, BeanFactoryAware, DisposableBean{

    private static final String CGLIB_TAG = "BySpringCGLIB";

    private ConfigurableListableBeanFactory beanFactory;

    private final AtomicBoolean startup = new AtomicBoolean(true);

    private final Map<LookMethodCache, Object> proxyBean = new ConcurrentHashMap<>(64);

    private final Map<Class<?>, Class<?>> classType = new ConcurrentHashMap<>(64);


    @Override
    public Object beforeAdvice(Object obj, Method method, Object[] args) throws Throwable {

        Object proxy = null;
        if (startup.get()) {//The exception is no longer handled. If you are interested, you can listen.
            try {
                proxy = AopContext.currentProxy();
            } catch (Exception e) {
                startup.set(false);
                //ignore
            }
        }
        if (Objects.nonNull(proxy)) {
            return method.invoke(proxy, args);
        }
        final Class<?> type = extractClass(obj);
        final LookMethodCache lookMethodCache = new LookMethodCache(method, type);
        Object bean = proxyBean.get(lookMethodCache);
        if (Objects.isNull(bean)) {
            String shortClassName = ClassUtils.getShortName(type.getName());
            bean = beanFactory.getBean(Introspector.decapitalize(shortClassName));
            addBean(lookMethodCache, bean);
        }
        if (bean instanceof String) {
            bean = beanFactory.getBean((String) bean);
            addBean(lookMethodCache, bean);
        }
        if (AopUtils.isJdkDynamicProxy(bean)) {
            //Can be tiny cached
            method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
        }
        return method.invoke(bean, args);

    }

    private Class<?> extractClass(Object obj) {

        Class<?> type, currentType = obj.getClass(), originalType = obj.getClass();

        type = classType.get(currentType);

        if (Objects.nonNull(type)) {
            return type;
        }
        synchronized (classType) {
            type = classType.get(currentType);
            if (Objects.nonNull(type)) {
                return type;
            }

            for (; ; ) {
                if (currentType.getName().contains(CGLIB_TAG)) {
                    currentType = currentType.getSuperclass();
                } else {
                    break;
                }
            }
            classType.put(originalType, currentType);
        }
        return currentType;
    }


    @Override
    public Object afterAdvice(Object obj, Object[] args, MethodProxy mp) throws Throwable {
        AdviceContext.setCurrentProxyStatus(Boolean.FALSE);
        return mp.invokeSuper(obj, args);
    }

    private synchronized void addBean(LookMethodCache lookMethodCache, Object bean) {
        this.proxyBean.put(lookMethodCache, bean);
    }

    public void addBean(String methodName, Class<?> beanType, Class<?>[] parameterType, Object bean) {
        addBean(new LookMethodCache(methodName, beanType, parameterType), bean);
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }


    @Override
    public void destroy() throws Exception {
        if (!CollectionUtils.isEmpty(proxyBean)) {
            proxyBean.clear();
        }
    }

    static class LookMethodCache {

        private String methodName;

        private Class<?> type;

        private Class<?>[] types;

        public LookMethodCache(Method method, Class<?> type) {
            this.methodName = method.getName();
            this.type = type;
            this.types = method.getParameterTypes();
        }

        public LookMethodCache(String methodName, Class<?> type, Class<?>[] types) {
            this.methodName = methodName;
            this.type = type;
            this.types = types;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LookMethodCache that = (LookMethodCache) o;
            return Objects.equals(methodName, that.methodName) && Objects.equals(type, that.type) && Arrays.equals(types, that.types);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(methodName, type);
            result = 31 * result + Arrays.hashCode(types);
            return result;
        }
    }


}
