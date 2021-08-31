package com.qycr.framework.aop.support.replacer;

import org.springframework.beans.factory.support.MethodOverride;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AdviceOverride extends MethodOverride {


    private final String adviceBeanName;

    private final List<String> typeIdentifiers = new ArrayList<>();

    private final boolean methodProxy;



    public AdviceOverride(String methodName, String adviceBeanName,boolean methodProxy) {
        super(methodName);
        Assert.notNull(adviceBeanName, "Advice replacer bean name must not be null");
        this.adviceBeanName = adviceBeanName;
        this.methodProxy=methodProxy;
    }

    public boolean isMethodProxy() {
        return methodProxy;
    }

    public String getAdviceBeanName() {
        return this.adviceBeanName;
    }


    public void addTypeIdentifier(String identifier) {
        this.typeIdentifiers.add(identifier);
    }


    @Override
    public boolean matches(Method method) {
        if (!method.getName().equals(getMethodName())) {
            return false;
        }
        if (!isOverloaded()) {
            // Not overloaded: don't worry about arg type matching...
            return true;
        }
        // If we get here, we need to insist on precise argument matching...
        if (this.typeIdentifiers.size() != method.getParameterCount()) {
            return false;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < this.typeIdentifiers.size(); i++) {
            String identifier = this.typeIdentifiers.get(i);
            if (!parameterTypes[i].getName().contains(identifier)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof AdviceOverride) || !super.equals(other)) {
            return false;
        }
        AdviceOverride that = (AdviceOverride) other;
        return (ObjectUtils.nullSafeEquals(this.adviceBeanName, that.adviceBeanName) &&
                ObjectUtils.nullSafeEquals(this.methodProxy, that.methodProxy) &&
                ObjectUtils.nullSafeEquals(this.typeIdentifiers, that.typeIdentifiers));
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.adviceBeanName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.methodProxy);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.typeIdentifiers);
        return hashCode;
    }

    @Override
    public String toString() {
        return "Advice override for method '" + getMethodName() + "'";
    }

}
