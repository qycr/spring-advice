package com.qycr.framework.aop.support.proxy;

public interface ProxyFactory {

    public <S> Class<?> getProxyClass(Class<S>  s);

    public <S> S getBean(Class<S> subclass,String beanName);

}
