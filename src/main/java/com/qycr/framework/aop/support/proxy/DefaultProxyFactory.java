package com.qycr.framework.aop.support.proxy;

import com.qycr.framework.aop.support.annotation.Advice;
import com.qycr.framework.aop.support.annotation.AdviceContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.*;

import java.lang.reflect.Method;

public class DefaultProxyFactory implements ProxyFactory{

    private static final int NO_OP=0;

    private static final int ADVICE=1;

    private final BeanFactory beanFactory;

    private static final  Class<?>[] CALLBACK_TYPES=new Class[]{
            NoOp.class,
            MethodAdviceExecutionMethodInterceptor.class
    };

    public DefaultProxyFactory(BeanFactory beanFactory){
        this.beanFactory=beanFactory;
    }

    @Override
    public <S> Class<?> getProxyClass(Class<S> s) {
        return createSubclass(s);
    }

    @Override
    public <S> S getBean(Class<S> subclass, String beanName) {
        return (S)instantiate(subclass,beanName);
    }

    private <S> Class<?> createSubclass(Class<S> s) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(s);
        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
        enhancer.setCallbackFilter(new MethodAdviceExecutionCallbackFilter());
        enhancer.setCallbackTypes(CALLBACK_TYPES);
        return enhancer.createClass();
    }

    public Object instantiate(Class<?> subclass,String beanName) {
        Object instance=BeanUtils.instantiateClass(subclass);
        return populateBean(instance,beanName);
    }

    public Object populateBean(Object instance,String beanName) {

        if(!(instance instanceof Factory)){
            return instance;
        }
        Factory factory = (Factory) instance;
        factory.setCallbacks(new Callback[] {
                NoOp.INSTANCE,
                new MethodAdviceExecutionMethodInterceptor(this.beanFactory,beanName)
        });
        return instance;
    }

    private static class MethodAdviceExecutionMethodInterceptor implements MethodInterceptor{

        private final BeanFactory beanFactory;

        private final String beanName;

        public MethodAdviceExecutionMethodInterceptor(BeanFactory beanFactory,String beanName){
            this.beanFactory=beanFactory;
            this.beanName=beanName;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

            final boolean currentProxyStatus = AdviceContext.currentProxyStatus();
            if(currentProxyStatus){
                return methodProxy.invokeSuper(obj,args);
            }
            return method.invoke(beanFactory.getBean(beanName),args);
        }
    }

    private static class MethodAdviceExecutionCallbackFilter implements CallbackFilter{



        @Override
        public int accept(Method method) {
            if(!method.isAnnotationPresent(Advice.class)){
                return NO_OP;
            }
            return ADVICE;
        }
    }


}
