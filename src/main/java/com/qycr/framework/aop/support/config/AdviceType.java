package com.qycr.framework.aop.support.config;


public enum AdviceType {

    //default @Advice or custom Annotation
    ANNOTATION,

    // default JdkRegexpMethodPointcut
    REGEX,

    //default AspectJExpressionPointcut
    ASPECTJ,

    /**
     *  When switching to a custom entry point to collect connection points,
     *  and it is clear that the expression (expressionType=true) has been processed,
     *  the side needs to provide a constructor with a parameter as an expression,
     *  or provide setExpression(String[] expression);
     */
    @Deprecated
    CUSTOM,


    CONSUMER;



}
