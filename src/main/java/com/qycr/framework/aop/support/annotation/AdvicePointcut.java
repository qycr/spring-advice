package com.qycr.framework.aop.support.annotation;

import org.springframework.aop.Pointcut;

public interface AdvicePointcut extends Pointcut {

    default public void setExpression(String ...expression){};

    default public String[] getExpression(){return null;}


}
