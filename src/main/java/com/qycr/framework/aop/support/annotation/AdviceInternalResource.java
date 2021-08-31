package com.qycr.framework.aop.support.annotation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class AdviceInternalResource  implements Serializable {

    private boolean status;

    private transient Method method;

    private transient Object target;

    private transient Object[] parameters;

    public AdviceInternalResource(Method method, Object target, Object[] parameters) {
        this.method = method;
        this.target = target;
        this.parameters = parameters;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdviceInternalResource that = (AdviceInternalResource) o;
        return Objects.equals(method, that.method) && Objects.equals(target, that.target) && Arrays.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash( method, target);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }
}




