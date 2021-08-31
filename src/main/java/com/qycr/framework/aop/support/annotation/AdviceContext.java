package com.qycr.framework.aop.support.annotation;

import org.springframework.core.NamedThreadLocal;


public class AdviceContext {

    private static final ThreadLocal<Boolean> currentProxy = new NamedThreadLocal<Boolean>("current advice status"){
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };
    private static final ThreadLocal<AdviceInternalResource> adviceInternalResource = new NamedThreadLocal<>("current advice resource status");


    private AdviceContext() {

    }

    public static boolean currentProxyStatus() throws IllegalStateException {
        return currentProxy.get();
    }

    public static boolean setCurrentProxyStatus( boolean status) {
        boolean old = currentProxy.get();
        if (!old || status) {
            currentProxy.set(status);
        }
        else {
            currentProxy.remove();
        }
        return old;
    }


    public static AdviceInternalResource currentProxyResourceStatus() throws IllegalStateException {
        return adviceInternalResource.get();
    }

    public static void setCurrentProxyStatus( AdviceInternalResource resource) {
        adviceInternalResource.set(resource);
    }
    public static void currentProxyStatusRemove() {
        adviceInternalResource.remove();
    }

}
