package com.qycr.framework.aop.support.annotation;

import com.qycr.framework.aop.support.config.AdviceMethodExecutionInvoker;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class MethodInvokerAdviceExecutionInterceptor extends AbstractAdviceExecutionInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        AdviceMethodExecutionInvoker invoker=()->{
            try {
                return invocation.proceed();
            } catch (Throwable e) {
                throw e;
            }
        };
        return execute(invoker,invocation.getThis(),invocation.getMethod(),invocation.getArguments());
    }

    @Override
    protected Object execute(AdviceMethodExecutionInvoker invoker, Object target, Method method, Object[] args) throws Throwable {
        try {
            AdviceContext.setCurrentProxyStatus(Boolean.TRUE);
            return invoker.invoke();
        }finally {
            AdviceContext.setCurrentProxyStatus(Boolean.FALSE);
        }
    }
}
